package com.example.loaescuela.network.models;

public class UserToken {
    public String token;
    public String name, level;
    public Long id;

    public UserToken(String token, String name, String level){
        this.token=token;
        this.name=name;
        this.level = level;
    }
}
