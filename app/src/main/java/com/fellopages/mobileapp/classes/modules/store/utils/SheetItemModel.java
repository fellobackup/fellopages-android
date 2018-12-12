package com.fellopages.mobileapp.classes.modules.store.utils;

import org.json.JSONObject;

/**
 * Created by root on 29/8/16.
 */
public class SheetItemModel {
    String name, key, icon;
    JSONObject keyObject;

    public SheetItemModel(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public SheetItemModel(String name, String key, String icon) {
        this.name = name;
        this.key = key;
        this.icon = icon;
    }

    public SheetItemModel(JSONObject name, String key) {
        this.keyObject = name;
        this.key = key;
    }

    public JSONObject getKeyObject() {
        return keyObject;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIcon() {
        return icon;
    }

}
