package kz.qozha;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final RegistryKey<EntityType<?>> STALKER_KEY =
        RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(QozhaMod.MOD_ID, "stalker"));

    public static final EntityType<StalkerEntity> STALKER = Registry.register(
        Registries.ENTITY_TYPE, STALKER_KEY,
        EntityType.Builder.create(StalkerEntity::new, SpawnGroup.MISC)
            .dimensions(0.6f, 1.8f)      // scaled up by the SCALE attribute
            .maxTrackingRange(10)        // in chunks, so you can see him from far away
            .build(STALKER_KEY));

    public static void init() {
        FabricDefaultAttributeRegistry.register(STALKER, StalkerEntity.createStalkerAttributes());
    }
}
