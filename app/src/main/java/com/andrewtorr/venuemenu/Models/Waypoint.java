package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.File;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("Waypoint")
public class Waypoint extends ParseObject {
    private ParseGeoPoint point;
    private String name;
    private ParseFile imagePFile;

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public ParseFile getImagePFile() {
        return getParseFile("imagePFile");
    }

    public void setImagePFile(ParseFile imagePFile) {
        put("imagePFile", imagePFile);
    }

    public ParseGeoPoint getPoint() {
        return getParseGeoPoint("point");
    }

    public void setPoint(ParseGeoPoint point) {
        put("point", point);
    }

    public Waypoint() {}

    public Waypoint(String wname, double wlat, double wlng, File imageFile) {
        super();
        setName(wname);
        setPoint(new ParseGeoPoint(wlat, wlng));

        //TODO: Save image

    }
}
