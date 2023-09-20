package band.kessokuteatime.lightemittingtriode.util.old;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;

public class ClientDelegate {

    private final Block block;
    private final Item item;
    private final DyeColor color;

    public ClientDelegate(DyeColor color, Block block, Item item) {
        this.color = color;
        this.block = block;
        this.item = item;
    }
}
