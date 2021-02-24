package com.grupo5.quevaquerer.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.client.MyOrderDaySubItemAdapter;
import com.grupo5.quevaquerer.entity.DetailsOrder;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.ToastAlert;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyOrderActivity extends AppCompatActivity implements OnMapReadyCallback {
    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView total, address;
    OrderHeader header = null;
    MyOrderDaySubItemAdapter myorder;
    DecimalFormat df = new DecimalFormat("0.00");
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.icons));
        setContentView(R.layout.activity_my_order);
        setTitle("Información del pedido");
        recyclerView = findViewById(R.id.listaproductos);
        total = findViewById(R.id.total);
        address = findViewById(R.id.direccion);
        toolbar = findViewById(R.id.toolbarD);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        header = (OrderHeader) getIntent().getExtras().getSerializable("orderHeader");
        if (header != null) {
            orderInformation();
        } else {
            new ToastAlert(0, "La información del pedido no se puede visualizar.", this, getLayoutInflater()).toastPersonalization();
        }
    }

    public void orderInformation() {
        total.setText(df.format(header.getTotalorder()));
        if (header.getLatitude() == 0 && header.getLongitude() == 0) {
            address.setText("No se ha especificado una dirección de envío");
        } else {
            address.setText(Props.getLocationName(this, header.getLatitude(), header.getLongitude()));
        }
        myorder = new MyOrderDaySubItemAdapter(this, header.getDetailsOrders());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myorder);
    }

    public void onVolver(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (header != null) {
            mMap = googleMap;
            LatLng ubication = new LatLng(header.getLatitude(), header.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(ubication)
                    .title("Lugar del envío")
                    .draggable(true));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 10.1f));
        }
    }
}
