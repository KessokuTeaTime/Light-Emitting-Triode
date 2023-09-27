package band.kessokuteatime.lightemittingtriode.content.entity;

import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NixieTubeEntity extends PersistentProjectileEntity {

    public NixieTubeEntity(EntityType<NixieTubeEntity> entityType, World world) {
        super(entityType, world);
    }

    public NixieTubeEntity(World world, double x, double y, double z) {
        super(EntityType.ARROW, x, y, z, world);
    }

    public NixieTubeEntity(World world, LivingEntity owner) {
        super(ModRegistries.EntityTypes.NIXIE_TUBE, owner, world);
    }

    public int getLuminance() {
        return 20;
    }

    @Override
    protected ItemStack asItemStack() {
        return ModRegistries.Items.NIXIE_TUBE.getDefaultStack();
    }

    @Override
    public boolean isAttackable() {
        return true;
    }
}
