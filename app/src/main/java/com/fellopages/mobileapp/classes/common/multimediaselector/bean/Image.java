package com.fellopages.mobileapp.classes.common.multimediaselector.bean;

public class Image {
    public String path;
    public String mimeType;
    public String name;
    public long time;

    public Image(String path, String name, String mimeType, long time){
        this.path = path;
        this.name = name;
        this.mimeType = mimeType;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
