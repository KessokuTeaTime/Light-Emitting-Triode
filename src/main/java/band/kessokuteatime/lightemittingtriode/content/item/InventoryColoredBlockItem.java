package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import band.kessokuteatime.lightemittingtriode.content.item.base.WithCustomItemParentModelId;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class InventoryColoredBlockItem extends ColoredBlockItem implements WithCustomItemParentModelId {
    public InventoryColoredBlockItem(Block block, Settings settings, DyeColor dyeColor) {
        super(block, settings, dyeColor);
    }

    @Override
    public Identifier getItemModelId(Variant.Basis basis) {
        return basis.genericId("inventory");
    }
}
