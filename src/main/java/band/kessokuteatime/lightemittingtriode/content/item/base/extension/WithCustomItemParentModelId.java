package band.kessokuteatime.lightemittingtriode.content.item.base.extension;

import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import net.minecraft.util.Identifier;

public interface WithCustomItemParentModelId {
    Identifier getItemModelId(Wrapper.Basis basis);
}
