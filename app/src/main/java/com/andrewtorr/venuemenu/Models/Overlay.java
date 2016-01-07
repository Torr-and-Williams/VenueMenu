package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("Overlay")
public class Overlay extends ParseObject {
    private Lot lot;
    private Layer layer;
    private ParseFile image;
}
