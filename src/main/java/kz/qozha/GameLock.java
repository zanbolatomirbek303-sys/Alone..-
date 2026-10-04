package kz.qozha;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

/** Creative mode is forbidden: anyone who switches to it (command, F3+F4, anything) is put back to survival instantly. */
public class GameLock {
    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (p.isCreative()) {
                p.changeGameMode(GameMode.SURVIVAL);
                p.sendMessage(Text.literal("§cCreative режимі өшірілген!"), true);
            }
        }
    }
}
