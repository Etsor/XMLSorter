package com.etsor.settings.model;

/**
 * Модель настроек для обработки XML
 */
public class Settings {
    private String arrayName;
    private String attributeName;

    public String getArrayName() {
        return arrayName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
