package com.example.sunsun.shoppingrouteguide;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

import static com.example.sunsun.shoppingrouteguide.R.id.from_edittext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private TextView txtview;
    private TextView cnt;
    private int PROXIMITY_RADIUS1 =100;
    private int PROXIMITY_RADIUS2 =250;
    int count=0;
    int size;
    int interval;
    List<Double>lng = new  ArrayList<>();
    List<Double>lat = new  ArrayList<>();
   /* String text_from;
    String text_to;*/
    String text_via;
    String origin;
    String destination;

    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtview = (TextView) findViewById(R.id.via);

       /* btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);


        Intent i = getIntent();
        text_from = i.getStringExtra ( "UserInput" );

        etOrigin.setText ( text_from );
        //etOrigin.setEnable(false);

        etDestination = (EditText) findViewById(R.id.etDestination);
        Intent j = getIntent();
        text_to = i.getStringExtra ( "UserInput2" );
        etDestination.setText ( text_to );*/
        Intent i = getIntent();
        origin = i.getStringExtra ( "UserInput" );
        Intent j = getIntent();
        destination= i.getStringExtra ( "UserInput2" );

        text_via = i.getStringExtra("UserInput3");
        txtview.setText(text_via);
        myDb = new DatabaseHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            /*final AlertDialog alert = builder.create();
            alert.show();*/
                View mView = getLayoutInflater().inflate(R.layout.save_route, null);
                final EditText from = (EditText) mView.findViewById(from_edittext);
                final EditText to = (EditText) mView.findViewById(R.id.to_edittext);
                final EditText via = (EditText) mView.findViewById(R.id.via_edittext);
                final EditText description = (EditText) mView.findViewById(R.id.des_edittext);
                from.setText(origin);
                to.setText(destination);
                via.setText(text_via);


                Button msubmit = (Button) mView.findViewById(R.id.savebutton);


                msubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (description.getText().toString().isEmpty()) {
                            Toast.makeText(MapsActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                        } else {


                            boolean isInserted = myDb.insertData(from.getText().toString(), to.getText().toString(),
                                    via.getText().toString(), description.getText().toString());
                            if(isInserted == true)
                                Toast.makeText(MapsActivity.this,"Your route has been saved",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(MapsActivity.this,"Error saving Route",Toast.LENGTH_LONG).show();

                           // Toast.makeText(MapsActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MapsActivity.this, MainActivity.class);
                            startActivity(i);

                        }

                    }
                });

                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
       sendRequest();

      /*  btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });*/
    }

    private void sendRequest() {
       // mMap.clear();
        /*String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();


        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
*/
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    private String getUrl (double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS1);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCZOnPHfndzbbhBxlFOdtbjSTyD0zQsklo");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
    private String getUrl1 (double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS2);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCZOnPHfndzbbhBxlFOdtbjSTyD0zQsklo");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            for(int j=0;j<route.points.size();j++) {
                lng.add(j,route.points.get(j).longitude);
                lat.add(j,route.points.get(j).latitude);
               /* lng[j]=route.points.get(j).longitude;
                lat[j]=route.points.get(j).latitude;*/

            }
            size = route.points.size();
            interval=size/8;

        }
        String text = txtview.getText().toString();
        String Value;


        if(text.equals("Restaurant"))
        {
            Toast.makeText(MapsActivity.this, "size"+size, Toast.LENGTH_LONG).show();
            Value = "restaurant";


            for (int i = 0; i < size; ) {

                String url = getUrl1 (lat.get(i), lng.get(i), Value);
                // String url = getUrl(lat[i], lng[i], Value);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                i=i+interval;
            }




            // Toast.makeText(MapsActivity.this, "Restaurants on the way", Toast.LENGTH_LONG).show();
        }
        else if (text.equals("Hospital"))
        {
            Value="hospital";
            for (int i = 0; i < size; ) {

                String url = getUrl1 (lat.get(i), lng.get(i), Value);
                // String url = getUrl(lat[i], lng[i], Value);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                i=i+interval;
            }

            Toast.makeText(MapsActivity.this,"Hospitals on the way", Toast.LENGTH_LONG).show();
        }

        else if (text.equals("Department store"))
        {


            Value = "department_store";
            for (int i = 0; i< size; ) {
                String url = getUrl1 (lat.get(i), lng.get(i), Value);
                // String url = getUrl(lat[i], lng[i], Value);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                i=i+interval;

            }

            Toast.makeText(MapsActivity.this,"Department stores on the way", Toast.LENGTH_LONG).show();
        }
        else if (text.equals("ATM"))
        {


            Value = "atm";
            for (int i = 0; i < size; ) {
                String url = getUrl1 (lat.get(i), lng.get(i), Value);
                // String url = getUrl(lat[i], lng[i], Value);

                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                i=i+interval;
            }

            Toast.makeText(MapsActivity.this,"ATMs on the way", Toast.LENGTH_LONG).show();
        }
        else if (text.equals("Petrol pump"))
        {
            Value="gas_station";
            for (int i = 0; i < size; ) {

                String url = getUrl1 (lat.get(i), lng.get(i), Value);
                // String url = getUrl(lat[i], lng[i], Value);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                i=i+interval;
            }

            Toast.makeText(MapsActivity.this,"Petrol pumps on the way", Toast.LENGTH_LONG).show();
        }


    }
}





