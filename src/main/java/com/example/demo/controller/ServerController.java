package com.example.demo.controller;

import com.example.demo.ecs.EntityPlayer;
import com.example.demo.ecs.ComponentPosition;
import com.example.demo.event.EventGreeting;
import com.example.demo.logging.MyMarkers;
import com.example.demo.persistence.ComponentDbPosition;
import com.example.demo.persistence.EntityDbPlayer;
import lombok.extern.slf4j.Slf4j;
import org.gamedo.Gamedo;
import org.gamedo.annotation.Tick;
import org.gamedo.ecs.interfaces.IEntity;
import org.gamedo.gameloop.components.entitymanager.interfaces.IGameLoopEntityManager;
import org.gamedo.gameloop.functions.IGameLoopEntityManagerFunction;
import org.gamedo.gameloop.functions.IGameLoopEventBusFunction;
import org.gamedo.gameloop.functions.IGameLoopTickManagerFunction;
import org.gamedo.gameloop.interfaces.IGameLoop;
import org.gamedo.persistence.core.DbDataMongoTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@Slf4j
@RestController("/")
public class ServerController {

    private static final AtomicBoolean LoginSwitch = new AtomicBoolean(false);
    private static final String IdPrefix = "gamedo-";
    private static final AtomicLong IdCounter = new AtomicLong(1);
    @Value("${app.entity.count}")
    private int entityCount = 1000;

    private final DbDataMongoTemplate dbDataMongoTemplate;
    private final MongoTemplate mongoTemplate;

    public ServerController(DbDataMongoTemplate dbDataMongoTemplate, MongoTemplate mongoTemplate) {
        this.dbDataMongoTemplate = dbDataMongoTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/init")
    public Map<Boolean, Long> init() {

        LoginSwitch.set(false);
        IdCounter.set(1);
        mongoTemplate.dropCollection(EntityDbPlayer.class);

        final CompletableFuture<Boolean>[] futures = IntStream.rangeClosed(1, entityCount)
                .mapToObj(i -> IdPrefix + i)
                .map(id -> Gamedo.io().selectNext().submit(gameLoop -> this.generatePlayer(gameLoop, id)))
                .toArray(CompletableFuture[]::new);

        final Map<Boolean, Long> map = CompletableFuture.allOf(futures)
                .thenApply(v -> Arrays.stream(futures)
                        .map(CompletableFuture::join)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))).join();

        IdCounter.set(1);
        return map;
    }

    @RequestMapping("/login")
    public boolean serverStart() {
        LoginSwitch.set(true);
        Gamedo.single().selectNext().submit(IGameLoopTickManagerFunction.register(this));

        return true;
    }

    @RequestMapping("/switch")
    public boolean switchLogin() {
        LoginSwitch.set(!LoginSwitch.get());

        return LoginSwitch.get();
    }

    @RequestMapping("/logout")
    public List<Boolean> serverShutdown() {

        LoginSwitch.set(false);
        final CompletableFuture<List<Boolean>> future = Gamedo.worker().submitAll(this::unregisterEntity);

        return future.join();
    }

    private boolean generatePlayer(IGameLoop gameLoop, String entityId) {

        final EntityDbPlayer entityDbPlayer = new EntityDbPlayer(entityId, null);
        entityDbPlayer.addComponentDbData(new ComponentDbPosition(1, 1));

        log.info(MyMarkers.Load, "save player:{}", entityDbPlayer);
        dbDataMongoTemplate.save(entityDbPlayer);

        return true;
    }

    private boolean unregisterEntity(IGameLoop gameLoop) {

        final Optional<IGameLoopEntityManager> entityManager = gameLoop.getComponent(IGameLoopEntityManager.class);

        return entityManager.map(manager -> {

            new HashMap<>(manager.getEntityMap())
                    .forEach((key, value) ->
                            manager.unregisterEntity(key)
                                    .ifPresent(i -> log.info("模拟下线成功, entity:{}", i)));
            return true;
        }).orElse(false);
    }

    @Tick(delay = 0, tick = 1, timeUnit = TimeUnit.SECONDS)
    private boolean simulateLogin(Long currentTime, Long lastTriggerTime)
    {
        if (!LoginSwitch.get() || IdCounter.get() > entityCount) {
            return false;
        }

        log.info(MyMarkers.Login, "模拟一批登录");
        //每秒最多登录100个玩家
        for (int i = 1; i < 100 && IdCounter.get() <= entityCount; i++) {
            final String id = IdPrefix + IdCounter.getAndIncrement();
            final EventGreeting event = new EventGreeting("hello " + id);
            final IGameLoop worker = Gamedo.worker().selectNext();

            //模拟使用io线程加载entity
            CompletableFuture.supplyAsync(() -> loadEntity(id), Gamedo.io())
                    //加载完毕，将之安全发布到worker线程
                    .thenAccept(entity -> worker.submit(IGameLoopEntityManagerFunction.registerEntity(entity)))
                    //注册完毕，向其发送消息
                    .thenAccept(s -> worker.submit(IGameLoopEventBusFunction.post(event)));
        }

        return true;
    }

    private IEntity loadEntity(String id) {

        final EntityDbPlayer player = mongoTemplate.findById(id, EntityDbPlayer.class);

        EntityPlayer entity = new EntityPlayer(id, player);
        entity.addComponent(ComponentPosition.class, new ComponentPosition(entity));

        log.info(MyMarkers.Load, "加载entity, entity:{}", entity);
        return entity;
    }
}
