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
        log.info(MyMarkers.Entity, "ticking...");

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

        mongoTemplate.updateFirstAsync(dbData)
                .thenAccept(r -> log.info(MyMarkers.Entity, "save finish, entity:{}, result:{}", getOwner().getId(), r));
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
