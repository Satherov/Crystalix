package dev.satherov.crystalix.config.annotation;

import net.neoforged.fml.config.ModConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
    
    ModConfig.Type value() default ModConfig.Type.COMMON;
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Group {
        
        String value() default "";
    }
}
