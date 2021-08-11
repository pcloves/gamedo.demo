package org.gamedo.demo.event;

import lombok.Value;
import org.gamedo.gameloop.components.eventbus.interfaces.IEvent;

@Value
public class EventGreeting implements IEvent {
    public String id;
    public String content;
}
