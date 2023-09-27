package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.content.entity.NixieTubeEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class NixieTubeItem extends BowItem {
    public NixieTubeItem(Settings settings) {
        super(settings);
    }

    public NixieTubeEntity createNixieTube(World world, LivingEntity shooter) {
        return new NixieTubeEntity(world, shooter);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        float pullProgress;
        if ((pullProgress = BowItem.getPullProgress(getMaxUseTime(ItemStack.EMPTY) - remainingUseTicks)) < 0.1)
            return;

        if (!world.isClient()) {
            NixieTubeEntity nixieTubeEntity = createNixieTube(world, player);
            nixieTubeEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.2F, pullProgress * 3, 1);
            nixieTubeEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;

            if (pullProgress == 1) nixieTubeEntity.setCritical(true);

            int levelPower;
            if ((levelPower = EnchantmentHelper.getLevel(Enchantments.POWER, stack)) > 0)
                nixieTubeEntity.setDamage(nixieTubeEntity.getDamage() + levelPower * 0.5 + 0.5);

            int levelPunch;
            if ((levelPunch = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack)) > 0)
                nixieTubeEntity.setPunch(levelPunch);

            if (!player.isCreative())
                stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));

            world.spawnEntity(nixieTubeEntity);
        }

        world.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1, 1 / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F
        );

        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }
}
