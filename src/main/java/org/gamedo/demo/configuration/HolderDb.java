package org.gamedo.demo.configuration;

import org.gamedo.gameloop.GameLoop;
import org.gamedo.gameloop.GameLoopConfig;
import org.gamedo.gameloop.interfaces.IGameLoopGroup;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"NonFinalStaticVariableUsedInClassInitialization"})
final class HolderDb {

    static final IGameLoopGroup db = DemoApp.context().getBean(IGameLoopGroup.class, GameLoopConfig.builder()
            .gameLoopGroupId("dbs")
            .nodeCountPerGameLoop(500)
            .gameLoopCount(Runtime.getRuntime().availableProcessors() * 10)
            .gameLoopIdPrefix("db-")
            .gameLoopIdCounter(new AtomicInteger(1))
            .gameLoopImplClazz(GameLoop.class)
            .componentRegisters(GameLoopConfig.DEFAULT.getComponentRegisters())
            .daemon(false)
            .build()
    );

    private HolderDb() {
    }
}
