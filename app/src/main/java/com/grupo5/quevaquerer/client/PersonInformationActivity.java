package com.grupo5.quevaquerer.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;

public class PersonInformationActivity extends AppCompatActivity implements OnMapReadyCallback {
    Client client = null;
    ImageView image;
    Toolbar toolbar;
    TextView name, phone, user, password, address;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.icons));
        setContentView(R.layout.activity_person_information);
        setTitle("Información del cliente");
        image = findViewById(R.id.imagencliente);
        name = findViewById(R.id.nombrecliente);
        phone = findViewById(R.id.telefono);
        user = findViewById(R.id.usuario);
        password = findViewById(R.id.contrasena);
        address = findViewById(R.id.direccionpedido);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = (Client) getIntent().getExtras().getSerializable("client");
        if (client != null) {
            dataClient();
        } else {
            new ToastAlert(0, "La información del cliente no se puede visualizar.", this, getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dataClient() {
        String url = client.getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(image);
        } else {
            image.setImageResource(R.drawable.sinfoto);
        }
        name.setText(client.getName() + " " + client.getLastname());
        phone.setText(Html.fromHtml("<b>Teléfono: </b>" + client.getPhone()));
        user.setText(Html.fromHtml("<b>Usuario: </b>" + client.getUsername()));
        password.setText(Html.fromHtml("<b>Contraseña: </b>" + client.getUserpassword()));
        if (client.getLatitude() == 0 && client.getLongitude() == 0) {
            address.setText("No se ha registrado una dirección");
        } else {
            address.setText(Props.getLocationName(this, client.getLatitude(), client.getLongitude()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (client != null) {
            mMap = googleMap;
            LatLng ubication = new LatLng(client.getLatitude(), client.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(ubication)
                    .title("Ubicación de " + client.getName())
                    .draggable(true));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 10.1f));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
