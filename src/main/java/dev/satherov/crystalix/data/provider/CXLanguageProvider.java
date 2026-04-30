package dev.satherov.crystalix.data.provider;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.config.SLConfigLoader;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.LanguageProvider;

import net.minecraft.data.PackOutput;

@NothingNull
public class CXLanguageProvider extends LanguageProvider {
    
    public CXLanguageProvider(PackOutput output) {
        super(output, Crystalix.MOD_ID, "en_us");
    }
    
    @Override
    protected void addTranslations() {
        CXLanguage.translate(this::add);
        SLConfigLoader.translate(ModList.get().getModContainerById(Crystalix.MOD_ID).orElseThrow(), this::add);
        this.add(CXRegistry.CRYSTALIX_BLOCK.get(), "Crystalix Glass");
        this.add(CXRegistry.CRYSTALIX_WAND.get(), "Crystalix Wand");
    }
}
