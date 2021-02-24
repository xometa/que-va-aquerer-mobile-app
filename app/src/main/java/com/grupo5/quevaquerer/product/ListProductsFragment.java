package com.grupo5.quevaquerer.product;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListProductsFragment extends Fragment implements View.OnClickListener, ProductItemClickListener, TextWatcher, CallbackResponse, ClickMessages {
    View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_listaproducts;
    private FloatingActionButton floatingActionButton;
    private EditText searchProduct;
    ProductListAdapter adapter_listproducts;
    List<Product> list;

    //para los datos
    Sgl sgl;
    RequestParams params;
    UrlProduct urlProduct;
    DialogProduct dp = null;
    ConfirmationDialog bottom = null;
    Client client;

    //varaiables para la foto
    Uri imageUri;
    int TOMAR_FOTO = 100;
    int SELEC_IMAGEN = 200;
    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    String path = "";

    public ListProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list_products, container, false);
        rv_listaproducts = v.findViewById(R.id.rvlistproducts);
        swipeRefreshLayout = v.findViewById(R.id.swipeproduct);
        floatingActionButton = v.findViewById(R.id.btnnewproducto);
        searchProduct = v.findViewById(R.id.etbuscarproducto);
        floatingActionButton.setOnClickListener(this);
        searchProduct.addTextChangedListener(this);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                listProducts("");
            }
        });
        listProducts("");
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        list = new ArrayList<>();
        urlProduct = new UrlProduct();
    }

    public void dataClient() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {
            String url = client.getImage();
            if (!url.equals("") && url != null && url != "null") {
                Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeAdminActivity.imageAdmin);
                Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeAdminActivity.userimage);
            } else {
                HomeAdminActivity.imageAdmin.setImageResource(R.drawable.sinfoto);
                HomeAdminActivity.userimage.setImageResource(R.drawable.sinfoto);
            }
            HomeAdminActivity.username.setText(client.getName() + " " + client.getLastname());
        } else {
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private void listProducts(String search) {
        params = new RequestParams();
        params.put("search", search);
        sgl.get(urlProduct.getListProducts("listProducts"), params, "listProducts");
    }

    public void chargeRecyclerView() {
        adapter_listproducts = new ProductListAdapter(getContext(), list, this);
        rv_listaproducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_listaproducts.setAdapter(adapter_listproducts);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnnewproducto:
                dp = new DialogProduct(this, null, "Nuevo producto");
                dp.show(getFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onClickOptionsProduct(Product product, View view) {
        final Product productOp = product;
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
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
        popupMenu.inflate(R.menu.operations);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify_operation:
                        editProduct(productOp);
                        return true;
                    case R.id.delete_operation:
                        deleteProduct(productOp);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void deleteProduct(Product product) {
        bottom = new ConfirmationDialog(this, getContext(), R.drawable.warning, "Eliminar producto", "¿Estás seguro de eliminar el producto " + product.getName() + "?", "Aceptar", product);
        bottom.show(getFragmentManager(), "");
    }

    public void editProduct(Product product) {
        if (product != null) {
            dp = new DialogProduct(this, product, "Editar producto");
            dp.show(getFragmentManager(), "");
        } else {
            new ToastAlert(0, "El producto no se puede modificar.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void onCancelDialog(View view) {
        if (dp != null) {
            dp.dismiss();
        }
    }

    @Override
    public void onActionDialog(Product product, View view) {
        params = new RequestParams();
        if (dp != null) {
            if (dp.getName_product().getText().toString().isEmpty()) {
                new ToastAlert(0, "Ingrese el nombre del producto.", getContext(), getLayoutInflater()).toastPersonalization();
            } else {
                if (dp.getDescription_product().getText().toString().isEmpty()) {
                    new ToastAlert(0, "Ingrese una descripción para el producto.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    if (dp.getQuantity_product().getText().toString().isEmpty()) {
                        new ToastAlert(0, "Ingrese una cantidad mayor o igual a cero", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        if (Integer.parseInt(dp.getQuantity_product().getText().toString()) < 0) {
                            new ToastAlert(0, "La cantidad ingresada es incorrecta.", getContext(), getLayoutInflater()).toastPersonalization();
                        } else {
                            if (dp.getPrice_product().getText().toString().isEmpty()) {
                                new ToastAlert(0, "Ingrese un precio mayor o igual a cero.", getContext(), getLayoutInflater()).toastPersonalization();
                            } else {
                                if (Double.parseDouble(dp.getQuantity_product().getText().toString()) < 0) {
                                    new ToastAlert(0, "El precio ingresado es incorrecto.", getContext(), getLayoutInflater()).toastPersonalization();
                                } else {
                                    if (dp.getId_category().getSelectedItemPosition() == 0) {
                                        new ToastAlert(0, "Seleccione una categoría para el producto.", getContext(), getLayoutInflater()).toastPersonalization();
                                    } else {
                                        //obteniendo la categoria seleccionada
                                        Category itemCategory = (Category) dp.getId_category().getItemAtPosition(dp.getId_category().getSelectedItemPosition());
                                        /*
                                         * si el objeto product no es null, quiere decir que el producto se va a editar
                                         */
                                        String idproduct = "";
                                        if (product != null) {
                                            idproduct = String.valueOf(product.getId());
                                        } else {
                                            idproduct = "";
                                        }
                                        /*
                                         * si el path sigue siendo vacío, quiere decir que no se ha seleccionado o tomado una foto
                                         */
                                        if (!path.equals("")) {
                                            try {
                                                params.put("image", new File(path));
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        params.put("idproduct", idproduct);
                                        params.put("name", dp.getName_product().getText().toString());
                                        params.put("quantity", dp.getQuantity_product().getText().toString());
                                        params.put("price", dp.getPrice_product().getText().toString());
                                        params.put("description", dp.getDescription_product().getText().toString());
                                        params.put("available", 0);
                                        params.put("idcategory", itemCategory.getId());
                                        params.put("option", "saveProduct");
                                        sgl.post(urlProduct.postProduct(), params, "saveProduct");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClickCameraGalery(View view) {
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
            if (data != null && dp != null) {
                imageUri = data.getData();
                dp.getImage_product().setImageURI(imageUri);
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
            if (dp != null && !path.equals("")) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                dp.getImage_product().setImageBitmap(bitmap);
            } else {
                path = "";
            }
        }
    }

    @Override
    public void onChangeStatusProduct(Product product, View view, int status) {
        params = new RequestParams();
        params.put("option", "editStatus");
        params.put("idproduct", product.getId());
        params.put("name", product.getName());
        params.put("available", status);
        sgl.post(urlProduct.postStatusProduct(), params, "statusProduct");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() != 0) {
            listProducts(searchProduct.getText().toString());
        } else {
            listProducts("");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void success(String data, String option) {

        if (option.equals("listProducts")) {
            ArrayList<Product> temp = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray listJSON = new JSONArray(obj.getString("listProducts"));
                for (int p = 0; p < listJSON.length(); p++) {
                    temp.add(new Product(listJSON.getJSONObject(p).getInt("idproduct"),
                            listJSON.getJSONObject(p).getString("image"),
                            listJSON.getJSONObject(p).getString("name"),
                            listJSON.getJSONObject(p).getString("description"),
                            listJSON.getJSONObject(p).getInt("quantity"),
                            listJSON.getJSONObject(p).getDouble("price"),
                            listJSON.getJSONObject(p).getInt("available"),
                            new Category(listJSON.getJSONObject(p).getInt("idcategory"),
                                    listJSON.getJSONObject(p).getString("namecategory"))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            list = temp;
            chargeRecyclerView();
        }
        if (option.equals("statusProduct")) {
            try {
                JSONObject obj = new JSONObject(data);
                int statusProduct = obj.getInt("status");
                String message = obj.getString("message");
                new ToastAlert(statusProduct, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (option.equals("saveProduct")) {
            try {
                JSONObject obj = new JSONObject(data);
                String message = obj.getString("message");
                int status = obj.getInt("status");
                new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resetElements();
            listProducts("");
        }

        if (option.equals("deleteProduct")) {
            try {
                JSONObject obj = new JSONObject(data);
                String message = obj.getString("message");
                int status = obj.getInt("status");
                new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listProducts("");
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();
    }


    public void resetElements() {
        if (dp != null) {
            path = "";
            dp.setProduct(null);
            dp.getBtnSaveEdit().setText("Guardar");
            dp.getTitle_product().setText("Nuevo producto");
            dp.getName_product().setText("");
            dp.getDescription_product().setText("");
            dp.getQuantity_product().setText("");
            dp.getPrice_product().setText("");
            dp.getImage_product().setImageResource(R.drawable.sinfoto);
            dp.getId_category().setSelection(0);
            searchProduct.setText("");
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
        Product product = (Product) object;
        if (product.getId() > 0 && product != null && bottom != null) {
            sgl.delete(urlProduct.deleteProduct(product.getId()), null, "deleteProduct");
            bottom.dismiss();
        } else {
            new ToastAlert(0, "Error al eliminar el producto " + product.getDescription() + ".", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }
}
