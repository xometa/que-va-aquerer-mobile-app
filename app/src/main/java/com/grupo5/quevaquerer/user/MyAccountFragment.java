package com.grupo5.quevaquerer.user;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo5.quevaquerer.Localizacion;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, CallbackResponse, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    View v;
    private ImageView image, image1, btnImage;
    private EditText name, lastname, phone, user;
    private TextInputLayout password;
    private ConstraintLayout containerData, containerMap;
    private ScrollView containerInformation;
    Client client, aux;
    private GoogleMap mMap;
    private Button btnEdit, btnModified, btnCancel, btnChange, btnCancelMap, btnUbication;
    private TextView name1, phone1, direction1, user1;

    //enviado de datos
    UrlClient urlClient;
    RequestParams params;
    Sgl sgl;

    //varaiables para la foto
    Uri imageUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path = "";

    //ubicacion del cliente
    double latitude = 0;
    double longitude = 0;

    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_account, container, false);
        image = v.findViewById(R.id.imagen);
        name = v.findViewById(R.id.nombre);
        btnImage = v.findViewById(R.id.cambiarimagen);
        lastname = v.findViewById(R.id.apellido);
        phone = v.findViewById(R.id.telefono);
        user = v.findViewById(R.id.usuario);
        password = v.findViewById(R.id.contrasena);
        containerData = v.findViewById(R.id.contenedor);
        containerMap = v.findViewById(R.id.mapadatos);
        containerInformation = v.findViewById(R.id.contenedorinformacion);
        btnEdit = v.findViewById(R.id.btnEditarPerfil);
        btnModified = v.findViewById(R.id.btnModificar);
        btnCancel = v.findViewById(R.id.btnCancelarregistro);
        btnChange = v.findViewById(R.id.btnCambiarubicacion);
        btnCancelMap = v.findViewById(R.id.btnCancelarMapa);
        btnUbication = v.findViewById(R.id.btnubicacion);

        image1 = v.findViewById(R.id.imagenver);
        name1 = v.findViewById(R.id.nombrever);
        phone1 = v.findViewById(R.id.telefonover);
        direction1 = v.findViewById(R.id.direccionver);
        user1 = v.findViewById(R.id.usuariover);

        btnEdit.setOnClickListener(this);
        btnModified.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnCancelMap.setOnClickListener(this);
        btnUbication.setOnClickListener(this);
        //recuperando las preferencias
        dataClient();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlClient = new UrlClient();
        params = new RequestParams();
        sgl = new Sgl(getContext(), this);
    }

    public void dataClient() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {
            latitude = client.getLatitude();
            longitude = client.getLongitude();
            controllerConstraintLayout("1");
        } else {
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private void clientInformation() {
        String url = client.getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(image);
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(image1);
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeUserActivity.imageuser);
        } else {
            image.setImageResource(R.drawable.sinfoto);
            image1.setImageResource(R.drawable.sinfoto);
            HomeUserActivity.imageuser.setImageResource(R.drawable.sinfoto);
        }
        name.setText(client.getName());
        lastname.setText(client.getLastname());
        phone.setText(client.getPhone());
        user.setText(client.getUsername());
        password.getEditText().setText("");


        name1.setText(client.getName() + " " + client.getLastname());
        if (client.getLatitude() == 0 && client.getLongitude() == 0) {
            direction1.setText("Agregue una dirección");
        } else {
            direction1.setText(Props.getLocationName(getContext(), client.getLatitude(), client.getLongitude()));
        }
        phone1.setText(client.getPhone());
        user1.setText(client.getUsername());
    }

    private void getStartedLocalization() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Localizacion localizacion = new Localizacion(getContext());
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ubication;
        if (client.getLongitude() == 0 && client.getLatitude() == 0) {
            getStartedLocalization();
            ubication = new LatLng(Constantes.latitude, Constantes.longitude);
        } else {

            ubication = new LatLng(client.getLatitude(), client.getLongitude());
        }
        mMap = googleMap;
        ubication = new LatLng(client.getLatitude(), client.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(ubication)
                .title("Mi ubicación")
                .draggable(true));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 10.1f));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    public void controllerConstraintLayout(String option) {
        if (option.equals("1")) {
            containerData.setVisibility(View.VISIBLE);
            containerInformation.setVisibility(View.GONE);
            containerMap.setVisibility(View.GONE);
            clientInformation();
        } else if (option.equals("2")) {
            containerInformation.setVisibility(View.VISIBLE);
            containerData.setVisibility(View.GONE);
            containerMap.setVisibility(View.GONE);
            clientInformation();
        } else if (option.equals("3")) {
            containerInformation.setVisibility(View.GONE);
            containerData.setVisibility(View.GONE);
            containerMap.setVisibility(View.VISIBLE);
            clientInformation();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnEditarPerfil) {
            controllerConstraintLayout("2");
        } else if (id == R.id.btnModificar) {
            editInformation();
        } else if (id == R.id.btnCancelarregistro) {
            controllerConstraintLayout("1");
        } else if (id == R.id.cambiarimagen) {
            cameraGalery();
        } else if (id == R.id.btnCambiarubicacion) {
            editInformation();
        } else if (id == R.id.btnCancelarMapa) {
            controllerConstraintLayout("1");
        } else if (id == R.id.btnubicacion) {
            controllerConstraintLayout("3");
        }
    }

    private void cameraGalery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        final CharSequence[] options = {"Tomar foto", "Elegir de la galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Tomar foto")) {
                    openCamera();
                } else if (options[which].equals("Elegir de la galería")) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/");
                    startActivityForResult(i.createChooser(i, "Seleccione"), SELEC_IMAGEN);

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void openCamera() {
        String nameImage = "";
        File fileImage = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreate = fileImage.exists();

        if (isCreate == false) {
            isCreate = fileImage.mkdirs();
        }

        if (isCreate == true) {
            nameImage = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nameImage;
        File image = new File(path);
        Intent i = null;
        i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getActivity().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, image);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        }
        startActivityForResult(i, TOMAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELEC_IMAGEN) {
            if (data != null) {
                imageUri = data.getData();
                image1.setImageURI(imageUri);
                image.setImageURI(imageUri);
                path = Props.getPath(imageUri, getActivity());
            } else {
                path = "";
            }
        } else if (requestCode == TOMAR_FOTO) {
            MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
            if (!path.equals("")) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image1.setImageBitmap(bitmap);
                image.setImageBitmap(bitmap);
            } else {
                path = "";
            }
        }
    }

    public void editInformation() {
        params = new RequestParams();
        if (name.getText().toString().isEmpty()) {
            new ToastAlert(0, "Ingrese sus nombres.", getContext(), getLayoutInflater()).toastPersonalization();
        } else {
            if (lastname.getText().toString().isEmpty()) {
                new ToastAlert(0, "Ingrese sus apellidos.", getContext(), getLayoutInflater()).toastPersonalization();
            } else {
                if (phone.getText().toString().isEmpty()) {
                    new ToastAlert(0, "Ingrese su número de teléfono.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    if (user.getText().toString().isEmpty()) {
                        new ToastAlert(0, "Ingrese un usuario para su cuenta.", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        if (client != null) {
                            params.put("option", "saveClient");
                            params.put("idclient", client.getId());
                            params.put("name", name.getText().toString());
                            params.put("lastname", lastname.getText().toString());
                            if (!path.equals("")) {
                                try {
                                    params.put("image", new File(path));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            params.put("phone", phone.getText().toString());
                            params.put("latitude", latitude);
                            params.put("longitude", longitude);
                            params.put("username", user.getText().toString());
                            params.put("rol", 1);
                            if (!password.getEditText().getText().toString().isEmpty()) {
                                params.put("userpassword", password.getEditText().getText().toString());
                            }
                            sgl.post(urlClient.postClient(), params, "editClient");
                        } else {
                            new ToastAlert(0, "La información de inicio de sesión ha sido alterada.", getContext(), getLayoutInflater()).toastPersonalization();
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("editClient")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                String message = obj.getString("message");
                if (status == 1) {
                    new ToastAlert(status, "Su información ha sido actualizada correctamente.", getContext(), getLayoutInflater()).toastPersonalization();
                    sgl.get(urlClient.getClient(client.getId()), null, "newPreferences");
                } else {
                    new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (option.equals("newPreferences")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                if (status == 1) {
                    SharedPreferencesManager.destroySharedPreferences();
                    aux = new Client(obj.getInt("idclient"), obj.getString("name"), obj.getString("lastname"),
                            obj.getString("image"), obj.getString("phone"), obj.getDouble("latitude"),
                            obj.getDouble("longitude"), obj.getString("username"),
                            obj.getString("userpassword"), obj.getInt("rol"));
                    SharedPreferencesManager.setSomeSetValue(Constantes.PREFERENCES, aux);
                    dataClient();
                } else {
                    new ToastAlert(status, "No se ha podido actualizar la nueva información", getContext(), getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada ha fallado..", getContext(), getLayoutInflater()).toastPersonalization();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
