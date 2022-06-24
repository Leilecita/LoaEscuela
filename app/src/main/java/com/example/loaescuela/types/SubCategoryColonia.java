package com.example.loaescuela.types;

public enum SubCategoryColonia {

    ALL(Constants.TYPE_ALL),
    SUB_CATEGORY_COLONIA_KIDS(Constants.SUB_CATEGORY_COLONIA_KIDS),
    SUB_CATEGORY_COLONIA_MINI(Constants.SUB_CATEGORY_COLONIA_MINI);

    private final String name;

    SubCategoryColonia(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
