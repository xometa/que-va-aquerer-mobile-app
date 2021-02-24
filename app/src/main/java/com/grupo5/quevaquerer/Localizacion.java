package com.grupo5.quevaquerer;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.grupo5.quevaquerer.props.Constantes;

public class Localizacion implements LocationListener {
    Context c;

    public Localizacion(Context c) {
        this.c = c;
    }

    @Override
    public void onLocationChanged(Location location) {
        Constantes.longitude = location.getLongitude();
        Constantes.latitude = location.getLatitude();
        Log.d("", "Latitud: " + location.getLatitude());
        Log.d("", "Longitud: " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider
                    .AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider
                    .OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider
                    .TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("GPS", "GPS activado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("GPS", "GPS desactivado");
    }
}
