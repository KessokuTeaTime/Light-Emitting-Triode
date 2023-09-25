package band.kessokuteatime.lightemittingtriode.content.item;

import band.kessokuteatime.lightemittingtriode.content.item.base.extension.WithCustomItemParentModelId;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class InventoryColoredBlockItem extends ColoredBlockItem implements WithCustomItemParentModelId {
    public InventoryColoredBlockItem(Wrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Identifier getItemModelId(Wrapper.Basis basis) {
        return basis.genericId("inventory");
    }
}
