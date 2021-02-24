package com.grupo5.quevaquerer.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.user.HomeUserActivity;
import com.grupo5.quevaquerer.user.LoginActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class CreateAccountActivity extends AppCompatActivity implements CallbackResponse {
    public static String navigation = "MainActivity";

    private EditText name, lastname, phone, user;
    private TextInputLayout password;
    Sgl sgl;
    RequestParams params;
    UrlClient urlClient;
    Client client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_account);
        name = findViewById(R.id.nombreuser);
        lastname = findViewById(R.id.apellidouser);
        phone = findViewById(R.id.telefono);
        user = findViewById(R.id.usuario);
        password = findViewById(R.id.contrasena);
        sgl = new Sgl(this, this);
        params = new RequestParams();
        urlClient = new UrlClient();
    }

    //para el boton de regreso
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                viewNaviagtion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //dar click en el textview
    public void onAction(View v) {
        int id = v.getId();
        if (id == R.id.txtCreateAccount) {
            finish();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.buttonLogin) {
            saveUser();
        }
    }

    private void saveUser() {
        params = new RequestParams();
        if (name.getText().toString().isEmpty()) {
            new ToastAlert(0, "Ingrese sus nombres.", this, getLayoutInflater()).toastPersonalization();
        } else {
            if (lastname.getText().toString().isEmpty()) {
                new ToastAlert(0, "Ingrese sus apellidos.", this, getLayoutInflater()).toastPersonalization();
            } else {
                if (phone.getText().toString().isEmpty()) {
                    new ToastAlert(0, "Ingrese su número de teléfono.", this, getLayoutInflater()).toastPersonalization();
                } else {
                    if (user.getText().toString().isEmpty()) {
                        new ToastAlert(0, "Ingrese un usuario para su cuenta.", this, getLayoutInflater()).toastPersonalization();
                    } else {
                        if (password.getEditText().getText().toString().isEmpty()) {
                            new ToastAlert(0, "Ingrese una contraseña para su cuenta.", this, getLayoutInflater()).toastPersonalization();
                        } else {
                            params.put("option", "saveClient");
                            params.put("name", name.getText().toString());
                            params.put("lastname", lastname.getText().toString());
                            params.put("phone", phone.getText().toString());
                            params.put("latitude", 0);
                            params.put("longitude", 0);
                            params.put("username", user.getText().toString());
                            params.put("rol", 1);
                            params.put("userpassword", password.getEditText().getText().toString());
                            sgl.post(urlClient.postClient(), params, "newClient");
                        }
                    }
                }
            }
        }
    }

    //metodo para el control de las nevagaciones
    public void viewNaviagtion() {
        Intent i;
        if (navigation.equals("MainActivity")) {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (navigation.equals("LoginActivity")) {
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        viewNaviagtion();
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("newClient")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                String message = obj.getString("message");
                if (status == 1) {
                    new ToastAlert(status, "Su cuenta ha sido creada correctamente.", this, getLayoutInflater()).toastPersonalization();
                    sgl.get(urlClient.getAvailableUser(user.getText().toString(), password.getEditText().getText().toString()), null, "availableUser");
                } else {
                    new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    SharedPreferencesManager.setSomeSetValue("client", client);
                    emptyElements();
                    openSession();
                } else {
                    new ToastAlert(status, message, this, getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void emptyElements() {
        name.setText("");
        lastname.setText("");
        phone.setText("");
        user.setText("");
        password.getEditText().setText("");
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada ha fallado.", this, getLayoutInflater()).toastPersonalization();
    }

    public void openSession() {
        client = SharedPreferencesManager.getSomeSetValue("client");
        Intent i;
        if (client != null) {
            if (client.getRol() == 0) {
                i = new Intent(this, HomeAdminActivity.class);
            } else {
                i = new Intent(this, HomeUserActivity.class);
            }
            startActivity(i);
        } else {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}
