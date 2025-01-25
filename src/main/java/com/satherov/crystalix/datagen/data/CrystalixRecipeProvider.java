package com.satherov.crystalix.datagen.data;

import java.util.concurrent.CompletableFuture;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.BlockSet;
import com.satherov.crystalix.content.CrystalixTags;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class CrystalixRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public CrystalixRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    private void tint(TagKey<Item> item, TagKey<Item> color, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 8)
                .pattern("aaa")
                .pattern("aba")
                .pattern("aaa")
                .define('a', item)
                .define('b', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, Crystalix.MOD_ID + ":tinted_" + output.getId().getPath());
    }

    private void block(TagKey<Item> color, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('a', ItemTags.WOOL)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void dark(Item glass, TagKey<Item> color, Item output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', glass)
                .define('a', Tags.Items.GEMS_AMETHYST)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void dark(DeferredHolder<Block, ?> glass, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', glass.get().asItem())
                .define('a', Tags.Items.GEMS_AMETHYST)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, output.getId() + "_from_" + glass.getId().getPath());
    }

    private void light(Item glass, TagKey<Item> color, Item output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', glass)
                .define('a', Tags.Items.DUSTS_GLOWSTONE)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void light(DeferredHolder<Block, ?> glass, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', glass.get().asItem())
                .define('a', Tags.Items.DUSTS_GLOWSTONE)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, output.getId() + "_from_" + glass.getId().getPath());
    }

    private void ghost(Item glass, TagKey<Item> color, Item output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', glass)
                .define('a', Items.PHANTOM_MEMBRANE)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void ghost(DeferredHolder<Block, ?> glass, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', glass.get().asItem())
                .define('a', Items.PHANTOM_MEMBRANE)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, output.getId() + "_from_" + glass.getId().getPath());
    }

    private void shaded(Item glass, TagKey<Item> color, Item output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', glass)
                .define('a', ItemTags.COALS)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void shaded(DeferredHolder<Block, ?> glass, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', glass.get().asItem())
                .define('a', ItemTags.COALS)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, output.getId() + "_from_" + glass.getId().getPath());
    }

    private void reinforced(Item glass, TagKey<Item> color, Item output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 4)
                .pattern("gag")
                .pattern("aca")
                .pattern("gag")
                .define('g', glass)
                .define('a', Tags.Items.OBSIDIANS)
                .define('c', color)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void reinforced(DeferredHolder<Block, ?> glass, DeferredHolder<Block, ?> output, RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.get().asItem(), 4)
                .pattern("gag")
                .pattern("a a")
                .pattern("gag")
                .define('g', glass.get().asItem())
                .define('a', Tags.Items.OBSIDIANS)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput, output.getId() + "_from_" + glass.getId().getPath());
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        BlockSet.apply(set -> {

            tint(CrystalixTags.ITEMTAG_BLOCK, set.color.getTag(), set.BLOCK, recipeOutput);
            block(set.color.getTag(), set.BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_SHADED_BLOCK, set.color.getTag(), set.SHADELESS_BLOCK, recipeOutput);
            shaded(set.BLOCK.get().asItem(), set.color.getTag(), set.SHADELESS_BLOCK.get().asItem(), recipeOutput);
            tint(CrystalixTags.ITEMTAG_REINFORCED_BLOCK, set.color.getTag(), set.REINFORCED_BLOCK, recipeOutput);
            reinforced(set.BLOCK.get().asItem(), set.color.getTag(), set.REINFORCED_BLOCK.get().asItem(), recipeOutput);
            tint(CrystalixTags.ITEMTAG_LIGHT_BLOCK, set.color.getTag(), set.LIGHT_BLOCK, recipeOutput);
            light(set.BLOCK.get().asItem(), set.color.getTag(), set.LIGHT_BLOCK.get().asItem(), recipeOutput);
            tint(CrystalixTags.ITEMTAG_DARK_BLOCK, set.color.getTag(), set.DARK_BLOCK, recipeOutput);
            dark(set.BLOCK.get().asItem(), set.color.getTag(), set.DARK_BLOCK.get().asItem(), recipeOutput);
            tint(CrystalixTags.ITEMTAG_GHOST_BLOCK, set.color.getTag(), set.GHOST_BLOCK, recipeOutput);
            ghost(set.BLOCK.get().asItem(), set.color.getTag(), set.GHOST_BLOCK.get().asItem(), recipeOutput);

            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_BLOCK, recipeOutput);
            shaded(set.REINFORCED_BLOCK, set.SHADELESS_REINFORCED_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_BLOCK, set.SHADELESS_REINFORCED_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_LIGHT_BLOCK, set.color.getTag(), set.SHADELESS_LIGHT_BLOCK, recipeOutput);
            shaded(set.LIGHT_BLOCK, set.SHADELESS_LIGHT_BLOCK, recipeOutput);
            light(set.SHADELESS_BLOCK, set.SHADELESS_LIGHT_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_DARK_BLOCK, set.color.getTag(), set.SHADELESS_DARK_BLOCK, recipeOutput);
            shaded(set.DARK_BLOCK, set.SHADELESS_DARK_BLOCK, recipeOutput);
            dark(set.SHADELESS_BLOCK, set.SHADELESS_DARK_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_GHOST_BLOCK, recipeOutput);
            shaded(set.GHOST_BLOCK, set.SHADELESS_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_BLOCK, set.SHADELESS_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_BLOCK, set.color.getTag(), set.REINFORCED_LIGHT_BLOCK, recipeOutput);
            reinforced(set.LIGHT_BLOCK, set.REINFORCED_LIGHT_BLOCK, recipeOutput);
            light(set.REINFORCED_BLOCK, set.REINFORCED_LIGHT_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_REINFORCED_DARK_BLOCK, set.color.getTag(), set.REINFORCED_DARK_BLOCK, recipeOutput);
            reinforced(set.DARK_BLOCK, set.REINFORCED_DARK_BLOCK, recipeOutput);
            dark(set.REINFORCED_BLOCK, set.REINFORCED_DARK_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_REINFORCED_GHOST_BLOCK, set.color.getTag(), set.REINFORCED_GHOST_BLOCK, recipeOutput);
            reinforced(set.GHOST_BLOCK, set.REINFORCED_GHOST_BLOCK, recipeOutput);
            ghost(set.REINFORCED_BLOCK, set.REINFORCED_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_LIGHT_GHOST_BLOCK, set.color.getTag(), set.LIGHT_GHOST_BLOCK, recipeOutput);
            ghost(set.LIGHT_BLOCK, set.LIGHT_GHOST_BLOCK, recipeOutput);
            light(set.GHOST_BLOCK, set.LIGHT_GHOST_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_DARK_GHOST_BLOCK, set.color.getTag(), set.DARK_GHOST_BLOCK, recipeOutput);
            ghost(set.DARK_BLOCK, set.DARK_GHOST_BLOCK, recipeOutput);
            dark(set.GHOST_BLOCK, set.DARK_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_LIGHT_BLOCK, recipeOutput);
            shaded(set.REINFORCED_LIGHT_BLOCK, set.SHADELESS_REINFORCED_LIGHT_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_LIGHT_BLOCK, set.SHADELESS_REINFORCED_LIGHT_BLOCK, recipeOutput);
            light(set.SHADELESS_REINFORCED_BLOCK, set.SHADELESS_REINFORCED_LIGHT_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_DARK_BLOCK, recipeOutput);
            shaded(set.REINFORCED_DARK_BLOCK, set.SHADELESS_REINFORCED_DARK_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_DARK_BLOCK, set.SHADELESS_REINFORCED_DARK_BLOCK, recipeOutput);
            dark(set.SHADELESS_REINFORCED_BLOCK, set.SHADELESS_REINFORCED_DARK_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_GHOST_BLOCK, recipeOutput);
            shaded(set.REINFORCED_GHOST_BLOCK, set.SHADELESS_REINFORCED_GHOST_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_GHOST_BLOCK, set.SHADELESS_REINFORCED_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_REINFORCED_BLOCK, set.SHADELESS_REINFORCED_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_SHADED_LIGHT_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_LIGHT_GHOST_BLOCK, recipeOutput);
            shaded(set.LIGHT_GHOST_BLOCK, set.SHADELESS_LIGHT_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_LIGHT_BLOCK, set.SHADELESS_LIGHT_GHOST_BLOCK, recipeOutput);
            light(set.SHADELESS_GHOST_BLOCK, set.SHADELESS_LIGHT_GHOST_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_DARK_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_DARK_GHOST_BLOCK, recipeOutput);
            shaded(set.DARK_GHOST_BLOCK, set.SHADELESS_DARK_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_DARK_BLOCK, set.SHADELESS_DARK_GHOST_BLOCK, recipeOutput);
            dark(set.SHADELESS_GHOST_BLOCK, set.SHADELESS_DARK_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_REINFORCED_LIGHT_GHOST_BLOCK, set.color.getTag(), set.REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            reinforced(set.LIGHT_GHOST_BLOCK, set.REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            ghost(set.REINFORCED_LIGHT_BLOCK, set.REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            light(set.REINFORCED_GHOST_BLOCK, set.REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_REINFORCED_DARK_GHOST_BLOCK, set.color.getTag(), set.REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            reinforced(set.DARK_GHOST_BLOCK, set.REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            ghost(set.REINFORCED_DARK_BLOCK, set.REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            dark(set.REINFORCED_GHOST_BLOCK, set.REINFORCED_DARK_GHOST_BLOCK, recipeOutput);

            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_LIGHT_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            shaded(set.REINFORCED_LIGHT_GHOST_BLOCK, set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_LIGHT_GHOST_BLOCK, set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            light(set.SHADELESS_REINFORCED_GHOST_BLOCK, set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_REINFORCED_LIGHT_BLOCK, set.SHADELESS_REINFORCED_LIGHT_GHOST_BLOCK, recipeOutput);
            tint(CrystalixTags.ITEMTAG_SHADED_REINFORCED_DARK_GHOST_BLOCK, set.color.getTag(), set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            shaded(set.REINFORCED_DARK_GHOST_BLOCK, set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            reinforced(set.SHADELESS_DARK_GHOST_BLOCK, set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            dark(set.SHADELESS_REINFORCED_GHOST_BLOCK, set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
            ghost(set.SHADELESS_REINFORCED_DARK_BLOCK, set.SHADELESS_REINFORCED_DARK_GHOST_BLOCK, recipeOutput);
        });
    }
}
