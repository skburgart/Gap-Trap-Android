package com.skburgart.pothole.net;

public class NetConfig {

    public interface Callback {
        public void processReseponse(String response);
    }
}