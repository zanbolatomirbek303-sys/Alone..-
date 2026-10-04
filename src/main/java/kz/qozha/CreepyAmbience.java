package kz.qozha;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/** Random creepy sounds near the player: footsteps, someone mining, doors, heartbeat, moans. */
public class CreepyAmbience {
    private static final Random RNG = new Random();
    private record Task(int at, Runnable run) {}
    private static final List<Task> TASKS = new ArrayList<>();

    private static final int EVENT_CHANCE = 900;   // 1 in 900 ticks per player (~every 45 s). Lower = more often.

    public static void tick(MinecraftServer server) {
        int now = server.getTicks();

        for (Iterator<Task> it = TASKS.iterator(); it.hasNext();) {
            Task t = it.next();
            if (t.at() <= now) { it.remove(); t.run().run(); }
        }
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isSpectator()) continue;
            if (RNG.nextInt(EVENT_CHANCE) == 0) trigger(p, now);
        }
    }

    private static void at(int tick, Runnable r) { TASKS.add(new Task(tick, r)); }

    private static void play(ServerWorld w, double x, double y, double z, SoundEvent s, float vol, float pitch) {
        w.playSound(null, x, y, z, s, SoundCategory.AMBIENT, vol, pitch);
    }

    private static void trigger(ServerPlayerEntity p, int now) {
        ServerWorld w = p.getServerWorld();
        final double px = p.getX(), py = p.getY(), pz = p.getZ();
        final double angle = RNG.nextDouble() * Math.PI * 2;
        final double cos = Math.cos(angle), sin = Math.sin(angle);
        BlockSoundGroup g = w.getBlockState(p.getBlockPos().down()).getSoundGroup();

        switch (RNG.nextInt(5)) {
            case 0 -> { // someone walking up to you
                for (int i = 0; i < 9; i++) {
                    final double r = 14 - i;
                    at(now + 8 * i, () -> play(w, px + cos * r, py, pz + sin * r,
                        g.getStepSound(), 1.5f, 0.85f + RNG.nextFloat() * 0.2f));
                }
            }
            case 1 -> { // someone mining a block
                final double r = 9 + RNG.nextInt(8);
                for (int i = 0; i < 4; i++) {
                    at(now + 7 * i, () -> play(w, px + cos * r, py, pz + sin * r, g.getHitSound(), 1.8f, 0.8f));
                }
                at(now + 30, () -> play(w, px + cos * r, py, pz + sin * r, g.getBreakSound(), 2.0f, 0.9f));
            }
            case 2 -> { // a door opens, then closes
                final double r = 8 + RNG.nextInt(8);
                at(now, () -> play(w, px + cos * r, py, pz + sin * r, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, 1.8f, 0.8f));
                at(now + 50, () -> play(w, px + cos * r, py, pz + sin * r, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, 1.8f, 0.8f));
            }
            case 3 -> { // heartbeat
                for (int i = 0; i < 8; i++) {
                    at(now + 20 * i, () -> play(w, px, py, pz, SoundEvents.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f));
                }
            }
            default -> { // far-away moan
                SoundEvent[] moans = {
                    SoundEvents.ENTITY_GHAST_AMBIENT, SoundEvents.ENTITY_ENDERMAN_STARE,
                    SoundEvents.BLOCK_PORTAL_AMBIENT, SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT };
                play(w, px + cos * 20, py, pz + sin * 20, moans[RNG.nextInt(moans.length)], 2.0f, 0.5f);
            }
        }
    }
}
