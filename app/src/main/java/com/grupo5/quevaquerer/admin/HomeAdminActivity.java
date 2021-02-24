package com.grupo5.quevaquerer.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.category.ListCategoriesFragment;
import com.grupo5.quevaquerer.client.UserContainerFragment;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.order.ListOrderDayFragment;
import com.grupo5.quevaquerer.order.AllOrdersFragment;
import com.grupo5.quevaquerer.product.ListProductsFragment;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.user.LoginActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HomeAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ClickMessages {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView navigationtitle, rol;
    public static TextView username;
    public static ImageView userimage, imageAdmin;
    public static String back = "";
    public static Client client;
    ConfirmationDialog bottom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        navigationtitle = findViewById(R.id.titulonavegacionadmin);
        //accediendo a los elementos del encabezado del menú
        View headerAdmin = navigationView.getHeaderView(0);
        username = headerAdmin.findViewById(R.id.username);
        userimage = headerAdmin.findViewById(R.id.imguser);
        rol = headerAdmin.findViewById(R.id.rol);
        imageAdmin = findViewById(R.id.imagenadmin);

        setSupportActionBar(toolbar);
        setTitle("");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.itemhomeadmin);
        if (!back.equals("")) {
            showFragment(back);
        } else {
            showFragment("Home");
            back = "";
        }
        HomeAdminActivity.client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        viewDataUser();
    }

    public void operationsUser(View v) {
        int id = v.getId();
        if (id == R.id.imagenadmin) {
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
                        showFragment("Account");
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

    private void viewDataUser() {
        if (HomeAdminActivity.client != null) {
            username.setText(HomeAdminActivity.client.getName() + " " + HomeAdminActivity.client.getLastname());
            String url = HomeAdminActivity.client.getImage();
            if (!url.equals("") && url != null && url != "null") {
                Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(imageAdmin);
                Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(userimage);
            } else {
                imageAdmin.setImageResource(R.drawable.sinfoto);
                userimage.setImageResource(R.drawable.sinfoto);
            }
            if (client.getRol() == 0) {
                rol.setText("Administrador");
            } else {
                rol.setText("Cliente");
            }
            //new ToastAlert(1, "Bienvenido " + HomeAdminActivity.client.getName(), this, getLayoutInflater()).toastPersonalization();
        }
    }

    public void showFragment(String option) {
        if (option.equals("Home")) {
            navigationtitle.setText("Inicio");
            showSelectedFragment(new HomeAdminFragment());
        } else if (option.equals("Categories")) {
            navigationtitle.setText("Categorías");
            showSelectedFragment(new ListCategoriesFragment());
        } else if (option.equals("Products")) {
            navigationtitle.setText("Productos");
            showSelectedFragment(new ListProductsFragment());
        } else if (option.equals("Users")) {
            navigationtitle.setText("Usuarios");
            showSelectedFragment(new UserContainerFragment());
        } else if (option.equals("OrderDay")) {
            navigationtitle.setText("Pedidos del día");
            showSelectedFragment(new ListOrderDayFragment());
        } else if (option.equals("AllOrders")) {
            navigationtitle.setText("Todos los pedidos");
            showSelectedFragment(new AllOrdersFragment());
        } else if (option.equals("Account")) {
            navigationtitle.setText("Mi cuenta");
            showSelectedFragment(new AdminAccountFragment());
        }
    }

    @SuppressLint("WrongConstant")
    public void showSelectedFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.containeradmin, f)
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemhomeadmin:
                showFragment("Home");
                break;
            case R.id.itemcategory:
                showFragment("Categories");
                break;
            case R.id.itemproduct:
                showFragment("Products");
                break;
            case R.id.itemclient:
                showFragment("Users");
                break;
            case R.id.itemorder:
                showFragment("OrderDay");
                break;
            case R.id.itemlistorders:
                showFragment("AllOrders");
                break;
            case R.id.itemaccount:
                showFragment("Account");
                break;
            case R.id.itemexit:
                messageDialog();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
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
