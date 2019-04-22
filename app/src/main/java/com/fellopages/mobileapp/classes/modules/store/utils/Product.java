package com.fellopages.mobileapp.classes.modules.store.utils;

import com.google.gson.JsonObject;


public class Product {

    private int product_id;
    private JsonObject configFields;
    private int quantity;

    public Product() {
        super();
    }

    public Product(int id, int quantity, JsonObject config) {
        super();
        this.product_id = id;
        this.quantity = quantity;
        this.configFields = config;
    }
    public int getId() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int qty){
        this.quantity = qty;
    }

    public JsonObject getConfig() {
        return configFields;
    }

}
