package com.skburgart.pothole.net;

public class NetConfig {

    public static final String BASE_URL = "http://192.252.222.195:8080/pothole";

    public interface Callback {
        public void processReseponse(String response);
    }
}