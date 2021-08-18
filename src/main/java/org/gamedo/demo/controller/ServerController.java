package org.gamedo.demo.controller;

import org.gamedo.demo.ecs.ComponentPosition;
import org.gamedo.demo.ecs.EntityPlayer;
import org.gamedo.demo.event.EventGreeting;
import org.gamedo.demo.logging.MyMarkers;
import org.gamedo.demo.persistence.ComponentDbPosition;
import org.gamedo.demo.persistence.EntityDbPlayer;
import lombok.extern.log4j.Log4j2;
import org.gamedo.Gamedo;
import org.gamedo.annotation.Tick;
import org.gamedo.ecs.interfaces.IEntity;
import org.gamedo.gameloop.components.entitymanager.interfaces.IGameLoopEntityManager;
import org.gamedo.gameloop.interfaces.IGameLoop;
import org.gamedo.logging.Markers;
import org.gamedo.persistence.GamedoMongoTemplate;
import org.gamedo.util.function.IGameLoopEntityManagerFunction;
import org.gamedo.util.function.IGameLoopEventBusFunction;
import org.gamedo.util.function.IGameLoopTickManagerFunction;
import org.springframework.beans.factory.annotation.Value;
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

@SuppressWarnings({"unused", "FieldMayBeFinal"})
@Log4j2
@RestController("/")
public class ServerController {

    private static final AtomicBoolean LoginSwitch = new AtomicBoolean(false);
    private static final String IdPrefix = "gamedo-";
    private static final AtomicLong IdCounter = new AtomicLong(1);
    @Value("${app.entity.count}")
    private int entityCount = 1000;

    private final GamedoMongoTemplate gamedoMongoTemplate;

    public ServerController(GamedoMongoTemplate gamedoMongoTemplate) {
        this.gamedoMongoTemplate = gamedoMongoTemplate;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/init")
    public Map<Boolean, Long> init() {

        LoginSwitch.set(false);
        IdCounter.set(1);
        gamedoMongoTemplate.dropCollection(EntityDbPlayer.class);

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

        future.thenAccept(list -> IdCounter.set(1));

        return future.join();
    }

    private boolean generatePlayer(IGameLoop gameLoop, String entityId) {

        final EntityDbPlayer entityDbPlayer = new EntityDbPlayer(entityId, null);
        entityDbPlayer.addComponentDbData(new ComponentDbPosition(1, 1));

        log.info(MyMarkers.DB, "save player:{}", entityDbPlayer);
        gamedoMongoTemplate.save(entityDbPlayer);

        return true;
    }

    private boolean unregisterEntity(IGameLoop gameLoop) {

        final Optional<IGameLoopEntityManager> entityManager = gameLoop.getComponent(IGameLoopEntityManager.class);
        final String gameLoopId = gameLoop.getId();

        return entityManager.map(manager -> {
            new HashMap<>(manager.getEntityMap())
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() instanceof EntityPlayer)
                    .forEach(entry -> manager.unregisterEntity(entry.getKey())
                            .ifPresent(entity -> log.info(Markers.GameLoop, "模拟下线成功, entity:{}", entity)));
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
            final EventGreeting event = new EventGreeting(id, "hello " + id);
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

        final EntityDbPlayer player = gamedoMongoTemplate.findById(id, EntityDbPlayer.class);

        EntityPlayer entity = new EntityPlayer(id, player);
        entity.addComponent(ComponentPosition.class, new ComponentPosition(entity));

        log.info(MyMarkers.DB, "加载entity, entity:{}", entity);
        return entity;
    }
}
