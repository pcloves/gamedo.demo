package org.gamedo.demo.configuration;

import org.gamedo.Gamedo;
import org.gamedo.configuration.GameLoopProperties;
import org.gamedo.configuration.GamedoProperties;
import org.gamedo.gameloop.GameLoop;
import org.gamedo.gameloop.GameLoopConfig;
import org.gamedo.gameloop.interfaces.IGameLoopGroup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DemoApp extends Gamedo {

    @SuppressWarnings({"NonFinalStaticVariableUsedInClassInitialization"})
    private static final class MyHolder {
        public static final IGameLoopGroup db = applicationContext.getBean(IGameLoopGroup.class, GameLoopConfig.builder()
                .gameLoopGroupId("dbs")
                .gameLoopCount(Runtime.getRuntime().availableProcessors() * 10)
                .gameLoopIdPrefix("db-")
                .gameLoopIdCounter(new AtomicInteger(1))
                .gameLoopImplClazz(GameLoop.class)
                .componentRegisters(GameLoopConfig.DEFAULT.getComponentRegisters())
                .daemon(false)
                .build()
        );
    }

    protected DemoApp(ApplicationContext applicationContext, GamedoProperties gamedoProperties, GameLoopProperties gameLoopProperties) {
        super(applicationContext, gamedoProperties, gameLoopProperties);
    }

    public static IGameLoopGroup db() {
        return DemoApp.MyHolder.db;
    }
}
