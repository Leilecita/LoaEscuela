package com.example.loaescuela.types;

public enum SubCategoryEscuela {

    ALL(Constants.TYPE_ALL),
    SUB_CATEGORY_ESCUELA_AD(Constants.SUB_CATEGORY_ESCUELA_ADULTOS),
    SUB_CATEGORY_ESCUELA_INT(Constants.SUB_CATEGORY_ESCUELA_INTERMEDIOS);

    private final String name;

    SubCategoryEscuela(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
