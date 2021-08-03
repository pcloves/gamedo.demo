package com.example.demo.event;

import lombok.Value;
import org.gamedo.gameloop.components.eventbus.interfaces.IEvent;

@Value
public class EventResponse implements IEvent {
    public String response;
}
