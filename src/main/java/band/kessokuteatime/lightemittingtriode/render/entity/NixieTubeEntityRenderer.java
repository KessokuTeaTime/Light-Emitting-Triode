package band.kessokuteatime.lightemittingtriode.render.entity;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.entity.NixieTubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class NixieTubeEntityRenderer extends ProjectileEntityRenderer<NixieTubeEntity> {
    public NixieTubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(NixieTubeEntity entity) {
        return LightEmittingTriode.id("textures", "entity", "projectiles", "nixie_tube.png");
    }
}
