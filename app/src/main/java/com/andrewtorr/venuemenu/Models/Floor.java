package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Andrew on 1/7/2016.
 *
 */
@ParseClassName("Floor")
public class Floor extends ParseObject {
    private Layer layer;
    private String name;
    private Integer floor;
}
