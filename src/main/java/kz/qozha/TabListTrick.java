package kz.qozha;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

/**
 * Sends a fake tab-list entry named "???". Because the list then has 2 entries, TAB also works in single-player.
 * Reflection is used so this keeps compiling if Mojang changes the packet constructors slightly between 1.21.x versions.
 */
public class TabListTrick {
    private static final UUID FAKE_ID = UUID.fromString("00000000-0000-4000-8000-000000000666");

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            try {
                handler.sendPacket(buildPacket());
            } catch (Exception e) {
                QozhaMod.LOGGER.error("Could not add '???' to tab list", e);
            }
        });
    }

    private static PlayerListS2CPacket buildPacket() throws Exception {
        // Profile name must be a normal valid name; the "???" is the display name shown in TAB.
        GameProfile profile = new GameProfile(FAKE_ID, "Unknown");

        // Build the Entry record using its biggest (canonical) constructor
        Constructor<?> best = null;
        for (Constructor<?> c : PlayerListS2CPacket.Entry.class.getDeclaredConstructors()) {
            if (best == null || c.getParameterCount() > best.getParameterCount()) best = c;
        }
        Class<?>[] types = best.getParameterTypes();
        Object[] args = new Object[types.length];
        int bools = 0;
        for (int i = 0; i < types.length; i++) {
            Class<?> t = types[i];
            if (t == UUID.class) args[i] = FAKE_ID;
            else if (t == GameProfile.class) args[i] = profile;
            else if (t == boolean.class) args[i] = (bools++ == 0);   // 1st boolean = listed(true), others false
            else if (t == int.class) args[i] = 0;                    // latency, list order
            else if (t == GameMode.class) args[i] = GameMode.SURVIVAL;
            else if (t == Text.class) args[i] = Text.literal("???");
            else args[i] = null;                                     // chat session etc.
        }
        best.setAccessible(true);
        Object entry = best.newInstance(args);

        EnumSet<Action> actions = EnumSet.of(Action.ADD_PLAYER, Action.UPDATE_LISTED,
            Action.UPDATE_LATENCY, Action.UPDATE_GAME_MODE, Action.UPDATE_DISPLAY_NAME);

        for (Constructor<?> c : PlayerListS2CPacket.class.getDeclaredConstructors()) {
            Class<?>[] p = c.getParameterTypes();
            if (p.length == 2 && p[0] == EnumSet.class && p[1] == List.class) {
                c.setAccessible(true);
                return (PlayerListS2CPacket) c.newInstance(actions, List.of(entry));
            }
        }
        throw new IllegalStateException("PlayerListS2CPacket constructor not found");
    }
}
