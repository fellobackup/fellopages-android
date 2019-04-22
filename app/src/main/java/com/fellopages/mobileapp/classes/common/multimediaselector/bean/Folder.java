package com.fellopages.mobileapp.classes.common.multimediaselector.bean;

import java.util.List;

public class Folder {
    public String name;
    public String path;
    public String mimeType;
    public Image cover;
    public List<Image> images;

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
