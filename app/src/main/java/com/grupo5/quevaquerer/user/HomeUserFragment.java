package com.grupo5.quevaquerer.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.category.CategoryItemClickListener;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.category.CategoryAdapter;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.product.ProductAdapter;
import com.grupo5.quevaquerer.product.ProductInformationActivity;
import com.grupo5.quevaquerer.product.ProductItemClickListener;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlCategory;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.url.UrlProduct;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeUserFragment extends Fragment implements CategoryItemClickListener, CallbackResponse, TextWatcher, ProductItemClickListener {
    View v;
    private EditText search;
    private String nameCategory = "";
    private TextView textView;
    private ConstraintLayout container1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_categories;
    private RecyclerView rv_products;
    private List<Category> listcategories;
    private List<Product> listproducts;
    private CategoryAdapter adaptercategories;
    private ProductAdapter adapterproducts;
    private Sgl sgl;
    private RequestParams params;
    private UrlProduct urlProduct;
    private UrlCategory urlCategory;
    private Client client;

    public HomeUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_user, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipe_user);
        //search = v.findViewById(R.id.producto);
        search = HomeUserActivity.searchhome;
        rv_categories = v.findViewById(R.id.rvcategories);
        rv_products = v.findViewById(R.id.rvproducts);
        container1 = v.findViewById(R.id.contenedor1);
        textView = v.findViewById(R.id.letras);
        search.addTextChangedListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                cleanFields();
                executeRequestCategories();
                executeRequestProducts("", "");
            }
        });
        executeRequestCategories();
        executeRequestProducts("", "");
        dataClient();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listcategories = new ArrayList<>();
        listproducts = new ArrayList<>();
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        urlProduct = new UrlProduct();
        urlCategory = new UrlCategory();
    }

    public void dataClient() {
        client = SharedPreferencesManager.getSomeSetValue(Constantes.PREFERENCES);
        if (client != null) {
            String url = client.getImage();
            if (!url.equals("") && url != null && url != "null") {
                Glide.with(this).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(HomeUserActivity.imageuser);
            } else {
                HomeUserActivity.imageuser.setImageResource(R.drawable.sinfoto);
            }
        } else {
            SharedPreferencesManager.destroySharedPreferences();
            Intent i = new Intent(getContext(), MainActivity.class);
            startActivity(i);
        }
    }

    public void executeRequestProducts(String product, String category) {
        params = new RequestParams();
        params.put("product", product);
        params.put("category", category);
        sgl.get(urlProduct.getListAvailableProducts("availableProducts"), params, "listAvailableProducts");
    }

    public void executeRequestCategories() {
        sgl.get(urlCategory.getListCategories("listCategories"), null, "listCategories");
    }

    @Override
    public void onCategoryClick(Category category, View v) {
        if (!category.getName().equals("Todo")) {
            nameCategory = category.getName();
            executeRequestProducts(search.getText().toString(), category.getName());
        } else {
            nameCategory = "";
            search.setText("");
            executeRequestProducts("", "");
        }
    }

    @Override
    public void onCategoryAction(View v) {

    }

    @Override
    public void onClickOptionsProduct(Product product, View view) {
        if (product != null && product.getAvailable() == 1 && product.getQuantity() > 0 && product.getPrice() > 0) {
            Intent intent = new Intent(getActivity(), ProductInformationActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        } else {
            new ToastAlert(0, "El producto no esta disponible", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void onCancelDialog(View v) {

    }

    @Override
    public void onActionDialog(Product product, View view) {

    }

    @Override
    public void onClickCameraGalery(View view) {

    }

    @Override
    public void onChangeStatusProduct(Product product, View view, int status) {

    }

    @Override
    public void success(String data, String option) {
        if (option.equals("listCategories")) {
            ArrayList<Category> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                JSONArray list = new JSONArray(object.getString("listCategories"));
                for (int i = 0; i < list.length(); i++) {
                    temp.add(new Category(list.getJSONObject(i).getInt("idcategory"),
                            list.getJSONObject(i).getString("image"),
                            list.getJSONObject(i).getString("name"),
                            list.getJSONObject(i).getString("description")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fillRecyclerViewCategories(temp);
        } else if (option.equals("listAvailableProducts")) {
            ArrayList<Product> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                JSONArray list = new JSONArray(object.getString("listAvailableProducts"));
                for (int i = 0; i < list.length(); i++) {
                    temp.add(new Product(list.getJSONObject(i).getInt("idproduct"),
                            list.getJSONObject(i).getString("image"),
                            list.getJSONObject(i).getString("name"),
                            list.getJSONObject(i).getString("description"),
                            list.getJSONObject(i).getInt("quantity"),
                            list.getJSONObject(i).getDouble("price"),
                            list.getJSONObject(i).getInt("available"),
                            new Category(list.getJSONObject(i).getInt("idcategory"),
                                    list.getJSONObject(i).getString("namecategory"))));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listproducts = temp;
            seeContainer();
        } else {
            new ToastAlert(0, "No hay información que visualizar", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada ha fallado", getContext(), getLayoutInflater()).toastPersonalization();
    }

    public void seeContainer() {
        loadAvailableProducts();
        if (listproducts.size() > 0) {
            rv_products.setVisibility(View.VISIBLE);
            container1.setVisibility(View.GONE);
        } else {
            if (search.getText().toString().equals("") && nameCategory.equals("")) {
                textView.setText("No hay productos disponibles");
            }
            if (!search.getText().toString().equals("") && !nameCategory.equals("")) {
                textView.setText("No se encontraron resultados, para la búsqueda " + search.getText().toString() + ", ni para la categoría " + nameCategory);
            } else if (!nameCategory.equals("")) {
                textView.setText("Lo sentimos, en este momento no tenemos " + nameCategory+" disponible");
            } else if (!search.getText().toString().equals("")) {
                textView.setText("No se encontraron resultados, para la búsqueda");
            }
            rv_products.setVisibility(View.GONE);
            container1.setVisibility(View.VISIBLE);
        }
    }

    public void fillRecyclerViewCategories(ArrayList<Category> list) {
        listcategories = new ArrayList<>();
        listcategories.add(0, new Category(0, "../resources/category/Todo.png", "Todo", ""));
        listcategories.addAll(list);
        adaptercategories = new CategoryAdapter(getContext(), listcategories, this);
        rv_categories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_categories.setAdapter(adaptercategories);
    }

    public void loadAvailableProducts() {
        adapterproducts = new ProductAdapter(getContext(), listproducts, this);
        rv_products.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_products.setAdapter(adapterproducts);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            executeRequestProducts(search.getText().toString(), "");
        } else {
            executeRequestProducts("", "");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void cleanFields() {
        search.setText("");
    }
}
