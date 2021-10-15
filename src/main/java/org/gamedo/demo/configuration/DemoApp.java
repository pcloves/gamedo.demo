package org.gamedo.demo.configuration;

import org.gamedo.Gamedo;
import org.gamedo.gameloop.interfaces.IGameLoopGroup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DemoApp extends Gamedo {

    protected DemoApp(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public static IGameLoopGroup db() {
        return HolderDb.db;
    }
}
