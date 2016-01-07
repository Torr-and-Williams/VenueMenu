package com.andrewtorr.venuemenu.Models;

import android.graphics.Matrix;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("Pathnet")
public class Pathnet extends ParseObject {
    private ArrayList<LatLng> points;
    private Matrix pathmatrix; //TODO <-- make a custom Matrix object
}
