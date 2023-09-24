package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.util.Identifier;

public interface WithCustomItemParentModelId {
    Identifier getItemModelId(Variant.Basis basis);
}
