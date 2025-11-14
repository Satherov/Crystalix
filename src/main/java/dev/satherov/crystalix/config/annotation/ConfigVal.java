package dev.satherov.crystalix.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigVal {
    
    /**
     * Config entry name
     *
     * @return name
     */
    java.lang.String name();
    
    /**
     * Config entry comment
     *
     * @return comment
     */
    java.lang.String comment();
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface String { }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Boolean { }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Integer {
        
        int min() default java.lang.Integer.MIN_VALUE;
        
        int max() default java.lang.Integer.MAX_VALUE;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Long {
        
        long min() default java.lang.Long.MIN_VALUE;
        
        long max() default java.lang.Long.MAX_VALUE;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Double {
        
        double min() default java.lang.Double.MIN_VALUE;
        
        double max() default java.lang.Double.MAX_VALUE;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Enum {
        
        Class<? extends java.lang.Enum<?>> value();
    }
}
