package dev.satherov.crystalix.config;

/**
 * Base interface that all enums that represent config entries much implement.
 */
public interface ConfigEnum {
    
    String name();
    
    String comment();
}
