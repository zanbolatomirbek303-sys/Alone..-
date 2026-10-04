package kz.qozha;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QozhaMod implements ModInitializer {
    public static final String MOD_ID = "alone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.init();

        // 1) EMPTY WORLD: remove every mob (zombies, cows, sheep, villagers...) the moment it loads
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MobEntity && !(entity instanceof StalkerEntity)) {
                entity.discard();
            }
        });
        // ...and stop natural spawning completely
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            server.getGameRules().get(GameRules.DO_MOB_SPAWNING).set(false, server));

        // 2) Stalker spawning + creepy sounds
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            GameLock.tick(server);
            StalkerSpawner.tick(server);
            CreepyAmbience.tick(server);
        });

        // 3) "???" in the tab list (also makes TAB work in single-player)
        TabListTrick.init();

        LOGGER.info("Qozha Stalker loaded. Don't look back.");
    }
}
