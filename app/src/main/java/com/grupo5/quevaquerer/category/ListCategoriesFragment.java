package com.grupo5.quevaquerer.category;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlCategory;
import com.grupo5.quevaquerer.url.UrlClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListCategoriesFragment extends Fragment implements View.OnClickListener, CategoryItemClickListener, TextWatcher, ClickMessages, CallbackResponse {
    View v;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_listcategories;
    private List<Category> list;
    private EditText searchCategory;
    FloatingActionButton btnAddCategory;
    CategoryListAdapter clientListAdapter;
    //Clase de las url's
    Sgl sgl;
    UrlCategory urlCategory;
    RequestParams params;
    DialogCategory dialogCategory = null;
    ConfirmationDialog bottom = null;
    Client client;

    public ListCategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list_categories, container, false);
        rv_listcategories = v.findViewById(R.id.rvlistacategorias);
        swipeRefreshLayout = v.findViewById(R.id.swipecategory);
        btnAddCategory = v.findViewById(R.id.btnnewcategory);
        searchCategory = v.findViewById(R.id.etbuscarcategoria);
        searchCategory.addTextChangedListener(this);
        btnAddCategory.setOnClickListener(this);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                listCategories("");
            }
        });
        listCategories("");
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        list = new ArrayList<>();
        urlCategory = new UrlCategory();
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

    public void listCategories(String search) {
        params.put("search", search);
        sgl.get(urlCategory.getListCategories("listCategories"), params, "listCategories");
    }

    public void chargeRecyclerView() {
        clientListAdapter = new CategoryListAdapter(getContext(), list, this);
        rv_listcategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_listcategories.setAdapter(clientListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnnewcategory:
                dialogCategory = new DialogCategory(null, "Nueva Categoría", this);
                dialogCategory.show(getFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onCategoryClick(Category category, View view) {
        final Category categoryOp = category;
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        //usamos reflect para mostrar los iconos del menu, recalcando que en futura versiones
        // esto puede estar deprecado, ya que context menu no tiene soporte para iconos
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
                        editCategory(categoryOp);
                        return true;
                    case R.id.delete_operation:
                        deleteCategory(categoryOp);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.show();
    }

    @Override
    public void onCategoryAction(View v) {
        searchCategory.setText("");
        listCategories("");
    }

    @Override
    public void onCancelDialog(View v) {
        if (dialogCategory != null) {
            dialogCategory.dismiss();
        }
    }

    /*
    Es lo mismo de arriba pero si se desea usar, solo descomentarlo, y cortar la línea de abajo de donde esta la coma
    y pegarla a la par de los implements
    , PopupMenu.OnMenuItemClickListener
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modify_operation:
                Toast.makeText(getActivity(), "para editar ", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete_operation:
                Toast.makeText(getActivity(), "para eliminar ", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }*/

    public void deleteCategory(Category category) {
        bottom = new ConfirmationDialog(this, getContext(), R.drawable.warning, "Eliminar categoría", "¿Estás seguro de eliminar la categoría " + category.getName() + "?", "Aceptar", category);
        bottom.show(getFragmentManager(), "");
    }

    public void editCategory(Category category) {
        if (category.getId() > 0) {
            dialogCategory = new DialogCategory(category, "Editar Categoría", this);
            dialogCategory.show(getFragmentManager(), "");
        } else {
            new ToastAlert(1, "La categoría " + category.getName() + " no se puede editar.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    //metodos para búsqueda en tiempo real
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() != 0) {
            listCategories(searchCategory.getText().toString());
        } else {
            listCategories("");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void closeBottomSheetDialog(View v) {

    }

    @Override
    public void actionBottomSheetDialog(View v, Object object) {
        Category category = (Category) object;
        if (category.getId() > 0 && bottom != null) {
            sgl.delete(urlCategory.deleteCategory(category.getId()), null, "deleteCategory");
            bottom.dismiss();
        } else {
            new ToastAlert(2, "La categoría " + category.getName() + " no se puede eliminar.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("deleteCategory")) {
            try {
                JSONObject obj = new JSONObject(data);
                String message = obj.getString("message");
                int status = obj.getInt("status");
                if (status == 1) {
                    listCategories("");
                }
                new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (option.equals("listCategories")) {
            ArrayList<Category> temp = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(data);
                String message = obj.getString("message");
                int status = obj.getInt("status");
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
            list = temp;
            chargeRecyclerView();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada ha fallado.", getContext(), getLayoutInflater()).toastPersonalization();
    }
}
