package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CSRecipeProvider extends RecipeProvider implements IConditionBuilder {
    
    public CSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    
    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CSRegistry.WAND.get().asItem())
                .pattern(" gd")
                .pattern(" sg")
                .pattern("s  ")
                .define('s', Items.STICK)
                .define('g', CSRegistry.ITEM_TAG)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);
        
        CSRegistry.ENTRIES.forEach((type, holder) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, holder.get().asItem(), 4)
                    .pattern("gag")
                    .pattern("a a")
                    .pattern("gag")
                    .define('g', Tags.Items.GLASS_BLOCKS)
                    .define('a', type.tag())
                    .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                    .save(output);
        });
    }
    
}
