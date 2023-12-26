package com.example.loaescuela.types;

public enum SubCategoryType {

    ALL(Constants.TYPE_ALL),
    SUB_CATEGORY_ESCUELA_AD(Constants.SUB_CATEGORY_ESCUELA_ADULTOS),
    SUB_CATEGORY_ESCUELA_INT(Constants.SUB_CATEGORY_ESCUELA_INTERMEDIOS),
    SUB_CATEGORY_COLONIA_KIDS(Constants.SUB_CATEGORY_COLONIA_KIDS),
    SUB_CATEGORY_HIGHSCHOOL_HIGHSCHOOL(Constants.CATEGORY_HIGHSCHOOL),
    SUB_CATEGORY_COLONIA_MINI(Constants.SUB_CATEGORY_COLONIA_MINI);

    private final String name;

    SubCategoryType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
