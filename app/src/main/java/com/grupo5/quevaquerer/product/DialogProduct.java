package com.grupo5.quevaquerer.product;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.url.UrlCategory;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DialogProduct extends AppCompatDialogFragment {
    private ProductItemClickListener productItemClickListener;
    private Product product;
    private String action;
    private TextView title_product;
    private EditText name_product;
    private EditText description_product;
    private EditText quantity_product;
    private EditText price_product;
    private Spinner id_category;
    private ImageView image_product;
    private Button btnSaveEdit, btnCancel;
    DecimalFormat df = new DecimalFormat("0.00");

    //para poblar el spinner
    final List<Category> listSpinner;
    UrlCategory urlCategory;
    AsyncHttpClient httpClient;
    RequestParams params;

    public DialogProduct(ProductItemClickListener productItemClickListener, Product product, String action) {
        this.productItemClickListener = productItemClickListener;
        this.product = product;
        this.action = action;
        this.listSpinner = new ArrayList<>();
        this.urlCategory = new UrlCategory();
        this.params = new RequestParams();
        this.httpClient = new AsyncHttpClient();
        this.listSpinner.add(0, new Category("Seleccione una categoría"));
        listCategoriesSpinner();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TextView getTitle_product() {
        return title_product;
    }

    public void setTitle_product(TextView title_product) {
        this.title_product = title_product;
    }

    public EditText getName_product() {
        return name_product;
    }

    public void setName_product(EditText name_product) {
        this.name_product = name_product;
    }

    public EditText getDescription_product() {
        return description_product;
    }

    public void setDescription_product(EditText description_product) {
        this.description_product = description_product;
    }

    public EditText getQuantity_product() {
        return quantity_product;
    }

    public void setQuantity_product(EditText quantity_product) {
        this.quantity_product = quantity_product;
    }

    public EditText getPrice_product() {
        return price_product;
    }

    public void setPrice_product(EditText price_product) {
        this.price_product = price_product;
    }

    public Spinner getId_category() {
        return id_category;
    }

    public void setId_category(Spinner id_category) {
        this.id_category = id_category;
    }

    public ImageView getImage_product() {
        return image_product;
    }

    public void setImage_product(ImageView image_product) {
        this.image_product = image_product;
    }

    public Button getBtnSaveEdit() {
        return btnSaveEdit;
    }

    public void setBtnSaveEdit(Button btnSaveEdit) {
        this.btnSaveEdit = btnSaveEdit;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(Button btnCancel) {
        this.btnCancel = btnCancel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTransparentDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_product, null);
        builder.setView(v);
        //obteniendo los elementos del layout
        title_product = v.findViewById(R.id.tituloproducto);
        name_product = v.findViewById(R.id.nombreproducto);
        description_product = v.findViewById(R.id.descripcionproducto);
        quantity_product = v.findViewById(R.id.cantidadproducto);
        price_product = v.findViewById(R.id.precioproducto);
        id_category = v.findViewById(R.id.idcategoriaproducto);
        image_product = v.findViewById(R.id.imagenproducto);
        btnSaveEdit = v.findViewById(R.id.btngeproducto);
        btnCancel = v.findViewById(R.id.btncancelarproducto);
        //asignando eventos y datos
        title_product.setText(action);
        if (product != null) {
            name_product.setText(product.getName());
            description_product.setText(product.getDescription());
            quantity_product.setText(String.valueOf(product.getQuantity()));
            price_product.setText(String.valueOf(df.format(product.getPrice())));
            String path = product.getImage();
            if (!path.equals("") && path != null && path != "null") {
                Glide.with(getContext()).load(UrlProduct.urlPhoto + path.substring(2, path.length())).into(image_product);
            } else {
                image_product.setImageResource(R.drawable.sinfoto);
            }
            btnSaveEdit.setText("Editar");
        }
        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClickListener.onActionDialog(product, v);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClickListener.onCancelDialog(v);
            }
        });

        image_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productItemClickListener.onClickCameraGalery(v);
            }
        });
        return builder.create();
    }

    public void listCategoriesSpinner() {
        httpClient.get(urlCategory.getListCategories("listCategories"), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    ArrayList<Category> temp = new ArrayList<>();
                    try {
                        JSONObject obj=new JSONObject(new String(responseBody));
                        JSONArray listJSON = new JSONArray(obj.getString("listCategories"));
                        for (int i = 0; i < listJSON.length(); i++) {
                            temp.add(new Category(listJSON.getJSONObject(i).getInt("idcategory"),
                                    listJSON.getJSONObject(i).getString("image"),
                                    listJSON.getJSONObject(i).getString("name"),
                                    listJSON.getJSONObject(i).getString("description")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chargeSpinner(temp);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void chargeSpinner(List<Category> list) {
        listSpinner.addAll(list);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(getContext(), android.R.layout.simple_spinner_dropdown_item, listSpinner);
        getId_category().setAdapter(adapter);
        if (product != null) {
            for (int i = 0; i < listSpinner.size(); i++) {
                if (listSpinner.get(i).getId() == product.getCategory().getId()) {
                    getId_category().setSelection(i);
                    break;
                }
            }
        }
    }
}
