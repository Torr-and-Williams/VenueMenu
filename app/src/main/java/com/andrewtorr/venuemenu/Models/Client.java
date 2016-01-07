package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Andrew on 1/7/2016.
 *
 */
@ParseClassName("Client")
public class Client extends ParseObject {
    private String clientName;
    private float Nbound;
    private float Sbound;
    private float Ebound;
    private float Wbound;

    public String getClientName() {
        return getString("clientName");
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public float getNbound() {
        return (float) get("Nbound");
    }

    public void setNbound(float nbound) {
        Nbound = nbound;
    }

    public float getSbound() {
        return Sbound;
    }

    public void setSbound(float sbound) {
        Sbound = sbound;
    }

    public float getEbound() {
        return Ebound;
    }

    public void setEbound(float ebound) {
        Ebound = ebound;
    }

    public float getWbound() {
        return Wbound;
    }

    public void setWbound(float wbound) {
        Wbound = wbound;
    }
}
