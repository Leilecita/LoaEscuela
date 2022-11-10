package com.example.loaescuela.types;


public enum CategoryType {

    ALL(Constants.TYPE_ALL),
    ESCUELA(Constants.CATEGORY_ESCUELA),
    COLONIA(Constants.CATEGORY_COLONIA),
    HIGHSCHOOL(Constants.CATEGORY_HIGHSCHOOL);

    private final String name;

    CategoryType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
