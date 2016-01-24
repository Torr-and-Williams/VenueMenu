package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("Layer")
public class Layer extends ParseObject {
    private Client client;
    private User user;
    private String layerType;
    private String name;

    public Layer() {}

    public Layer(String name, String layerType, Client client, User user) {
        super();
        if (client != null) {put("client", client);}
        if (user != null) {put("user", user);}
        put("layerType", layerType);
        put("name", name);
    }
}
