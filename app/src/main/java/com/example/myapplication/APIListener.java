package com.example.myapplication;

public interface APIListener {
    void onPreExecute();
    void onRequestSuccess(Object object);
    void onRequestFailure(String message, int errorCode);
}
