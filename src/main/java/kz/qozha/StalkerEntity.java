package kz.qozha;

import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StalkerEntity extends PathAwareEntity {
    // ---- tweak these ----
    private static final double LOOK_DOT = 0.975;      // how exactly you must look at him (1.0 = dead center)
    private static final int VANISH_TICKS = 20;        // 20 ticks = 1 second
    private static final double FOLLOW_FROM = 14.0;    // he walks closer until this distance, then just stares
    // ---------------------

    private UUID targetId;
    private int lookTicks = 0;

    public StalkerEntity(EntityType<? extends StalkerEntity> type, World world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder createStalkerAttributes() {
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.22)
            .add(EntityAttributes.FOLLOW_RANGE, 80)
            .add(EntityAttributes.SCALE, 1.5);        // 1.5x taller than Steve (~2.7 blocks)
    }

    public void setTargetId(UUID id) { this.targetId = id; }
    public UUID getTargetId() { return targetId; }

    @Override
    public void tick() {
        super.tick();
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        PlayerEntity target = targetId == null ? null : world.getPlayerByUuid(targetId);
        if (target == null || !target.isAlive()) { this.discard(); return; }

        // Always stare at the player
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        double dy = target.getEyeY() - getEyeY();
        float yaw = (float) (MathHelper.atan2(dz, dx) * 57.29578) - 90.0f;
        float pitch = (float) -(MathHelper.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 57.29578);
        this.setYaw(yaw);
        this.setHeadYaw(yaw);
        this.setBodyYaw(yaw);
        this.setPitch(pitch);

        // Follow the player slowly
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (this.age % 20 == 0) {
            if (dist > FOLLOW_FROM) this.getNavigation().startMovingTo(target, 1.0);
            else this.getNavigation().stop();
        }

        // Did the player look at him?
        Vec3d look = target.getRotationVec(1.0f).normalize();
        Vec3d toMe = this.getEyePos().subtract(target.getEyePos()).normalize();
        boolean looking = look.dotProduct(toMe) > LOOK_DOT && target.canSee(this);

        if (looking) {
            lookTicks++;
            if (lookTicks >= VANISH_TICKS) vanish(world);
        } else {
            lookTicks = 0;
        }
    }

    private void vanish(ServerWorld world) {
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 2.0, getZ(), 40, 0.3, 1.0, 0.3, 0.01);
        world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
            SoundCategory.AMBIENT, 2.0f, 0.5f);
        this.discard();
    }

    @Override public boolean isPushable() { return false; }
}
