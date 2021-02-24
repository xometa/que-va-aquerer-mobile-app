package com.grupo5.quevaquerer.admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
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
public class AdminAccountFragment extends Fragment implements View.OnClickListener, CallbackResponse {
    View v;
    private ImageView imageuser, imagedata, clickimage;
    private TextView name, rol, phone, username, namedata, lastnamedata, phonedata, userdata;
    private TextInputLayout passworddata;
    private Button btnEdit, btnModified, btnCancel;
    private EditText nameadmin, lastnameadmin;
    private Client client, aux;
    ConstraintLayout containerAdmin;
    ScrollView dataAdmin;

    //para los datos
    Sgl sgl;
    RequestParams params;
    UrlClient urlClient;

    //varaiables para la foto
    Uri imageUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path = "";

    public AdminAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_admin_account, container, false);
        //contenedores
        containerAdmin = v.findViewById(R.id.contenedoradmin);
        dataAdmin = v.findViewById(R.id.contenedordatos);
        //elementos del contenedor admin
        imageuser = v.findViewById(R.id.imagenadmin);
        name = v.findViewById(R.id.nombreadmin);
        phone = v.findViewById(R.id.telefonoadmin);
        rol = v.findViewById(R.id.rol);
        username = v.findViewById(R.id.usuarioadmin);
        btnEdit = v.findViewById(R.id.btneditaradmin);
        btnModified = v.findViewById(R.id.btnmodifcardatos);
        btnCancel = v.findViewById(R.id.btncancelarmodificar);
        //elementos del contenedor datos admin
        imagedata = v.findViewById(R.id.imagendatos);
        clickimage = v.findViewById(R.id.clickimagendatos);
        namedata = v.findViewById(R.id.nombredatos);
        lastnamedata = v.findViewById(R.id.apellidosdatos);
        phonedata = v.findViewById(R.id.telefonodatos);
        userdata = v.findViewById(R.id.usuariodatos);
        passworddata = v.findViewById(R.id.contrasenadatos);
        //click para los botones
        btnEdit.setOnClickListener(this);
        btnModified.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        clickimage.setOnClickListener(this);
        //comprobamos la disponibilidad del usuario
        validateData();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgl = new Sgl(getContext(), this);
        urlClient = new UrlClient();
        params = new RequestParams();
    }

    public void validateData() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {

            controllerConstraintLayout("1");
        } else {
            getActivity().finishAffinity();
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);
        }

    }

    private void controllerConstraintLayout(String option) {
        clientInformation();
        if (option.equals("1")) {
            containerAdmin.setVisibility(View.VISIBLE);
            dataAdmin.setVisibility(View.GONE);
        } else if (option.equals("2")) {
            containerAdmin.setVisibility(View.GONE);
            dataAdmin.setVisibility(View.VISIBLE);
        }
    }

    private void clientInformation() {
        String url = client.getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(imageuser);
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(imagedata);
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeAdminActivity.userimage);
            Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeAdminActivity.imageAdmin);
        } else {
            imageuser.setImageResource(R.drawable.sinfoto);
            imagedata.setImageResource(R.drawable.sinfoto);
            HomeAdminActivity.userimage.setImageResource(R.drawable.sinfoto);
            HomeAdminActivity.imageAdmin.setImageResource(R.drawable.sinfoto);
        }
        HomeAdminActivity.username.setText(client.getName() + " " + client.getLastname());
        name.setText(client.getName() + " " + client.getLastname());
        phone.setText(client.getPhone());
        if (client.getRol() == 0) {
            rol.setText("Administrador");
        } else {
            rol.setText("Cliente");
        }
        username.setText(client.getUsername());

        namedata.setText(client.getName());
        lastnamedata.setText(client.getLastname());
        phonedata.setText(client.getPhone());
        userdata.setText(client.getUsername());
        passworddata.getEditText().setText("");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btneditaradmin) {
            controllerConstraintLayout("2");
        } else if (id == R.id.btnmodifcardatos) {
            modifiedClient();
        } else if (id == R.id.btncancelarmodificar) {
            controllerConstraintLayout("1");
        } else if (id == R.id.clickimagendatos) {
            openCameraGalery();
        }
    }

    private void openCameraGalery() {
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
                imageuser.setImageURI(imageUri);
                imagedata.setImageURI(imageUri);
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
                imageuser.setImageBitmap(bitmap);
                imagedata.setImageBitmap(bitmap);
            } else {
                path = "";
            }
        }
    }

    private void modifiedClient() {
        params = new RequestParams();
        if (namedata.getText().toString().isEmpty()) {
            new ToastAlert(0, "Ingrese sus nombres.", getContext(), getLayoutInflater()).toastPersonalization();
        } else {
            if (lastnamedata.getText().toString().isEmpty()) {
                new ToastAlert(0, "Ingrese sus apellidos.", getContext(), getLayoutInflater()).toastPersonalization();
            } else {
                if (phonedata.getText().toString().isEmpty()) {
                    new ToastAlert(0, "Ingrese su número de teléfono.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    if (userdata.getText().toString().isEmpty()) {
                        new ToastAlert(0, "Ingrese un usuario para su cuenta.", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        if (client != null) {
                            params.put("option", "saveClient");
                            params.put("idclient", client.getId());
                            params.put("name", namedata.getText().toString());
                            params.put("lastname", lastnamedata.getText().toString());
                            if (!path.equals("")) {
                                try {
                                    params.put("image", new File(path));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            params.put("phone", phonedata.getText().toString());
                            params.put("latitude", 0);
                            params.put("longitude", 0);
                            params.put("username", userdata.getText().toString());
                            params.put("rol", 0);
                            if (!passworddata.getEditText().getText().toString().isEmpty()) {
                                params.put("userpassword", passworddata.getEditText().getText().toString());
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
                    validateData();
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
        new ToastAlert(0, "La petición solicitada ha fallado.", getContext(), getLayoutInflater()).toastPersonalization();
    }
}
