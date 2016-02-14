package com.andrewtorr.venuemenu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andrewtorr.venuemenu.Models.Client;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    @Bind(R.id.add_marker_button)
    FloatingActionButton addMarkerFab;
    @Bind(R.id.edit_layer_button)
    FloatingActionButton editLayerFab;
    @Bind(R.id.add_lot_button)
    FloatingActionButton addLotButton;
    @Bind(R.id.confirm_overlay)
    FloatingActionButton overlayConfirm;
    @Bind(R.id.maps_layout)
    RelativeLayout map_layout;
    @Bind(R.id.lots_drawing)
    ImageView lots_drawing;
    @Bind(R.id.image_preview)
    ImageView preview;
    @Bind(R.id.lot_corner)
    ImageView lot_corner;

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
    private boolean newPoint = false;
    private boolean movingCorner = false;
    private boolean lotClosed = false;

    private String picturePath;

    private Bitmap drawing;
    private Canvas canvas;
    private Paint paint;
    private int x;
    private int y;
    private RelativeLayout.LayoutParams params;
    private DisplayMetrics displayMetrics;
    //private int width;
    //private int height;
    private boolean flatMode = false;

    private ArrayList<Point> points;

    private ParseQuery<Client> clientQuery;
    ArrayList<Client> visibleClients;

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

        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();

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

        if (!mMap.getUiSettings().isTiltGesturesEnabled()) {
            mMap.getUiSettings().setTiltGesturesEnabled(true);
        }

        //TODO: Load all local public map data

        try {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            final ParseGeoPoint here = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            clientQuery = ParseQuery.getQuery(Client.class);
            clientQuery.whereWithinKilometers("center", here, 5);
            clientQuery.findInBackground(new FindCallback<Client>() {
                @Override
                public void done(List<Client> clients, ParseException e) {
                    if (e == null) {
                        if (clients.size() > 0) {
                            Log.d(TAG, "local clients found!");
                        } else {
                            Log.d(TAG, "no local clients found, creating 3 dummy clients");
                            Client clientA = new Client(here.getLatitude(), here.getLongitude(), here.getLatitude() - 0.001, here.getLongitude() - 0.002, "Test Client A", 0xff6400);
                            Client clientB = new Client(here.getLatitude() + 0.002, here.getLongitude() + 0.001, here.getLatitude() + 0.001, here.getLongitude() - 0.001, "Test Client B", 0x3F51A5);
                            Client clientC = new Client(here.getLatitude() - 0.001, here.getLongitude() + 0.003, here.getLatitude() - 0.003, here.getLongitude() + 0.002, "Test Client C", 0x2FA521);

                            clients.add(clientA);
                            clients.add(clientB);
                            clients.add(clientC);

                            clientA.saveInBackground();
                            clientB.saveInBackground();
                            clientC.saveInBackground();

                            Log.d(TAG, "dummy clients saved!");
                        }


                        Log.d(TAG, "Showing local clients");
                        for (Client client : clients) {
                            Log.d(TAG, client.getClientName() + " - N: " + client.getNbound() + " S: " + client.getSbound() + " E: " + client.getEbound() + " W: " + client.getWbound());
                            Location temp = new Location(LocationManager.GPS_PROVIDER);
                            Location side = new Location(LocationManager.GPS_PROVIDER);
                            LatLng center = client.getCenterLatLng();
                            temp.setLatitude(center.latitude);
                            temp.setLongitude(center.longitude);
                            side.setLatitude(center.latitude);
                            side.setLongitude(client.getEbound());

                            float overlayWidth = temp.distanceTo(side) * 2;
                            int height = (int) Math.round((client.getNbound() - client.getSbound())*100000);
                            int width = (int) Math.round((client.getEbound() - client.getWbound())*100000);

                            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.client_square);

                            Canvas canvas = new Canvas(image);
                            Paint paint = new Paint();
                            ColorFilter filter = new LightingColorFilter(client.getColor(), 0);
                            paint.setColorFilter(filter);
                            Matrix matrix = new Matrix();
                            // resize the bit map
                            if (height > width) {
                                matrix.postScale(1, ((float) width) / ((float) height));
                            } else {
                                matrix.postScale(((float) height) / ((float) width), 1);
                            }

                            canvas.drawBitmap(source, matrix, paint);
                            BitmapDescriptor imaged = BitmapDescriptorFactory.fromBitmap(image);

                            //BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.client_square);
                            mMap.addGroundOverlay(new GroundOverlayOptions()
                                    .image(imaged)
                                    .position(client.getCenterLatLng(), overlayWidth)
                                    .transparency((float) 0.5));
                        }

                        visibleClients = new ArrayList<>(clients);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "map clicked! lat: " + latLng.latitude + ", lng : " + latLng.longitude);
                for (Client client : visibleClients) {
                    double Nbound = client.getNbound();
                    double Ebound = client.getEbound();
                    double Sbound = client.getSbound();
                    double Wbound = client.getWbound();
                    String name = client.getClientName();

                    //TODO: Handle client overlap!!

                    /*
                    if ((latLng.latitude <= Nbound) && (latLng.latitude >= Sbound)) {
                        Log.d(TAG, "clicked between vertical bounds of " + name);
                    }
                    if ((latLng.longitude <= Ebound) && (latLng.longitude >= Wbound)) {
                        Log.d(TAG, "clicked between horizontal bounds of " + name);
                    }
                    */

                    if ((latLng.latitude <= Nbound) && (latLng.latitude >= Sbound) && (latLng.longitude <= Ebound) && (latLng.longitude >= Wbound)) {
                        Log.d(TAG, "Client " + name + " clicked!");
                        zoomInOnRegion(client);
                    }
                }
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Map Activity", "marker clicked!");
                return false;
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d(TAG, "zoom: " + cameraPosition.zoom);
            }
        });

        zoomInOnMe(15);
    }




    // Functions

    public void addMarker(View v) {
        Location location; //Location

        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            if (location != null) {
                //Make a new waypoint
                //TODO: Throw up confirmation dialog before saving - set name, upload image etc there
                //TODO: Just start with the name for now. Create a view with an edittext in that becomes visible when this happens


                Waypoint waypoint = new Waypoint("Test", location.getLatitude(), location.getLongitude(), null);

                waypoint.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(context, "Waypoint saved!", Toast.LENGTH_SHORT).show();
                    }
                });

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.addMarker(markerOptions);

                zoomInOnMe(18);
            }
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }
    }

    public void editLayer(View v) {
        addMarkerFab.setVisibility(View.INVISIBLE);
        editLayerFab.setVisibility(View.INVISIBLE);

        showMenu();
    }

    public void addLot(View v) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //width = size.x;
        //height = size.y;

        if (lotMode) {
            Log.d(TAG, "ADD lot mode Go!");
            //(Use editImageView in Do as a template)
            points = new ArrayList<>();
            Log.d(TAG, "child count: " + map_layout.getChildCount());
            for (int i = 1; i < map_layout.getChildCount(); i++) {
                View view = map_layout.getChildAt(i);
                if ((view.getTag() != null) && (view.getTag() != "0")) {
                    map_layout.removeViewAt(i);
                }
            }

            drawing = Bitmap.createBitmap(lots_drawing.getWidth(), lots_drawing.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(drawing);
            paint = new Paint();
            paint.setStrokeWidth(10);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(ContextCompat.getColor(context, R.color.colorAccent));

            final float statusBarHeight = getResources().getDimension(getResources().getIdentifier("status_bar_height", "dimen", "android"));

            Log.d(TAG, "status bar height - " + statusBarHeight);

            lots_drawing.setVisibility(View.VISIBLE);

            preview.setVisibility(View.VISIBLE);
            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    x = Math.round(event.getRawX());
                    y = Math.round(event.getRawY() - statusBarHeight);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        lot_corner.setVisibility(View.VISIBLE);
                        if (!movingCorner) {
                            Log.d(TAG, "make a new Lot Corner");
                            params = (RelativeLayout.LayoutParams) lot_corner.getLayoutParams();
                            params.setMargins(x, y, 0, 0);
                            lot_corner.setLayoutParams(params);
                            lot_corner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lot_corner_orange));

                            if (points != null) {
                                lot_corner.setTag(points.size() - 1);
                            } else {
                                lot_corner.setTag(0);
                            }

                            newPoint = true;
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        Log.d(TAG, "move corner");
                        params = (RelativeLayout.LayoutParams) lot_corner.getLayoutParams();
                        params.setMargins(x, y, 0, 0);
                        lot_corner.setLayoutParams(params);
                        lot_corner.refreshDrawableState();

                        //Update the lot outline
                        if ((lot_corner.getTag() != null) && (points.size() > 0)) {
                            Log.d(TAG, "moving corner: " + movingCorner);
                            int index = Integer.parseInt(lot_corner.getTag().toString());
                            Log.d(TAG, "lot tag: " + index);
                            Point point = new Point(x, y);

                            if (movingCorner) {
                                Log.d(TAG, "new corner at index");
                                points.remove(index);
                                points.add(index, point);
                                drawLot();

                            } else {
                                Log.d(TAG, "new corner at " + (index + 1));
                                points.add(index + 1, point);
                                points.remove(index + 1);
                                drawLot();

                            }
                        } else {
                            Log.e(TAG, "ERROR 2! Cannot update Lot outline!");
                        }


                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (newPoint) {
                            lot_corner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lot_corner_white));
                            Log.d(TAG, "save corner");
                            Point point = new Point(x, y);
                            points.add(point);
                            Log.d(TAG, points.size() + " points");
                            newPoint = false;

                            lot_corner.setVisibility(View.INVISIBLE);
                            ImageView cornerView = new ImageView(getApplicationContext());
                            cornerView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lot_corner_white));
                            RelativeLayout.LayoutParams cParams = new RelativeLayout.LayoutParams(Math.round(32 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(32 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
                            cParams.setMargins(x, y, 0, 0);
                            cornerView.setLayoutParams(cParams);
                            //Save the position of the point for deletion
                            cornerView.setTag(points.size() - 1);
                            map_layout.addView(cornerView);
                            drawLot();

                            //Set a new onClickListener on the corner to move it
                            cornerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "corner tapped");
                                    if (movingCorner) {
                                        Log.d(TAG, "moving corner false");
                                        lot_corner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lot_corner_orange));
                                        movingCorner = false;
                                    } else {
                                        lot_corner = (ImageView) view;
                                        Log.d(TAG, "moving corner true");
                                        lot_corner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lot_corner_white));
                                        movingCorner = true;
                                    }
                                }
                            });

                            //Set long click listener to delete the corner
                            cornerView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(final View view) {
                                    //final View theView = view;
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(MapsActivity.this, R.style.StyledDialog);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Delete this point?");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            points.remove(Integer.parseInt(view.getTag().toString()));
                                            map_layout.removeView(view);
                                            drawLot();
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Nvm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                    return false;
                                }
                            });
                        } else {
                            //Change existing point
                            Log.d(TAG, "moving existing corner");
                        }

                    }
                    return true;
                }
            });

            //TODO: After at least three points have been created, allow snapping to the first point to complete the shape

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

    public void confirmOverlay(View v) {
        Log.d(TAG, "setting Overlay");

        BitmapDescriptor image = BitmapDescriptorFactory.fromPath(picturePath);
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        double zoom = mMap.getCameraPosition().zoom;
            double width = 15450000.0 / Math.pow(2, (zoom - 1));
        Log.d(TAG, "width: " + width);

        LatLng latLng = mMap.getCameraPosition().target;

            overlayOptions.position(latLng, (float) width);
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

    public void zoomInOnMe(int zoom) {
        Location location; //location
        try {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(zoom)                   // Sets the zoom
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
        flatMode = true;
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
                mMap.getUiSettings().setTiltGesturesEnabled(false);
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

        TranslateAnimation animateIn = new TranslateAnimation(0,0,250,0);
        animateIn.setDuration(250);
        newView.setAnimation(animateIn);
        holder.addView(newView, 0);

        final LinearLayout button1 = (LinearLayout) dialog_view.findViewById(R.id.button1);
        final LinearLayout button2 = (LinearLayout) dialog_view.findViewById(R.id.button2);
        final LinearLayout button3 = (LinearLayout) dialog_view.findViewById(R.id.button3);
        final LinearLayout button4 = (LinearLayout) dialog_view.findViewById(R.id.button4);

        final ImageButton topView = (ImageButton) dialog_view.findViewById(R.id.top_view);

        final TranslateAnimation animateOut = new TranslateAnimation(0, 0, 0, 250);
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
                        addMarker(null);
                        break;

                    case R.id.button2:
                        Log.d("Main Activity", "set pathnet button");
                        addLotButton.setVisibility(View.VISIBLE);

                        mainBtns = false;
                        pathMode = true;
                        break;

                    case R.id.button3:
                        Log.d("Main Activity", "set lot button");
                        addLotButton.setVisibility(View.VISIBLE);

                        goFlat();
                        mainBtns = false;
                        lotMode = true;
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

    private void drawLot() {
        drawing = Bitmap.createBitmap(lots_drawing.getWidth(), lots_drawing.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(drawing);

        if (lotClosed) {
            Log.d(TAG, "lot is closed, draw a polygon");
        } else {
            if ((points != null) && (points.size() > 1)) {
                for (int i = 1; i < points.size(); i++) {
                    int offset = Math.round(16 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    //int offset = 32;
                    Point start = points.get(i - 1);
                    Point end = points.get(i);
                    Log.d(TAG, "start: " + start.x + ", " + start.y + ", end:" + end.x + ", " + end.y);
                    canvas.drawLine(start.x + offset, start.y + offset, end.x + offset, end.y + offset, paint);
                }
            }
        }



        canvas.drawBitmap(drawing, 0, 0, null);
        Log.d(TAG, "drawing lot outline");
        lots_drawing.setImageDrawable(new BitmapDrawable(getResources(), drawing));
    }

    public void zoomInOnRegion(Client client) {
        //Location location = new Location("null provider");
        flatMode = true;
        try {
            Integer zoomLevel;
            double ClientWidth;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(client.getCenterLatLng(), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(client.getCenterLatLng())      // Sets the center of the map to location user
                    .zoom(20)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        } catch (SecurityException e) {
            Log.e("Error:", e.getLocalizedMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Log.d(TAG, "image loaded successfully!");
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
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
            Log.d(TAG, "back - in an edit mode");
            addMarkerFab.setVisibility(View.VISIBLE);
            editLayerFab.setVisibility(View.VISIBLE);
            addLotButton.setVisibility(View.INVISIBLE);
            preview.setVisibility(View.INVISIBLE);
            overlayConfirm.setVisibility(View.INVISIBLE);
            lots_drawing.setVisibility(View.INVISIBLE);
            overlayMode = false;
            pathMode = false;

            if (lotMode) {
                Log.d(TAG, "child count: " + map_layout.getChildCount());
                for (int i = 1; i < map_layout.getChildCount(); i++) {
                    View view = map_layout.getChildAt(i);
                    if ((view.getTag() != null) && (view.getTag() != "0")) {
                        map_layout.removeViewAt(i);
                    }
                }
            }
        } else {
            Log.d(TAG, "back - not in an edit mode");
            super.onBackPressed();
        }
    }
}
