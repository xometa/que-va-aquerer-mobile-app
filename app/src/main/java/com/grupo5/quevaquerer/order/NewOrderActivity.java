package com.grupo5.quevaquerer.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grupo5.quevaquerer.Localizacion;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.url.UrlOrder;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.grupo5.quevaquerer.user.HomeUserActivity;
import com.grupo5.quevaquerer.user.LoginActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOrderActivity extends AppCompatActivity implements OrderItemClickListener, CallbackResponse {

    ConstraintLayout empty, basket;
    AdapterOrder adapterOrder;
    RecyclerView recyclerView;
    TextView click, total;
    Toolbar toolbar;
    DecimalFormat df = new DecimalFormat("0.00");
    Sgl sgl;
    Gson gson;
    RequestParams params;
    UrlProduct urlProduct;
    UrlClient urlClient;
    UrlOrder urlOrder;
    Client client;
    //int quantityAvailable = 0;
    //para los datos del cliente
    int idclient = 0;
    double latitude = 0;
    double longitude = 0;
    Date date;
    //para obtener las coordenadas del gps
    LocationManager locationManager;

    //para la verificación de cantidad
    int positionProduct = 0;
    TextView quantityProduct = null;
    Product selection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        setTitle("Mi pedido");
        toolbar = findViewById(R.id.toolbar);
        empty = findViewById(R.id.canastavacia);
        basket = findViewById(R.id.canastapedidos);
        recyclerView = findViewById(R.id.listapedidoproductos);
        click = findViewById(R.id.agregarcomida);
        total = findViewById(R.id.totalpedido);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sgl = new Sgl(this, this);
        urlProduct = new UrlProduct();
        urlClient = new UrlClient();
        urlOrder = new UrlOrder();
        gson = new Gson();
        params = new RequestParams();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            getStartedLocalization();
        }
        validate();
    }

    public void validate() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {
            latitude = client.getLatitude();
            longitude = client.getLongitude();
            myOrder();
            sgl.get(urlClient.getClient(HomeUserActivity.client.getId()), null, "getClient");
        } else {
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ubication_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navegacionVista();
                return true;
            case R.id.house_ubication:
                ubication("houseubication");
                return true;
            case R.id.ubication_person:
                ubication("ubicationperson");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ubication(String s) {
        if (s.equals("houseubication")) {
            if (client != null) {
                latitude = client.getLatitude();
                longitude = client.getLongitude();
                new ToastAlert(1, "Se ha seleccionado la ubicación de su casa.", NewOrderActivity.this, getLayoutInflater()).toastPersonalization();
            } else {
                new ToastAlert(2, "Su información ha sido alterada, por favor inicie sesión nuevamente.", NewOrderActivity.this, getLayoutInflater()).toastPersonalization();
                SharedPreferencesManager.destroySharedPreferences();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        } else {
            getStartedLocalization();
            latitude = Constantes.latitude;
            longitude = Constantes.longitude;
            new ToastAlert(1, "Se ha seleccionado la ubicación actual.", this, getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void onBackPressed() {
        navegacionVista();
    }

    public void navegacionVista() {
        finish();
        Intent i = new Intent(this, HomeUserActivity.class);
        startActivity(i);
    }

    public void addMoreFood(View v) {
        navegacionVista();
    }

    public void productOrderList() {
        adapterOrder = new AdapterOrder(this, Constantes.listaproductOrderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterOrder);
    }

    @Override
    public void onViesDetailsProduct(View view, OrderHeader orderHeader) {

    }

    @Override
    public void quantityOrder(View view, Product product, String option, TextView quantity, int position) {
        if (option.equals("+")) {
            positionProduct = position;
            quantityProduct = quantity;
            selection = product;
            sgl.get(urlProduct.getQuantityAvailable(product.getId()), null, "quantityAvailable");
        } else if (option.equals("-")) {
            if (Integer.parseInt(quantity.getText().toString()) > 1) {
                quantity.setText(String.valueOf(Integer.valueOf(quantity.getText().toString()) - 1));
                product.setQuantity(Integer.valueOf(quantity.getText().toString()));
            } else {
                quantity.setText("1");
                product.setQuantity(1);
            }
            Constantes.listaproductOrderList.set(position, product);
        } else if (option.equals("remove")) {
            if (product != null) {
                Constantes.listaproductOrderList.remove(position);
                myOrder();
            } else {
                new ToastAlert(0, "El producto no se puede eliminar", this, getLayoutInflater()).toastPersonalization();
            }
        } else {
            new ToastAlert(0, "Opción incorrecta", this, getLayoutInflater()).toastPersonalization();
        }
        //Log.d("producto", "quantityOrder: " + product.getQuantity());
        totalOrder();
    }

    @Override
    public void setStatusOrder(View view, OrderHeader orderHeader, int status) {

    }

    public void myOrder() {
        if (Constantes.listaproductOrderList.size() > 0) {
            empty.setVisibility(View.GONE);
            basket.setVisibility(View.VISIBLE);
            productOrderList();
            totalOrder();
        } else {
            empty.setVisibility(View.VISIBLE);
            basket.setVisibility(View.GONE);
        }
    }

    public void savePedido(View v) {
        date = new Date();
        params = new RequestParams();
        if (HomeUserActivity.client != null) {
            if (idclient > 0 && HomeUserActivity.client.getId() == idclient) {
                //parseamos el arreglo, para enviarlo al web services
                String productList = gson.toJson(Constantes.listaproductOrderList);
                if (Constantes.listaproductOrderList.size() > 0) {
                    if (!(latitude == 0 && longitude == 0)) {
                        //enviando parametros
                        params.put("option", "saveOrderClient");
                        params.put("idclient", idclient);
                        params.put("latitude", latitude);
                        params.put("longitude", longitude);
                        params.put("dateorder", Props.formatDate1(date));
                        params.put("totalorder", total.getText().toString());
                        params.put("statusorder", 0);
                        params.put("productlist", productList);
                        sgl.post(urlOrder.postSaveOrder(), params, "saveOrder");
                    } else {
                        new ToastAlert(2, "Especifique una dirección, para el envío del pedido.", this, getLayoutInflater()).toastPersonalization();
                    }
                } else {
                    new ToastAlert(2, "Agrega productos a la canasta, para poder realizar el pedido.", this, getLayoutInflater()).toastPersonalization();
                }
            } else {
                new ToastAlert(2, "La información de inicio de sesión ha sido alterada.", this, getLayoutInflater()).toastPersonalization();
                SharedPreferencesManager.destroySharedPreferences();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            }
        } else {
            new ToastAlert(0, "Inicia sesión, para poder realizar el pedido.", this, getLayoutInflater()).toastPersonalization();
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

    public void totalOrder() {
        double totalOrder = 0;
        for (int i = 0; i < Constantes.listaproductOrderList.size(); i++) {
            totalOrder += (Constantes.listaproductOrderList.get(i).getQuantity() * Constantes.listaproductOrderList.get(i).getPrice());
        }
        total.setText(String.valueOf(df.format(totalOrder)));
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("quantityAvailable")) {
            try {
                JSONObject obj = new JSONObject(data);
                int quantity = obj.getInt("quantity");
                int status = obj.getInt("status");
                String message = obj.getString("message");
                if (status == 0 || status == 2) {
                    new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                }
                //quantityAvailable = quantity;
                availableProduct(quantity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (option.equals("getClient")) {
            try {
                JSONObject obj = new JSONObject(data);
                int getClient = obj.getInt("idclient");
                int getLatitude = obj.getInt("latitude");
                int getLongitude = obj.getInt("longitude");
                int status = obj.getInt("status");
                String message = obj.getString("message");
                if (status == 0 || status == 2) {
                    new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                }
                idclient = getClient;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (option.equals("saveOrder")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                String message = obj.getString("message");
                new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                if (status == 1) {
                    Constantes.listaproductOrderList = new ArrayList<>();
                    myOrder();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada ha fallado.", this, getLayoutInflater()).toastPersonalization();
    }

    public void availableProduct(int quantityAvailable) {
        if (selection != null && positionProduct >= 0 && quantityProduct != null) {
            if ((Integer.valueOf(quantityProduct.getText().toString()) + 1) <= quantityAvailable) {
                quantityProduct.setText(String.valueOf(Integer.valueOf(quantityProduct.getText().toString()) + 1));
                selection.setQuantity(Integer.valueOf(quantityProduct.getText().toString()));
                Constantes.listaproductOrderList.set(positionProduct, selection);
            }
        } else {
            quantityProduct.setText("1");
        }
    }

    private void getStartedLocalization() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion = new Localizacion(this);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartedLocalization();
            }
        }
    }
}
