package kz.qozha;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class StalkerSpawner {
    private static final Random RNG = new Random();
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>(); // ticks until next appearance

    public static void tick(MinecraftServer server) {
        if (server.getTicks() % 10 != 0) return;   // runs every 0.5 s

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isSpectator() || !p.isAlive()) continue;
            ServerWorld w = p.getServerWorld();
            if (w.getRegistryKey() != World.OVERWORLD) continue;

            UUID id = p.getUuid();
            boolean hasStalker = !w.getEntitiesByType(ModEntities.STALKER,
                s -> id.equals(s.getTargetId())).isEmpty();

            if (hasStalker) {                       // wait 15-60 s after he disappears
                COOLDOWN.put(id, 300 + RNG.nextInt(900));
                continue;
            }
            int cd = COOLDOWN.getOrDefault(id, 300) - 10;
            if (cd > 0) { COOLDOWN.put(id, cd); continue; }
            COOLDOWN.put(id, trySpawn(w, p) ? 300 + RNG.nextInt(900) : 40);
        }
    }

    private static boolean trySpawn(ServerWorld w, ServerPlayerEntity p) {
        for (int i = 0; i < 10; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double dist = 40 + RNG.nextDouble() * 12;       // 40-52 blocks away
            int x = MathHelper.floor(p.getX() + Math.cos(angle) * dist);
            int z = MathHelper.floor(p.getZ() + Math.sin(angle) * dist);

            if (!w.isChunkLoaded(new BlockPos(x, 0, z))) continue;
            int y = w.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (Math.abs(y - p.getY()) > 25) continue;       // skip if player is deep underground
            BlockPos pos = new BlockPos(x, y, z);
            if (!w.getFluidState(pos.down()).isEmpty()) continue;
            if (!w.getBlockState(pos.down()).isSolidBlock(w, pos.down())) continue;

            StalkerEntity s = ModEntities.STALKER.create(w, SpawnReason.EVENT);
            if (s == null) return false;
            s.refreshPositionAndAngles(x + 0.5, y, z + 0.5, 0, 0);
            s.setTargetId(p.getUuid());
            w.spawnEntity(s);
            return true;
        }
        return false;
    }
}
