package com.andrewtorr.venuemenu.Models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by Andrew on 1/7/2016.
 *
 */
@ParseClassName("Client")
public class Client extends ParseObject {
    private String clientName;
    private double Nbound;
    private double Sbound;
    private double Ebound;
    private double Wbound;
    private ParseGeoPoint center;
    private int color = 0xffffffff;

    public String getClientName() {
        return getString("clientName");
    }

    public void setClientName(String clientName) {
        put("clientName", clientName);
    }

    public int getColor() {
        return getInt("color");
    }

    public void setColor(int color) {
        put("color", color);
    }

    public double getNbound() {
        return getDouble("Nbound");
    }

    public void setNbound(double nbound) {
        put("Nbound", nbound);
    }

    public void resetNbound(double nbound) {
        setNbound(nbound);
        Nbound = nbound;
        resetCenter();
    }

    public double getSbound() {
        return getDouble("Sbound");
    }

    public void setSbound(double sbound) {
        put("Sbound", sbound);
    }

    public void resetSbound(double sbound) {
        setSbound(sbound);
        Sbound = sbound;
        resetCenter();
    }

    public double getEbound() {
        return getDouble("Ebound");
    }

    public void setEbound(double ebound) {
        put("Ebound", ebound);
    }

    public void resetEbound(double ebound) {
        setEbound(ebound);
        Ebound = ebound;
        resetCenter();
    }

    public double getWbound() {
        return getDouble("Wbound");
    }

    public void setWbound(double wbound) {
        put("Wbound", wbound);
    }

    public void resetWbound(double wbound) {
        setWbound(wbound);
        Wbound = wbound;
        resetCenter();
    }

    public void resetCenter() {
        put("center", new ParseGeoPoint((Nbound + Sbound) / 2, (Ebound + Wbound) / 2));
    }

    public LatLng getCenterLatLng() {
        center = (ParseGeoPoint) get("center");
        return new LatLng(center.getLatitude(), center.getLongitude());
    }

    public Client() {}

    public Client(double north, double east, double south, double west, String name, int color) {
        super();
        setNbound(north);
        setSbound(south);
        setEbound(east);
        setWbound(west);

        setClientName(name);
        setColor(color);

        put("center", new ParseGeoPoint((north + south) / 2, (east + west) / 2));
    }
}
