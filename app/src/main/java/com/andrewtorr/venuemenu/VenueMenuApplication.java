package com.andrewtorr.venuemenu;

import android.app.Application;

import com.andrewtorr.venuemenu.Models.Client;
import com.andrewtorr.venuemenu.Models.Floor;
import com.andrewtorr.venuemenu.Models.Layer;
import com.andrewtorr.venuemenu.Models.Lot;
import com.andrewtorr.venuemenu.Models.Overlay;
import com.andrewtorr.venuemenu.Models.Pathnet;
import com.andrewtorr.venuemenu.Models.User;
import com.andrewtorr.venuemenu.Models.Waypoint;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Andrew on 12/13/2015.
 *
 */
public class VenueMenuApplication extends Application {
    private static VenueMenuApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        ParseUser.registerSubclass(User.class);
        ParseUser.registerSubclass(Client.class);
        ParseObject.registerSubclass(Layer.class);
        ParseObject.registerSubclass(Floor.class);
        ParseObject.registerSubclass(Lot.class);
        ParseObject.registerSubclass(Waypoint.class);
        ParseObject.registerSubclass(Overlay.class);
        ParseObject.registerSubclass(Pathnet.class);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }
}
