package org.gamedo.demo.ecs;

import org.gamedo.demo.persistence.EntityDbPlayer;
import lombok.extern.log4j.Log4j2;
import org.gamedo.ecs.Entity;

@SuppressWarnings("unused")
@Log4j2
public class EntityPlayer extends Entity {

    protected final EntityDbPlayer entityDbPlayer;

    public EntityPlayer(String id, EntityDbPlayer entityDbPlayer) {
        super(id);
        this.entityDbPlayer = entityDbPlayer;
    }

}
