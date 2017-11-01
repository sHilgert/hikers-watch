package com.sergiohilgert.hikerswatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
  
  LocationManager locationManager;
  LocationListener locationListener;
  
  public void updateLocationInfo(Location location){
    TextView latTextView = (TextView) findViewById(R.id.latitudeTextView);
    TextView longTextView = (TextView) findViewById(R.id.longitudeTextView);
    TextView accTextView = (TextView) findViewById(R.id.accTextView);
    TextView altTextView = (TextView) findViewById(R.id.altitudeTextView);
    TextView addTextView = (TextView) findViewById(R.id.addressTextView);
    
    latTextView.setText("Latitude: " + location.getLatitude());
    longTextView.setText("Longitude: " + location.getLongitude());
    accTextView.setText("Accuracy: " + location.getAccuracy());
    altTextView.setText("Altitude: " + location.getAltitude());
    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
  
    try {
      List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
      String result = "Could not find address.";
      if(addressList != null && addressList.size() > 0){
        result = "Address: ";
        if(addressList.get(0).getSubThoroughfare() != null){
          result += addressList.get(0).getSubThoroughfare().toString() + " ";
        }
        if(addressList.get(0).getThoroughfare() != null){
          result += addressList.get(0).getThoroughfare().toString() + "\n";
        }
        if(addressList.get(0).getLocality() != null){
          result += addressList.get(0).getLocality().toString() + "\n";
        }
        if(addressList.get(0).getPostalCode() != null){
          result += addressList.get(0).getPostalCode().toString() + "\n";
        }
        if(addressList.get(0).getCountryName() != null){
          result += addressList.get(0).getCountryName().toString() + "\n";
        }
        
      }
      addTextView.setText(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
      startListening();
    }
  }
  
  public void startListening(){
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
      locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
       updateLocationInfo(location);
    
      }
  
      @Override
      public void onStatusChanged(String s, int i, Bundle bundle) {
    
      }
  
      @Override
      public void onProviderEnabled(String s) {
    
      }
  
      @Override
      public void onProviderDisabled(String s) {
    
      }
    };
    if(Build.VERSION.SDK_INT < 23){
      startListening();
    }else{
      if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
      }else{
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
          updateLocationInfo(location);
        }
      }
    }
  }
}
