package com.grupo5.quevaquerer.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.grupo5.quevaquerer.user.HomeUserActivity;

import java.text.DecimalFormat;

public class ProductInformationActivity extends AppCompatActivity {
    ImageView image;
    TextView description, name, price;
    Button btnAddToBask;
    DecimalFormat df = new DecimalFormat("0.00");
    Product product = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_product_information);
        setTitle("Nuevo pedido");
        //setStatusBarTranslucent(true);
        image = findViewById(R.id.imagenagregar);
        name = findViewById(R.id.nombreproducto);
        description = findViewById(R.id.descripcionproducto);
        price = findViewById(R.id.precioventa);
        btnAddToBask = findViewById(R.id.btnagregarcarrito);
        product = (Product) getIntent().getExtras().getSerializable("product");
        if (product != null) {
            String url = product.getImage();
            if (!url.equals("") && url != null && url != "null") {
                Glide.with(this).load(UrlProduct.urlPhoto + url.substring(2, url.length())).into(image);
            } else {
                image.setImageResource(R.drawable.sinfoto);
            }
            name.setText(product.getName());
            description.setText(product.getDescription());
            price.setText("$ " + String.valueOf(df.format(product.getPrice())));
            check(product, "create");
            Log.d("", "list: " + Constantes.listaproductOrderList.size());
        }
    }

    protected void setStatusBarTranslucent(boolean b) {
        if (b) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void AddToBasket(View v) {
        check(product, "btn");
    }

    public void check(Product product, String action) {
        if (product != null) {
            if (!Props.productOrderList(Constantes.listaproductOrderList, product)) {
                if (action.equals("btn")) {
                    Product addProduct = new Product(product.getId(), product.getImage(), product.getName(), 1, product.getPrice());
                    Constantes.listaproductOrderList.add(addProduct);
                    btnAddToBask.setText("Agregado a la canasta");
                } else {
                    btnAddToBask.setText("Agregar a la canasta");
                }
            } else {
                if (action.equals("btn")) {
                    btnAddToBask.setText("Agregado a la canasta");
                    new ToastAlert(2, "El producto ya ha sido agregado a la canasta", this, getLayoutInflater()).toastPersonalization();
                } else {
                    btnAddToBask.setText("Agregado a la canasta");
                }
            }
        } else {
            new ToastAlert(2, "El producto no se ha agregado a la canasta, vuelva a intentarlo", this, getLayoutInflater()).toastPersonalization();
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

    @Override
    public void onBackPressed() {
        finish();
        /*Intent i = new Intent(this, HomeUserActivity.class);
        startActivity(i);*/
    }
}
