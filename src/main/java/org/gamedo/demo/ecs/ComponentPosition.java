package org.gamedo.demo.ecs;

import org.gamedo.demo.configuration.DemoApp;
import org.gamedo.demo.event.EventGreeting;
import org.gamedo.demo.logging.MyMarkers;
import org.gamedo.demo.persistence.ComponentDbPosition;
import lombok.extern.log4j.Log4j2;
import org.gamedo.Gamedo;
import org.gamedo.annotation.Cron;
import org.gamedo.annotation.Subscribe;
import org.gamedo.annotation.Tick;
import org.gamedo.ecs.Component;
import org.gamedo.gameloop.components.eventbus.event.EventRegisterEntityPost;
import org.gamedo.gameloop.components.eventbus.event.EventUnregisterEntityPre;
import org.gamedo.persistence.GamedoMongoTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Log4j2
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

        dbData.update("x", dbData.getX());
        dbData.update("y", dbData.getY());
    }

    @Tick(delay = 0, tick = 10, timeUnit = TimeUnit.SECONDS)
    private void tickSave(Long currentTime, Long lastTriggerTime) {

        if (!dbData.isDirty()) {
            return;
        }

        //这里不太优雅
        final GamedoMongoTemplate mongoTemplate = Gamedo.context().getBean(GamedoMongoTemplate.class);

        //实际上，存盘逻辑的职责也不应该分散于各个组件，而是应该有一个模块统一管理，这里是为了演示
        mongoTemplate.updateDbDataFirstAsync(dbData, DemoApp.db())
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
            final GamedoMongoTemplate mongoTemplate = Gamedo.context().getBean(GamedoMongoTemplate.class);
            mongoTemplate.saveDbDataAsync(dbData, DemoApp.db())
                    .thenAccept(r -> log.info(MyMarkers.Entity,
                            "save finish before offline, entity:{}, result:{}",
                            getOwner().getId(),
                            r));
        }
    }

    @Cron("*/10 * * * * *")
    private void cron10Second(Long currentTime, Long lastTriggerTime) {
        log.info(MyMarkers.Entity, "cron 10 second.");
    }

    @Cron("*/20 * * * * *")
    private void cron20Second(Long currentTime, Long lastTriggerTime) {
    }

    @Subscribe
    private void eventGreeting(EventGreeting event) {
        if (event.getId().equals(getOwner().getId())) {
            log.info(MyMarkers.Entity, "receive greeting:{}", event);
        }
    }
}
