package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("Waypoint")
public class Waypoint extends ParseObject {
    private String name;
    private double lat;
    private double lng;
    private ParseFile imagePFile;

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public double getLat() {
        return getDouble("lat");
    }

    public void setLat(double lat) {
        put("lat", lat);
    }

    public double getLng() {
        return getDouble("lng");
    }

    public void setLng(double lng) {
        put("lng", lng);
    }

    public ParseFile getImagePFile() {
        return getParseFile("imagePFile");
    }

    public void setImagePFile(ParseFile imagePFile) {
        put("imagePFile", imagePFile);
    }

    public Waypoint() {}

    public Waypoint(String wname, double wlat, double wlng) {
        super();
        setName(wname);
        setLat(wlat);
        setLng(wlng);
    }
}
