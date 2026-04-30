package dev.satherov.crystalix.data.provider;

import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.neoforge.common.Tags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@NothingNull
public class CXRecipeProvider extends RecipeProvider {
    
    private final HolderGetter<Item> items;
    private final RecipeOutput output;
    
    protected CXRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.items = registries.lookupOrThrow(Registries.ITEM);
        this.output = output;
    }
    
    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, CXRegistry.CRYSTALIX_BLOCK.get(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('a', Tags.Items.GEMS_AMETHYST)
                .unlockedBy("has_amethyst", this.has(Tags.Items.GEMS_AMETHYST))
                .save(this.output);
        
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, CXRegistry.CRYSTALIX_WAND.get().asItem())
                .pattern(" gd")
                .pattern(" sg")
                .pattern("s  ")
                .define('s', Items.STICK)
                .define('g', CXRegistry.CRYSTALIX_ITEM_TAG)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_crystalix_glass", this.has(CXRegistry.CRYSTALIX_ITEM_TAG))
                .save(this.output);
    }
    
    
    public static class Runner extends RecipeProvider.Runner {
        
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }
        
        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new CXRecipeProvider(lookupProvider, output);
        }
        
        @Override
        public String getName() {
            return "Crystalix recipes";
        }
    }
}
