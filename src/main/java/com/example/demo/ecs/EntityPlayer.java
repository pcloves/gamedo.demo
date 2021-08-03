package com.example.demo.ecs;

import com.example.demo.persistence.EntityDbPlayer;
import lombok.extern.slf4j.Slf4j;
import org.gamedo.ecs.Entity;

@SuppressWarnings("unused")
@Slf4j
public class EntityPlayer extends Entity {

    protected final EntityDbPlayer entityDbPlayer;

    public EntityPlayer(String id, EntityDbPlayer entityDbPlayer) {
        super(id);
        this.entityDbPlayer = entityDbPlayer;
    }

}
