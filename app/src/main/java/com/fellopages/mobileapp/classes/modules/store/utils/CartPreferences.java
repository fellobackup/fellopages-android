package com.fellopages.mobileapp.classes.modules.store.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

public class CartPreferences {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String PRODUCTS = "Product";

    public CartPreferences() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveProducts(Context context, List<Product> products) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonProducts = gson.toJson(products);

        editor.putString(PRODUCTS, jsonProducts);

        editor.commit();
    }

    public void addProduct(Context context, Product newProduct,boolean isConfigured) {
        boolean isFound = false;
        List<Product> products = getProducts(context);
        if (products == null) {
            products = new ArrayList<>();
        }else if(!isConfigured){
            for(int i=0;i < products.size();i++){
                Product product = products.get(i);
                if(product.getId() == newProduct.getId()){
                    product.setQuantity(product.getQuantity()+1);
                    isFound = true;
                    break;
                }
            }
        }else {
            for(int i=0;i < products.size();i++){
                Product product = products.get(i);
                if(product.getConfig() != null && product.getConfig().equals(newProduct.getConfig())){
                    product.setQuantity(product.getQuantity()+1);
                    isFound = true;
                    break;
                }
            }
        }
        if(!isFound) {
            products.add(newProduct);
        }
        saveProducts(context, products);
    }

    public void removeProduct(Context context, int product_id) {
        List<Product> products = getProducts(context);
        if (products != null) {
            for(int i=0;i<products.size();i++) {
                Product product = products.get(i);
                if (product.getId() == product_id) {
                    products.remove(product);
                }
            }
            saveProducts(context, products);
        }
    }

    public void removeProduct(Context context,JSONObject config) {
        JsonParser parser = new JsonParser();
        List<Product> products = getProducts(context);
        if (products != null) {
            for(int i=0;i<products.size();i++){
                Product product = products.get(i);
                if(product.getConfig() != null && product.getConfig().equals(parser.parse(config.toString()))){
                    products.remove(product);
                    break;
                }
            }
            saveProducts(context,products);
        }
    }

    public ArrayList<Product> getProducts(Context context) {
        SharedPreferences settings;
        List<Product> products;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(PRODUCTS)) {
            String jsonProducts = settings.getString(PRODUCTS, null);
            Gson gson = new Gson();
            Product[] productItems = gson.fromJson(jsonProducts,
                    Product[].class);

            products = Arrays.asList(productItems);
            products = new ArrayList<>(products);

        } else
            return null;

        return (ArrayList<Product>) products;
    }

    public void updateProduct(Context context,int product_id,int qty){
        List<Product> products = getProducts(context);
        if (products != null) {
            for(int i=0;i<products.size();i++){
                Product product = products.get(i);
                if (product.getId() == product_id) {
                    product.setQuantity(qty);
                    break;
                }
            }
            saveProducts(context,products);
        }
    }

    public void updateProduct(Context context,int qty,JSONObject config){
        JsonParser parser = new JsonParser();
        List<Product> products = getProducts(context);
        if (products != null) {
            for(int i=0;i<products.size();i++){
                Product product = products.get(i);
                if(product.getConfig() != null && product.getConfig().equals(parser.parse(config.toString()))){
                    product.setQuantity(qty);
                    break;
                }
            }
            saveProducts(context,products);
        }
    }

    public String getProductArray(Context context){
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(getProducts(context), new TypeToken<List<Product>>() {}.getType());
        if(element.isJsonNull()){
            return null;
        }
        return element.getAsJsonArray().toString();
    }

}
