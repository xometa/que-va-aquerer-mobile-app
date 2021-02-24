package com.grupo5.quevaquerer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.client.CreateAccountActivity;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.slider.Slider;
import com.grupo5.quevaquerer.slider.SliderPageAdapter;
import com.grupo5.quevaquerer.user.HomeUserActivity;
import com.grupo5.quevaquerer.user.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ViewPager vp;
    SliderPageAdapter adapter;
    List<Slider> lista;
    TabLayout indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_home));
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vp = findViewById(R.id.viewpager);
        indicator = findViewById(R.id.indicator);
        lista = new ArrayList<>();
        lista.add(new Slider(R.drawable.onboarding1, "Encuentra tus comidas", "Descubre las mejores comidas de nuestro restaurante"));
        lista.add(new Slider(R.drawable.onboarding2, "Entrega rápida", "Entrega rápida a su hogar, oficina y donde quiera que esté"));
        lista.add(new Slider(R.drawable.onboarding3, "Desde cualquier lugar", "Solicita tu pedido, desde cualquier lugar"));
        adapter = new SliderPageAdapter(this, lista);
        vp.setAdapter(adapter);
        //for slider dinamic
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 2000, 4000);
        indicator.setupWithViewPager(vp, true);

    }

    class SliderTimer extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vp.getCurrentItem() < lista.size() - 1) {
                        vp.setCurrentItem(vp.getCurrentItem() + 1);
                    } else {
                        vp.setCurrentItem(0);
                    }
                }
            });
        }
    }

    //menu action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumainactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ingresar:
                finish();
                LoginActivity.navigation = "MainActivity";
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateAccountClient(View v) {
        finish();
        CreateAccountActivity.navigation = "MainActivity";
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

}
