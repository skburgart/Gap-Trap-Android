package com.skburgart.pothole.net;

public class NetConfig {

    public static final String BASE_URL = "http://skburgart.com/pothole";

    public interface Callback {
        public void processReseponse(String response);
    }
}