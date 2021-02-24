package com.grupo5.quevaquerer.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.client.CreateAccountActivity;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements CallbackResponse {
    EditText user, pass;
    public static String navigation = "MainActivity";
    Sgl sgl;
    Client client = null;
    RequestParams params;
    UrlClient urlClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_home));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_login);
        sgl = new Sgl(this, this);
        params = new RequestParams();
        urlClient = new UrlClient();
        user = findViewById(R.id.user);
        pass = findViewById(R.id.password);
    }

    public void onSession(View v) {
        params = new RequestParams();
        if (this.user.getText().toString().isEmpty() && this.pass.getText().toString().isEmpty()) {
            new ToastAlert(2, "Por favor complete los campos solicitados.", this, getLayoutInflater()).toastPersonalization();
        } else {
            if (this.user.getText().toString().isEmpty()) {
                new ToastAlert(2, "Ingrese el nombre del usuario.", this, getLayoutInflater()).toastPersonalization();
            } else {
                if (this.pass.getText().toString().isEmpty()) {
                    new ToastAlert(2, "Ingrese la contraseña.", this, getLayoutInflater()).toastPersonalization();
                } else {
                    sgl.get(urlClient.getAvailableUser(user.getText().toString(), pass.getText().toString()), null, "availableUser");
                }
            }

        }
    }

    public void onCreateAccount(View v) {
        finish();
        CreateAccountActivity.navigation = "LoginActivity";
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivity(i);
    }

    public void navegacionVista() {
        Intent i;
        if (navigation.equals("MainActivity")) {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (navigation.equals("CreateAccountActivity")) {
            i = new Intent(this, CreateAccountActivity.class);
            startActivity(i);
        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                navegacionVista();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        navegacionVista();
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("availableUser")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                String message = obj.getString("message");
                if (status == 1) {
                    client = new Client(obj.getInt("idclient"), obj.getString("name"), obj.getString("lastname"),
                            obj.getString("image"), obj.getString("phone"), obj.getDouble("latitude"),
                            obj.getDouble("longitude"), obj.getString("username"),
                            obj.getString("userpassword"), obj.getInt("rol"));
                    SharedPreferencesManager.setSomeSetValue(Constantes.PREFERENCES, client);
                    openSession();
                } else {
                    new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(2, "La petición solicitada ha fallado.", this, getLayoutInflater()).toastPersonalization();
    }

    public void openSession() {
        if (client != null) {
            Intent i;
            if (client.getRol() == 0) {
                i = new Intent(this, HomeAdminActivity.class);
            } else {
                i = new Intent(this, HomeUserActivity.class);
            }
            startActivity(i);
        }
    }
}
