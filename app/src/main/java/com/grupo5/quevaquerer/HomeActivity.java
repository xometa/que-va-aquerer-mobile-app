package com.grupo5.quevaquerer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.client.CreateAccountActivity;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.product.ProductAdapter;
import com.grupo5.quevaquerer.product.ProductItemClickListener;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.grupo5.quevaquerer.user.HomeUserActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements CallbackResponse, ProductItemClickListener, ClickMessages {
    private Client client = null;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductAdapter productAdapter;
    private Sgl sgl;
    private RequestParams params;
    private List<Product> listproducts;
    private UrlProduct urlProduct;
    private ConfirmationDialog bottom = null;
    TextView v;
    //contenedores
    ConstraintLayout container1, container2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.icons));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.productosdia);
        swipeRefreshLayout = findViewById(R.id.swipe);
        container1 = findViewById(R.id.contenedor1);
        container2 = findViewById(R.id.contenedor2);
        v = findViewById(R.id.textView43);
        sgl = new Sgl(this, this);
        params = new RequestParams();
        listproducts = new ArrayList<>();
        urlProduct = new UrlProduct();
        client = SharedPreferencesManager.getSomeSetValue("client");
        if (client != null) {
            Intent i;
            if (client.getRol() == 0) {
                i = new Intent(this, HomeAdminActivity.class);
            } else {
                i = new Intent(this, HomeUserActivity.class);
            }
            startActivity(i);
        } else {
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    dayProductsList("", "");
                }
            });
            dayProductsList("", "");
        }
    }

    public void dayProductsList(String product, String category) {
        params = new RequestParams();
        params.put("product", product);
        params.put("category", category);
        sgl.get(urlProduct.getListAvailableProducts("availableProducts"), params, "dayProductsList");
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void getStarted(View v) {
        //getStartedLocalization();
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("dayProductsList")) {
            ArrayList<Product> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                JSONArray list = new JSONArray(object.getString("listAvailableProducts"));
                for (int i = 0; i < list.length(); i++) {
                    temp.add(new Product(list.getJSONObject(i).getInt("idproduct"),
                            list.getJSONObject(i).getString("image"),
                            list.getJSONObject(i).getString("name"),
                            list.getJSONObject(i).getString("description"),
                            list.getJSONObject(i).getInt("quantity"),
                            list.getJSONObject(i).getDouble("price"),
                            list.getJSONObject(i).getInt("available"),
                            new Category(list.getJSONObject(i).getInt("idcategory"),
                                    list.getJSONObject(i).getString("namecategory"))));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listproducts = temp;
            seeContainer();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", this, getLayoutInflater()).toastPersonalization();
    }

    public void seeContainer() {
        fillRecyclerView();
        if (listproducts.size() > 0) {
            container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.GONE);
        } else {
            container2.setVisibility(View.VISIBLE);
            container1.setVisibility(View.GONE);
        }
    }

    public void fillRecyclerView() {
        productAdapter = new ProductAdapter(this, listproducts, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(productAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClickOptionsProduct(Product product, View view) {
        //new ToastAlert(2, "Para solicitar es producto, debes iniciar sesión.", this, getLayoutInflater()).toastPersonalization();
        bottom = new ConfirmationDialog(this, this, R.drawable.warning, "Al parecer no estas registrado", "Para solicitar este producto, debes registrarte", "Registrarte", null);
        bottom.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onCancelDialog(View view) {

    }

    @Override
    public void onActionDialog(Product product, View view) {

    }

    @Override
    public void onClickCameraGalery(View view) {

    }

    @Override
    public void onChangeStatusProduct(Product product, View view, int status) {

    }

    @Override
    public void closeBottomSheetDialog(View v) {
        if (bottom != null) {
            bottom.dismiss();
        }
    }

    @Override
    public void actionBottomSheetDialog(View v, Object object) {
        if (bottom != null) {
            bottom.dismiss();
            Intent i = new Intent(this, CreateAccountActivity.class);
            startActivity(i);
        }
    }
}
