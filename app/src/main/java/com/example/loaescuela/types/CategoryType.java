package com.example.loaescuela.types;


public enum CategoryType {

    ALL(Constants.TYPE_ALL),
    COLONIA(Constants.CATEGORY_COLONIA),
    ESCUELA(Constants.CATEGORY_ESCUELA),
    HIGHSCHOOL(Constants.CATEGORY_HIGHSCHOOL);

    private final String name;

    CategoryType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
