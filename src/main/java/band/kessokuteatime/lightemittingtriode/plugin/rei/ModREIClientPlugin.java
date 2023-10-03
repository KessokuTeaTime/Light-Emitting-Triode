package band.kessokuteatime.lightemittingtriode.plugin.rei;

import band.kessokuteatime.lightemittingtriode.LightEmittingTriode;
import band.kessokuteatime.lightemittingtriode.content.ModRegistries;
import band.kessokuteatime.lightemittingtriode.content.variant.Wrapper;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        Arrays.stream(ModRegistries.Blocks.Type.values())
                .forEach(type -> registry.group(
                        LightEmittingTriode.id("blocks", type.basis().variant().getId()),
                        LightEmittingTriode.translatable(
                                "collapsibleEntry",
                                "blocks", type.basis().variant().getId()
                        ),
                        EntryIngredients.ofItems(type.wrappers().stream()
                                .map(Wrapper::blockItem)
                                .collect(Collectors.toList())
                        )
                ));
    }
}
