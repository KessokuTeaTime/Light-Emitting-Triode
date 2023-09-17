package band.kessokuteatime.lightemittingtriode;

import band.kessokuteatime.lightemittingtriode.util.ClientDelegate;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class LETRegistries {
    public static class Registers {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(LET.ID, RegistryKeys.BLOCK);
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(LET.ID, RegistryKeys.ITEM);

        public static RegistrySupplier<Block> registerBlock(Identifier id, AbstractBlock.Settings settings) {
            return BLOCKS.register(id, () -> new Block(settings));
        }

        public static RegistrySupplier<Item> registerItem(Identifier id, Item.Settings settings) {
            return ITEMS.register(id, () -> new Item(settings));
        }

        public static void register() {
            BLOCKS.register();
            ITEMS.register();
        }
    }

    public static class ItemGroups {
        public static final ItemGroup GENERAL = CreativeTabRegistry.create(
                Text.translatable(LET.idString("itemGroup", "general")),
                () -> Items.LED.get().getDefaultStack()
        );
    }

    public static class Items {
        public static final RegistrySupplier<Item> BULB = Registers.registerItem(
                LET.id("bulb"),
                new Item.Settings()
        );

        public static final RegistrySupplier<Item> LED = Registers.registerItem(
                LET.id("led"),
                new Item.Settings().arch$tab(ItemGroups.GENERAL)
        );

        public static final RegistrySupplier<Item> SHADE = Registers.registerItem(
                LET.id("shade"),
                new Item.Settings().arch$tab(ItemGroups.GENERAL)
        );
    }

    public static void registerForColors(String name, Supplier<Block> supplier) {
        for (DyeColor color : DyeColor.values()) {
            Block block = supplier.get();
            Item item = new BlockItem(block, new Item.Settings());

            Identifier id = LET.id(name + "_" + color.getName());
            ClientDelegate delegate = new ClientDelegate(color, block, item);

            //Registers.registerItem(id, item);
            //Registers.registerBlock(id, block);
        }
    }
}
