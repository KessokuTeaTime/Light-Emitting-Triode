package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.item.base.Colorable;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColoredBlockItem extends BlockItem implements Colorable {
    private final Wrapper wrapper;

    public ColoredBlockItem(Wrapper wrapper) {
        super(wrapper.block(), new Settings());
        this.wrapper = wrapper;
    }

    protected Wrapper wrapper() {
        return wrapper;
    }

    @Override
    public DyeColor getDyeColor() {
        return wrapper().dyeColor();
    }

    @Override
    public String getParentTranslationKey() {
        return getTranslationKey().replace("_" + getDyeColor().getName(), "");
    }

    @Override
    public Text getName() {
        return Colorable.super.getName();
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(
                Text.literal("#" + Integer.toHexString(LightEmittingTriode.getColorFromDyeColor(getDyeColor())).toUpperCase())
                        .styled(style -> style
                                .withItalic(true)
                                .withColor(LightEmittingTriode.mapColorRange(
                                        LightEmittingTriode.getColorFromDyeColor(getDyeColor()),
                                        0x10, 0x64
                                ))
                        )
        );

        wrapper().basis().variant().getTooltips().forEach(modTooltip -> tooltip.add(modTooltip.text()));
    }
}
