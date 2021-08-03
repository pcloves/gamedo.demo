package com.example.demo.ecs;

import com.example.demo.event.EventGreeting;
import com.example.demo.logging.MyMarkers;
import com.example.demo.persistence.ComponentDbPosition;
import lombok.extern.slf4j.Slf4j;
import org.gamedo.Gamedo;
import org.gamedo.annotation.Cron;
import org.gamedo.annotation.Subscribe;
import org.gamedo.annotation.Tick;
import org.gamedo.ecs.Component;
import org.gamedo.gameloop.components.eventbus.event.EventRegisterEntityPost;
import org.gamedo.gameloop.components.eventbus.event.EventUnregisterEntityPre;
import org.gamedo.persistence.core.DbDataMongoTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Slf4j
public class ComponentPosition extends Component<EntityPlayer> {

    private final ComponentDbPosition dbData;

    public ComponentPosition(EntityPlayer owner) {
        super(owner);

        dbData = owner.entityDbPlayer.getComponentDbData(ComponentDbPosition.class);
    }

    @Tick(delay = 0, tick = 50)
    private void tick(Long currentMilliSecond, Long lastMilliSecond) {
        dbData.setX(ThreadLocalRandom.current().nextInt(100));
        dbData.setY(ThreadLocalRandom.current().nextInt(100));

        dbData.setDirty("x", dbData.getX());
        dbData.setDirty("y", dbData.getY());
    }

    @Tick(delay = 0, tick = 10, timeUnit = TimeUnit.SECONDS)
    private void save(Long currentTime, Long lastTriggerTime) {

        if (!dbData.isDirty()) {
            return;
        }

        //这里不太优雅
        final DbDataMongoTemplate mongoTemplate = Gamedo.context().getBean(DbDataMongoTemplate.class);

        //实际上，存盘逻辑的职责也不应该分散于各个组件，而是应该有一个模块统一管理，这里是为了演示
        mongoTemplate.updateFirstAsync(dbData)
                .thenAccept(r -> log.info(MyMarkers.Entity, "save finish, entity:{}, result:{}", getOwner().getId(), r));
    }

    @Subscribe
    private void eventRegisterEntityPost(EventRegisterEntityPost event) {

        if (event.getEntityId().equals(getOwner().getId())) {
            log.info(MyMarkers.Entity,
                    "register finish, entity:{}, thread:{}",
                    getOwner().getId(),
                    Thread.currentThread().getName());
        }
    }

    @Subscribe
    private void eventUnregisterEntityPre(EventUnregisterEntityPre event) {
        //实际上，玩家下线时的存盘职责不应该隶属于每个Component组件，这里只是为了演示
        if (event.getEntityId().equals(getOwner().getId())) {
            final DbDataMongoTemplate mongoTemplate = Gamedo.context().getBean(DbDataMongoTemplate.class);
            mongoTemplate.saveAsync(dbData)
                    .thenAccept(r -> log.info(MyMarkers.Entity, "save finish before offline, entity:{}, result:{}", getOwner().getId(), r));
        }
    }

    @Cron("@daily")
    private void cron(Long currentTime, Long lastTriggerTime) {
        log.info(MyMarkers.Entity, "it's a new day.");
    }

    @Subscribe
    private void eventHello(EventGreeting event) {
        log.info(MyMarkers.Entity, "receive greeting:{}", event.content);
    }
}
