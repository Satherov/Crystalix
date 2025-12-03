package dev.satherov.crystalix.compat;

import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

import java.util.Objects;

@EmiEntrypoint
public class CSEMIPlugin implements EmiPlugin {
    
    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(stack -> CSRegistry.OLD_ENTRIES.values().stream()
                .filter(Objects::nonNull)
                .map(DeferredHolder::getId)
                .toList()
                .contains(stack.getId())
        );
    }
}
