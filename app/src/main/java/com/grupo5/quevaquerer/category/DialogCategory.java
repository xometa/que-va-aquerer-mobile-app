package com.grupo5.quevaquerer.category;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlCategory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class DialogCategory extends AppCompatDialogFragment {
    private Category category;
    private String action;
    private TextView titledialog;
    private EditText namecategory;
    private EditText descriptioncategory;
    private ImageView imagecategory;
    private Button btnSaveEdit, btnCancel;
    private CategoryItemClickListener itemClickListener;

    Uri imageUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path = "";
    //para el envio de los datos
    UrlCategory urlCategory;
    AsyncHttpClient httpClient;
    RequestParams params;

    public DialogCategory(Category category, String action, CategoryItemClickListener categoryItemClickListener) {
        this.category = category;
        this.action = action;
        this.itemClickListener = categoryItemClickListener;
        urlCategory = new UrlCategory();
        httpClient = new AsyncHttpClient();
        params = new RequestParams();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTransparentDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_category, null);
        builder.setView(v);
        titledialog = v.findViewById(R.id.titulo1);
        namecategory = v.findViewById(R.id.nombrecategoria);
        descriptioncategory = v.findViewById(R.id.descripcioncategoria);
        imagecategory = v.findViewById(R.id.imagencategoria);
        btnSaveEdit = v.findViewById(R.id.btnConfirmacion);

        //asignando opciones correspondientes a los elementos
        titledialog.setText(action);
        //si el objeto categoria no es null; quiere decir que se ha seleccionado una categoría y es de editarla,
        //y se procede a cargar la informacion correspodiente a los elementos del dialog
        if (category != null) {
            namecategory.setText(category.getName());
            if (!category.getDescription().equals("No hay descripción")) {
                descriptioncategory.setText(category.getDescription());
            }
            String path = category.getImage();
            if (!path.equals("") && path != null && path != "null") {
                Glide.with(getContext()).load(UrlCategory.urlPhoto + path.substring(2, path.length())).into(imagecategory);
            } else {
                imagecategory.setImageResource(R.drawable.sinfoto);
            }
            btnSaveEdit.setText("Editar");
        }
        //eventos para los botones
        btnCancel = v.findViewById(R.id.btnCDExit);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        imagecategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });
        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params=new RequestParams();
                final View view = v;
                if (namecategory.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "El nombre de la categoría es obligatorio", Toast.LENGTH_SHORT).show();
                } else {
                    if (category != null) {
                        params.put("idcategory", category.getId());
                    }
                    if (!path.equals("")) {
                        try {
                            params.put("image", new File(path));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    params.put("name", namecategory.getText().toString());
                    params.put("description", descriptioncategory.getText().toString());
                    params.put("option", "saveCategory");
                    httpClient.post(urlCategory.postCategory(), params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                try {
                                    JSONObject obj = new JSONObject(new String(responseBody));
                                    String message = obj.getString("message");
                                    int status = obj.getInt("status");
                                    new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                clean();
                                itemClickListener.onCategoryAction(view);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
                itemClickListener.onCancelDialog(v);
            }
        });

        return builder.create();
    }

    //opciones para la imagen, si se desea seleccionar de la galería o tomar desde la camara
    public void showOptionsDialog() {
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

    //metodo para abrir la camara
    private void openCamera() {
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

    //captura de la camara o de la galeria y mostrarla en el circleimageview
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELEC_IMAGEN) {
            if (data != null) {
                imageUri = data.getData();
                imagecategory.setImageURI(imageUri);
                path = getPath(imageUri);
            } else {
                path = "";
            }
        } else if (requestCode == TOMAR_FOTO) {
            MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
            Log.d("", "path camera: " + path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imagecategory.setImageBitmap(bitmap);
        }
    }

    //devolvera el path de la imagen seleccionada de la galería
    private String getPath(Uri imageUri) {
        Cursor c = getActivity().getContentResolver().query(imageUri, null, null, null, null);
        if (c == null) {
            return imageUri.getPath();
        } else {
            c.moveToFirst();
            int index = c.getColumnIndex(MediaStore.Images.Media.DATA);
            return c.getString(index);
        }
    }

    //limpiamos los atributos correspondientes
    public void clean() {
        imagecategory.setImageResource(R.drawable.sinfoto);
        namecategory.setText("");
        descriptioncategory.setText("");
        path = "";
        category = null;
        titledialog.setText("Nueva Categoría");
        btnSaveEdit.setText("Guardar");
    }
}
