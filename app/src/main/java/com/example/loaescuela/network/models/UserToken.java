package com.example.loaescuela.network.models;

public class UserToken {
    public String token;
    public String name;
    public Long id;

    public UserToken(String token, String name){
        this.token=token;
        this.name=name;
    }
}
