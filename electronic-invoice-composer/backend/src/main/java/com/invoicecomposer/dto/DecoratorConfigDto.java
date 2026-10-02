package com.invoicecomposer.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for configuring a single decorator in the chain.
 * Contains the decorator type and a flexible parameter map.
 */
public class DecoratorConfigDto {

    private String type;
    private Map<String, Object> parameters = new HashMap<>();

    public DecoratorConfigDto() {
    }

    public DecoratorConfigDto(String type, Map<String, Object> parameters) {
        this.type = type;
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    /**
     * Retrieves a double parameter by key, with a fallback default.
     */
    public double getDoubleParam(String key, double defaultValue) {
        Object value = parameters.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a string parameter by key, with a fallback default.
     */
    public String getStringParam(String key, String defaultValue) {
        Object value = parameters.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /** Prototype support: independent copy of this decorator configuration. */
    public DecoratorConfigDto copy() {
        return new DecoratorConfigDto(type, new HashMap<>(parameters));
    }
}
