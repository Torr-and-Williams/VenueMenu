package com.andrewtorr.venuemenu.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
@ParseClassName("User")
public class User extends ParseUser {
    private String username;
    private ParseFile avatar;
    private Layer userLayer;
}