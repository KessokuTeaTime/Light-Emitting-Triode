package band.kessokuteatime.lightemittingtriode.mixin;

import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithInjectedTooltip;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
    @Inject(method = "getTooltipFromItem", at = @At("RETURN"), cancellable = true)
    private void injectTooltip(ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        Item item = stack.getItem();
        if (!WithInjectedTooltip.class.isAssignableFrom(item.getClass())) return;

        List<Text> tooltips = cir.getReturnValue();

        AtomicInteger index = new AtomicInteger(1);
        ((WithInjectedTooltip) item).getInjectedTooltips()
                .forEach(tooltip -> tooltips.add(index.getAndIncrement(), tooltip));

        cir.setReturnValue(tooltips);
    }
}
