package com.example.loaescuela.network;

public interface GenericCallback<T> {
    void onSuccess(T data);
    void onError(Error error);
}
