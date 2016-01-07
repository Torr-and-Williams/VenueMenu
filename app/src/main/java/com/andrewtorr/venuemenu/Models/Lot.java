package com.andrewtorr.venuemenu.Models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 12/31/2015.
 *
 */
@ParseClassName("Lot")
public class Lot extends ParseObject {
    private ArrayList<LatLng> corners;
    private String name;
    private Layer layer;
}
