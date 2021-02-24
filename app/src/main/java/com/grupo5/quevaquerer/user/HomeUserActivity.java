package com.grupo5.quevaquerer.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.order.MyOrderFragment;
import com.grupo5.quevaquerer.order.NewOrderActivity;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HomeUserActivity extends AppCompatActivity implements ClickMessages {
    BottomNavigationView bnv;
    //arreglo que contendra la lista de los productos, que el
    //cliente seleccione, para el pedido
    //public static ArrayList<Product> productOrderList;
    public static Client client;
    public FloatingActionButton floatingActionButton;
    Toolbar toolbar;
    public static EditText searchhome;
    public static ImageView imageuser;
    ImageView iconImage;
    private TextView titleuser;
    ConfirmationDialog bottom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.icons));
        setContentView(R.layout.activity_home_user);
        //productOrderList = new ArrayList<>();
        //Constantes.listaproductOrderList = new ArrayList<>();
        bnv = findViewById(R.id.buttonnavigation);
        floatingActionButton = findViewById(R.id.btnneworder);
        searchhome = findViewById(R.id.buscarinicio);
        imageuser = findViewById(R.id.imageuser);
        iconImage = findViewById(R.id.iconoimagen);
        titleuser = findViewById(R.id.titulouser);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        validate();
        //evento para mostrar un fragment segun lo que se seleccione de la navegacion
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.homeuser) {
                    MostrarSeleccionFragment("Home");
                } else if (id == R.id.ordersuser) {
                    MostrarSeleccionFragment("MyOrder");
                } else if (id == R.id.accountuser) {
                    MostrarSeleccionFragment("MyAccount");
                }
                return true;
            }
        });
    }

    public void validate() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {
            //mostramos por default un fragment
            MostrarSeleccionFragment("Home");
        } else {
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    @SuppressLint("WrongConstant")
    public void showSelectedFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, f)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    public void MostrarSeleccionFragment(String opcion) {
        if (opcion.equals("Home")) {
            elements("1");
            titleuser.setText("Inicio");
            showSelectedFragment(new HomeUserFragment());
        } else if (opcion.equals("MyOrder")) {
            elements("2");
            titleuser.setText("Mi pedido");
            showSelectedFragment(new MyOrderFragment());
        } else if (opcion.equals("MyAccount")) {
            elements("3");
            titleuser.setText("Mi cuenta");
            showSelectedFragment(new MyAccountFragment());
        }
    }

    public void onActions(View v) {
        int id = v.getId();
        if (id == R.id.btnneworder) {
            Intent i = new Intent(this, NewOrderActivity.class);
            startActivity(i);
        } else if (id == R.id.imageuser) {
            exitApp(v);
        }
    }

    private void exitApp(View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupMenu.inflate(R.menu.options_user);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.account_user:
                        MostrarSeleccionFragment("MyAccount");
                        return true;
                    case R.id.exit_app:
                        messageDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.show();
    }

    public void messageDialog() {
        bottom = new ConfirmationDialog(this, this, R.drawable.warning, "Salir de la aplicación", "¿Estás seguro de salir de la aplicación?", "Salir", null);
        bottom.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void elements(String option) {
        switch (option) {
            case "1":
                HomeUserActivity.searchhome.setVisibility(View.VISIBLE);
                titleuser.setVisibility(View.GONE);
                iconImage.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
            case "2":
            case "3":
                HomeUserActivity.searchhome.setVisibility(View.GONE);
                titleuser.setVisibility(View.VISIBLE);
                iconImage.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                break;
            default:
                HomeUserActivity.searchhome.setVisibility(View.GONE);
                titleuser.setVisibility(View.GONE);
                iconImage.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                break;
        }
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
            SharedPreferencesManager.destroySharedPreferences();
            finishAffinity();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}
