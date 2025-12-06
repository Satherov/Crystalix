package dev.satherov.crystalix.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.config.annotation.Config;
import dev.satherov.crystalix.config.annotation.ConfigVal;

import net.neoforged.fml.ModList;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.ModFileScanData;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigLoader {
    
    private final List<Cache> caches = new ArrayList<>();
    
    public static ConfigLoader create() {
        return new ConfigLoader();
    }
    
    public void discover(FMLModContainer container) {
        Map<ModConfig.Type, List<Class<?>>> configs = new HashMap<>();
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotation.annotationType().equals(Type.getType(Config.class))) {
                    try {
                        Class<?> clazz = Class.forName(annotation.clazz().getClassName());
                        Config config = clazz.getAnnotation(Config.class);
                        configs.computeIfAbsent(config.value(), type -> new ArrayList<>()).add(clazz);
                    } catch (ClassNotFoundException e) {
                        ConfigLoader.log.error("Failed to load config class {}", annotation.clazz().getClassName(), e);
                    }
                }
            }
        }
        configs.forEach((type, list) -> {
            list.forEach(clazz -> {
                Cache cache = new Cache();
                ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
                this.generate(clazz, cache, builder);
                ModConfigSpec spec = builder.build();
                cache.setSpec(spec);
                this.caches.add(cache);
                container.registerConfig(type, spec, Crystalix.MOD_ID + "/" + Crystalix.MOD_ID + "-" + type.extension() + ".toml");
            });
        });
    }
    
    public void update(IConfigSpec spec) {
        this.caches.forEach(cache -> {
            if (cache.getSpec() == spec) {
                cache.getValues().forEach((field, value) -> {
                    try {
                        field.setAccessible(true);
                        field.set(null, value.get());
                    } catch (IllegalAccessException e) {
                        ConfigLoader.log.error("Failed to update config field {}", field.getName(), e);
                    }
                });
            }
        });
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void generate(Class<?> clazz, Cache cache, ModConfigSpec.Builder builder) {
        for (Class<?> c : clazz.getDeclaredClasses()) {
            if (c.isAnnotationPresent(Config.Group.class)) {
                Config.Group group = c.getAnnotation(Config.Group.class);
                String name = group.value().isEmpty() ? c.getSimpleName() : group.value();
                builder.push(name);
                this.generate(c, cache, builder);
                builder.pop();
            }
        }
        
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigVal.class)) continue;
            ConfigVal val = field.getAnnotation(ConfigVal.class);
            
            try {
                field.setAccessible(true);
                Object obj = field.get(null);
                ModConfigSpec.ConfigValue<?> spec;
                
                if (field.isAnnotationPresent(ConfigVal.String.class)) {
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof String value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not a String");
                    builder.comment(" " + val.comment());
                    builder.comment(" Default: " + value);
                    spec = builder.define(val.name(), value);
                    
                } else if (field.isAnnotationPresent(ConfigVal.Boolean.class)) {
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof Boolean value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not a Boolean");
                    builder.comment(" " + val.comment());
                    builder.comment(" Default: " + value);
                    spec = builder.define(val.name(), value);
                    
                } else if (field.isAnnotationPresent(ConfigVal.Integer.class)) {
                    ConfigVal.Integer entry = field.getAnnotation(ConfigVal.Integer.class);
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof Integer value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not an Integer");
                    builder.comment(" " + val.comment());
                    spec = builder.defineInRange(val.name(), value, entry.min(), entry.max());
                    
                } else if (field.isAnnotationPresent(ConfigVal.Long.class)) {
                    ConfigVal.Long entry = field.getAnnotation(ConfigVal.Long.class);
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof Long value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not a Long");
                    builder.comment(" " + val.comment());
                    spec = builder.defineInRange(val.name(), value, entry.min(), entry.max());
                    
                } else if (field.isAnnotationPresent(ConfigVal.Double.class)) {
                    ConfigVal.Double entry = field.getAnnotation(ConfigVal.Double.class);
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof Double value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not a Double");
                    builder.comment(" " + val.comment());
                    spec = builder.defineInRange(val.name(), value, entry.min(), entry.max());
                    
                } else if (field.isAnnotationPresent(ConfigVal.Enum.class)) {
                    ConfigVal.Enum entry = field.getAnnotation(ConfigVal.Enum.class);
                    if (obj == null) throw new NullPointerException("Config Field " + field.getName() + " is null");
                    if (!(obj instanceof ConfigEnum value)) throw new IllegalArgumentException("Config Field " + field.getName() + " is not a Config Enum");
                    builder.comment(" " + val.comment());
                    builder.comment(" Default: " + obj);
                    Arrays.stream(entry.value().getEnumConstants()).forEach(e -> {
                        ConfigEnum cfg = (ConfigEnum) e;
                        builder.comment(" " + cfg.name() + " - " + cfg.comment());
                    });
                    spec = builder.defineEnum(val.name(), (Enum) value);
                    
                } else throw new IllegalArgumentException("Field is annotated with @ConfigVal, but has no defined type");
                cache.values.put(field, spec);
                
            } catch (IllegalAccessException e) {
                ConfigLoader.log.error("Failed to access field {}", field.getName(), e);
            } catch (NullPointerException e) {
                ConfigLoader.log.error("Config Field value {} is null or not static", field.getName(), e);
            }
        }
    }
    
    @NoArgsConstructor
    protected static class Cache {
        public @Getter @Setter ModConfigSpec spec;
        public @Getter Map<Field, ModConfigSpec.ConfigValue<?>> values = new HashMap<>();
    }
}
