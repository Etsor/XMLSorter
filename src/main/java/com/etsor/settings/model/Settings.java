package com.etsor.settings.model;

/**
 * Модель настроек для обработки XML
 */
public class Settings {
    private String arrayName;
    private String attributeName;

    /**
     * @return имя элемента, содержащего массив для сортировки
     */
    public String getArrayName() {
        return arrayName;
    }

    /**
     * @return имя атрибута по которому будет проводиться сортировка
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param arrayName имя элемента, содержащего массив для сортировки
     */
    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    /**
     * @param attributeName имя атрибута по которому будет проводиться сортировка
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
