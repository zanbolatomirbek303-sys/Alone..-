package kz.qozha.client;

import kz.qozha.StalkerEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

/** Draws the stalker with the player model + the default Steve skin. Height comes from the SCALE attribute. */
public class StalkerRenderer extends BipedEntityRenderer<StalkerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier STEVE = Identifier.ofVanilla("textures/entity/player/wide/steve.png");

    public StalkerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public PlayerEntityRenderState createRenderState() { return new PlayerEntityRenderState(); }

    @Override
    public Identifier getTexture(PlayerEntityRenderState state) { return STEVE; }
}
