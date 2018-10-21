package more.william.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lanzar extends AppCompatActivity {

    private final static int LOCATION_PERMISSION = 0;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private static final double LATAEROPUERTO = 4.697730480112796;
    private static final double LONGAEROPUERTO = -74.14075972845345;
    private TextView longi;
    private TextView lat;
    //UNA SOLA VEZ
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView altitude;
    private Button refresh;
    private Button guardar;
    //ACTUALIZACION PERIODICA
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private TextView distaciaaero;
    private TextView guardadas;
    private JSONArray localizaciones;
    private List<String> listalocations;


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanzar);

        listalocations = new ArrayList<String>();

        guardadas = findViewById(R.id.guardadas);
        guardadas.setText("Fecha\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tLatitud\t\tLongitud\n");

        localizaciones = new JSONArray();


        //Log.i("11111111111111111111111", "Location update in the callback: ");
        distaciaaero = findViewById(R.id.distaciaaero);
        mLocationRequest = createLocationRequest();
        longi = findViewById(R.id.longitud);
        //Log.i("2222222222222222", "Location update in the callback: ");
        lat = findViewById(R.id.latitud);
        altitude = findViewById(R.id.altalt);
        refresh = findViewById(R.id.button2);
        guardar = findViewById(R.id.guardar);
        //Log.i("33333333333333333333", "Location update in the callback: ");
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                "Se necesita acceder a la ubicacion", LOCATION_PERMISSION);
        //Log.i("44444444444444444444", "Location update in the callback: ");

        mLocationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    lat.setText(String.valueOf(location.getLatitude()));
                    longi.setText(String.valueOf(location.getLongitude()));
                    altitude.setText(String.valueOf(location.getAltitude()));
                    distaciaaero.setText(
                            String.valueOf(distanceaero(location.getLatitude(),
                                    location.getLongitude(), LATAEROPUERTO, LONGAEROPUERTO)) + " Km");
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Log.i("555555555555555555555", "Location update in the callback: ");

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationView();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeJSONObject();
            }
        });
        //Log.i("6666666666666666", "Location update in the callback: ");
        //LocationView();
    }

    public double distanceaero(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private void requestPermission(Activity context, String permission, String explanation, int requestId) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
        }
    }


    private void LocationView() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                    new
                            OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    Log.i("LOCATION", "onSuccess location");
                                    if (location != null) {
                                        Log.i(" LOCATION ", "Longitud: " +
                                                location.getLongitude());
                                        longi.setText(String.valueOf(location.getLongitude()));
                                        lat.setText(String.valueOf(location.getLatitude()));
                                        altitude.setText(String.valueOf(location.getAltitude()));
                                    }
                                }
                            });


        }

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                LocationView();
                break;
            }
        }
    }


    private void writeJSONObject() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                    new  OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location mCurrentLocation) {



                                    MyLocation myLocation = new MyLocation();
                                    myLocation.setFecha(new Date(System.currentTimeMillis()));
                                    myLocation.setLatitud(mCurrentLocation.getLatitude());
                                    myLocation.setLongitud(mCurrentLocation.getLongitude());
                                    localizaciones.put(myLocation.toJSON());
                                    String temp = (String) guardadas.getText();
                                    temp = temp + myLocation.getFecha()+"\t"+myLocation.getLatitud()+"\t"+myLocation.getLongitud()+"\n";
                                    guardadas.setText(temp);

                                    Writer output = null;
                                    String filename = "locations.json";
                                    try {
                                        File file = new File(getBaseContext().getExternalFilesDir(null), filename);
                                        Log.i("LOCATION", "Ubicacion de archivo: " + file);
                                        output = new BufferedWriter(new FileWriter(file));
                                        output.write(localizaciones.toString());
                                        output.close();
                                        Toast.makeText(getApplicationContext(), "Location saved",
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    }

            });
         }
    }





}