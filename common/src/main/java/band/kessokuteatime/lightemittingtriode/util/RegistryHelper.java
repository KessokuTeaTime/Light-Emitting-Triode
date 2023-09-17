package band.kessokuteatime.lightemittingtriode.util;

import band.kessokuteatime.lightemittingtriode.LET;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryHelper {

    private static final ArrayList<ClientDelegate> delegates = new ArrayList<>();
    public static final String ID = "led";

    public static final ItemGroup GROUP = FabricItemGroup.builder(id("group"))
            .displayName(Text.translatable("itemGroup.led.group"))
            .icon(() -> new ItemStack(LET.BULB))
            .build();

    public static final List<Item> items = new ArrayList<>();
    public static final FabricItemSettings ITEM_SETTINGS = new FabricItemSettings();

    public static Identifier id(String name) {
        return new Identifier(ID, name);
    }

    public static void registerForColors(String name, Supplier<Block> supplier, DiodeVariant.RecipeDelegate recipe) {
        for (DyeColor color : DyeColor.values()) {
            Block block = supplier.get();
            Item item = new BlockItem(block, ITEM_SETTINGS);

            String id = name + "_" + color.getName();
            ClientDelegate delegate = new ClientDelegate(color, block, item);

            registerItem(id, item, true);
            registerBlock(id, block);

            recipe.register(item, color);

            delegates.add(delegate);
        }
    }

    public static void registerItem(String name, Item item, boolean addToGroup) {
        if (addToGroup) {
            items.add(item);
        }

        Registry.register(Registries.ITEM, id(name), item);
    }

    public static void registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
    }

    @Environment(EnvType.CLIENT)
    public static void applyDelegates() {
        for (ClientDelegate delegate : delegates) {
            delegate.register();
        }

        LET.LOG.info("[LED] Applied " + delegates.size() + " client delegates.");
        delegates.clear();
    }

    @Environment(EnvType.SERVER)
    public static void discardDelegates() {
        LET.LOG.info("[LED] Discarded " + delegates.size() + " client delegates.");
        delegates.clear();
    }

    public static void appendItemsToGroup() {
        ItemGroupEvents.modifyEntriesEvent(GROUP).register(listener -> {
            items.forEach(listener::add);
        });
    }

}
