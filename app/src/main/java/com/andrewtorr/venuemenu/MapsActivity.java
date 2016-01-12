package com.andrewtorr.venuemenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewtorr.venuemenu.Models.Waypoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    @Bind(R.id.add_marker_button)
    FloatingActionButton addMarkerFab;
    @Bind(R.id.edit_layer_button)
    FloatingActionButton editLayerFab;
    @Bind(R.id.add_lot_button)
    FloatingActionButton addLotButton;
    @Bind(R.id.image_preview)
    ImageView preview;
    @Bind(R.id.confirm_overlay)
    FloatingActionButton overlayConfirm;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private Context context;

    private String TAG = "MapsAcivity";

    private boolean leave = true;
    private boolean mainBtns = true;
    private boolean overlayMode = false;
    private boolean pathMode = false;
    private boolean lotMode = false;

    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basics
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Butterknife
        ButterKnife.bind(this);

        //Parse Stuff


        //Set Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(4);
        mMap.setMyLocationEnabled(true);

        //set search scale to between 0.1 and 10 km
        //query layers within radius (actually a square of sides 2r, for now at least)

        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.mipmap.trollface);
        mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(image)
                .position(new LatLng(39.773194, -86.158391), 500)
                .transparency((float) 0.5));

        BitmapDescriptor campusmap = BitmapDescriptorFactory.fromResource(R.mipmap.campusmap);
        mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(campusmap)
                .position(new LatLng(39.774600, -86.176680), 2264)
                .transparency((float) 0.5));

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Map Activity", "marker clicked!");
                return false;
            }
        });

        zoomInOnMe();
    }






    //Buttons

    @OnClick(R.id.add_marker_button)
    public void addMarker() {
        Location location;

        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            if (location != null) {
                //Make a new waypoint
                //TODO: Throw up confirmation dialog before saving - set name, upload image etc there
                Waypoint waypoint = new Waypoint("Test", location.getLatitude(), location.getLongitude());

                waypoint.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(context, "Waypoint saved!", Toast.LENGTH_SHORT).show();
                    }
                });

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.addMarker(markerOptions);

                zoomInOnMe();
            }
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }
    }

    @OnClick(R.id.edit_layer_button)
    public void editLayer() {
        addMarkerFab.setVisibility(View.INVISIBLE);
        editLayerFab.setVisibility(View.INVISIBLE);

        showMenu();
    }

    @OnClick(R.id.add_lot_button)
    public void addLot() {
        if (lotMode) {
            Log.d(TAG, "ADD lot mode Go!");
            //TODO: Make a canvas "visible", set up a touch listener to draw a point when the touch ends
            //(Use editImageView in Do as a template)

            //TODO: Set up an on touch listener for the point, moves while touching?

            //TODO: OR set up touch listener to check to see if it's near a point at the beginning? - that sounds more complicated

            //TODO: Add points to an array list

            //TODO: Draw lines between all the points in order

            //TODO: After at least two points have been drawn, allow snapping to the first point to complete the shape

            //TODO: Prevent the lines from crossing somehow???

            //TODO: If you click on an existing point on an existing lot, it should set the first point for the new lot there

        } else if (overlayMode) {
            Log.d(TAG, "ADD overlay mode Go!");
            preview.setVisibility(View.VISIBLE);
            mMap.getUiSettings().setTiltGesturesEnabled(false);

            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);

        } else if (pathMode) {
            Log.d(TAG, "ADD path mode Go!");

        }
    }

    @OnClick(R.id.confirm_overlay)
    public void confirmOverlay() {
        Log.d(TAG, "setting Overlay");
        //TODO: Figure out math for width

        //TODO: Rotate image depending on portrait/landscape

        //TODO: Log statement whenever zoom changed - get zoom level

        //TODO: Try various widths, compare to zoom level - reverse-engineer algorithm

        BitmapDescriptor image = BitmapDescriptorFactory.fromPath(picturePath);
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions();
        float width;
        float zoom = mMap.getCameraPosition().zoom;
        width = (float) (200000 / Math.pow(zoom, 3));
        Log.d(TAG, "width: " + width);

        LatLng latLng = mMap.getCameraPosition().target;

        overlayOptions.position(latLng, width);
        overlayOptions.bearing(mMap.getCameraPosition().bearing);
        overlayOptions.image(image);

        mMap.addGroundOverlay(overlayOptions);

        Log.d(TAG, "done setting overlay");
        preview.setImageBitmap(null);
        preview.setVisibility(View.INVISIBLE);
        overlayConfirm.setVisibility(View.INVISIBLE);
        addLotButton.setVisibility(View.VISIBLE);
    }




    //Functions

    public void zoomInOnMe() {
        Location location; //location
        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .bearing(45)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }
    }

    public void goFlat() {
        Location location; //location
        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(20)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }
    }

    public void showMenu() {
        leave = true;
        Log.d("Main Activity", "Adding menu view");

        final Dialog dialog = new Dialog(MapsActivity.this, R.style.Dialog_Blank);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater dialog_inflater = LayoutInflater.from(getApplicationContext());
        final View dialog_view = dialog_inflater.inflate(R.layout.menu_holder, null);

        final ViewGroup holder = (ViewGroup) dialog_view.findViewById(R.id.menu_holder);
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.bottom_menu, holder, false);

        TranslateAnimation animateIn = new TranslateAnimation(0,0,256,0);
        animateIn.setDuration(250);
        newView.setAnimation(animateIn);
        holder.addView(newView, 0);

        final TextView button1 = (TextView) dialog_view.findViewById(R.id.button1);
        final TextView button2 = (TextView) dialog_view.findViewById(R.id.button2);
        final TextView button3 = (TextView) dialog_view.findViewById(R.id.button3);
        final TextView button4 = (TextView) dialog_view.findViewById(R.id.button4);

        final ImageButton topView = (ImageButton) dialog_view.findViewById(R.id.top_view);

        final TranslateAnimation animateOut = new TranslateAnimation(0, 0, 0, 256);
        animateOut.setDuration(250);
        newView.setAnimation(animateOut);

        View.OnClickListener m_clickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View p_v) {
                switch (p_v.getId()) {
                    case R.id.button1:
                        Log.d("Main Activity", "add marker button");

                        leave = false;
                        mainBtns = true;
                        addMarker();
                        break;

                    case R.id.button2:
                        Log.d("Main Activity", "set lot button");
                        addLotButton.setVisibility(View.VISIBLE);

                        mainBtns = false;
                        lotMode = true;
                        break;

                    case R.id.button3:
                        Log.d("Main Activity", "set pathnet button");
                        addLotButton.setVisibility(View.VISIBLE);

                        mainBtns = false;
                        pathMode = true;
                        break;

                    case R.id.button4:
                        Log.d("Main Activity", "set overlay button");
                        addLotButton.setVisibility(View.VISIBLE);

                        goFlat();
                        mainBtns = false;
                        overlayMode = true;
                        break;

                    case R.id.top_view:
                        mainBtns = true;
                        break;

                    default:
                        mainBtns = true;
                        break;
                }

                if (leave) {
                    newView.startAnimation(animateOut);

                    animateOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (mainBtns) {
                                addMarkerFab.setVisibility(View.VISIBLE);
                                editLayerFab.setVisibility(View.VISIBLE);
                            }

                            dialog.cancel();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            }
        };

        button1.setOnClickListener(m_clickListener);
        button2.setOnClickListener(m_clickListener);
        button3.setOnClickListener(m_clickListener);
        button4.setOnClickListener(m_clickListener);

        topView.setOnClickListener(m_clickListener);
        dialog.setContentView(dialog_view);

        newView.startAnimation(animateIn);
        dialog.show();
    }

    private void editPaths() {
        //TODO: Set up system similar to Lots

    }

    private void editLots() {
        addLotButton.setVisibility(View.VISIBLE);

        //TODO: Set up onclick listeners on existing lots to edit attributes (change image, title, description, etc.)
        //Later: add ability to set Lat & Lng explicitly for the corners

        //TODO: Set up long click listeners to edit vertices
    }

    private void editOverlays() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Log.d(TAG,"image loaded successfully!");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            preview.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            addLotButton.setVisibility(View.INVISIBLE);
            overlayConfirm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (lotMode || overlayMode || pathMode) {
            addMarkerFab.setVisibility(View.VISIBLE);
            editLayerFab.setVisibility(View.VISIBLE);
            addLotButton.setVisibility(View.INVISIBLE);
            preview.setVisibility(View.INVISIBLE);
            overlayConfirm.setVisibility(View.INVISIBLE);
            lotMode = false;
            overlayMode = false;
            pathMode = false;
        } else {
            super.onBackPressed();
        }
    }
}
