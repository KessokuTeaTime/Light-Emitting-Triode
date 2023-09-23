package band.kessokuteatime.lightemittingtriode.content.item.base;

import band.kessokuteatime.lightemittingtriode.content.Variant;
import net.minecraft.util.Identifier;

public interface WithCustomItemModelId {
    Identifier getItemModelId(Variant.Basis basis);
}
