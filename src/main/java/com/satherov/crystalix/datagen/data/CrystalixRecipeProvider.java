package com.satherov.crystalix.datagen.data;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;

import java.util.concurrent.CompletableFuture;

public class CrystalixRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public CrystalixRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    private void tint(TagKey<Item> color, TagKey<Item> type, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 8)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', type)
                .define('b', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, Crystalix.MOD_ID + ":tinted_" + output.getId().getPath());
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CrystalixRegistry.WAND.get().asItem())
                .pattern("  n")
                .pattern(" s ")
                .pattern("s  ")
                .define('s', Items.STICK)
                .define('n', Tags.Items.NETHER_STARS)
                .unlockedBy("has_star", has(Tags.Items.NETHER_STARS))
                .save(recipeOutput);

        CrystalixRegistry.BLOCKS_MAP.forEach((color, typeMap) -> {
            typeMap.forEach((type, block) -> {

                tint(color.getTag(), CrystalixRegistry.ITEM_TAGS.get(type), block, recipeOutput);

                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block.get().asItem(), 4)
                        .pattern("gag")
                        .pattern("aca")
                        .pattern("gag")
                        .define('g', getGlassIngredient(type))
                        .define('a', type.getTag())
                        .define('c', color.getTag())
                        .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                        .save(recipeOutput);
            });
        });
    }

    private static TagKey<Item> getGlassIngredient(CrystalixRegistry.BlockTypes type) {
        return type == CrystalixRegistry.BlockTypes.GLASS
                ? Tags.Items.GLASS_BLOCKS
                : CrystalixRegistry.ITEM_TAGS.get(CrystalixRegistry.BlockTypes.GLASS);
    }

}
