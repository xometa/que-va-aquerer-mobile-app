package com.grupo5.quevaquerer.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
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
public class ListClientsFragment extends Fragment implements ClientItemClickListener, CallbackResponse, ClickMessages {
    View v;
    private RecyclerView rv_listclients;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Client> listclient;
    ClientListAdapter clientAdapter;
    //para el sacado de datos
    RequestParams params;
    Sgl sgl;
    UrlClient urlClient;
    Client client;
    ConfirmationDialog bottom = null;

    public ListClientsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list_clients, container, false);
        rv_listclients = v.findViewById(R.id.rvlistaclientes);
        swipeRefreshLayout = v.findViewById(R.id.swipeclient);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                listClients();
            }
        });
        listClients();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listclient = new ArrayList<>();
        params = new RequestParams();
        sgl = new Sgl(getContext(), this);
        urlClient = new UrlClient();
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

    public void listClients() {
        sgl.get(urlClient.getListClients("listClients"), null, "clients");
    }

    public void chargeRecyclerVew() {
        clientAdapter = new ClientListAdapter(getContext(), listclient, this);
        rv_listclients.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_listclients.setAdapter(clientAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClientClick(Client client, View view) {
        final Client clientOp = client;
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
        popupMenu.inflate(R.menu.menu_client);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.see_client:
                        optionsClient(clientOp, "see");
                        return true;
                    case R.id.delete_client:
                        optionsClient(clientOp, "delete");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.show();
    }

    //metodo para eliminar y editar clientes
    private void optionsClient(Client client, String option) {
        if (option.equals("see")) {
            Intent i = new Intent(getContext(), PersonInformationActivity.class);
            i.putExtra("client", client);
            startActivity(i);
        } else if (option.equals("delete")) {
            bottom = new ConfirmationDialog(this, getContext(), R.drawable.warning, "Eliminar cliente", "¿Estás seguro de eliminar el cliente " + client.getName() + "?", "Aceptar", client);
            bottom.show(getFragmentManager(), "");
        }
    }


    @Override
    public void onActionDialogClient(View view) {

    }

    @Override
    public void onCancelDialogClient(View view) {

    }

    @Override
    public void success(String data, String option) {
        if (option.equals("clients")) {
            ArrayList<Client> temp = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray listJSON = new JSONArray(obj.getString("listClients"));
                for (int c = 0; c < listJSON.length(); c++) {
                    if (listJSON.getJSONObject(c).getInt("rol") == 1) {
                        temp.add(new Client(listJSON.getJSONObject(c).getInt("idclient"),
                                listJSON.getJSONObject(c).getString("name"),
                                listJSON.getJSONObject(c).getString("lastname"),
                                listJSON.getJSONObject(c).getString("image"),
                                listJSON.getJSONObject(c).getString("phone"),
                                listJSON.getJSONObject(c).getDouble("latitude"),
                                listJSON.getJSONObject(c).getDouble("longitude"),
                                listJSON.getJSONObject(c).getString("username"),
                                listJSON.getJSONObject(c).getString("userpassword"),
                                listJSON.getJSONObject(c).getInt("rol")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listclient = temp;
            chargeRecyclerVew();
        }
        if (option.equals("deleteClient")) {
            try {
                JSONObject obj = new JSONObject(data);
                String message = obj.getString("message");
                int status = obj.getInt("status");
                new ToastAlert(status, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listClients();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();
    }

    @Override
    public void closeBottomSheetDialog(View v) {
        if (bottom != null) {
            bottom.dismiss();
        }
    }

    @Override
    public void actionBottomSheetDialog(View v, Object object) {
        if (bottom != null) {
            Client client = (Client) object;
            if (client.getId() > 0) {
                sgl.delete(urlClient.deleteClient(client.getId()), null, "deleteClient");
                bottom.dismiss();
            } else {
                new ToastAlert(0, "No se ha seleccionado ningún cliente, vuelva a intentarlo.", getContext(), getLayoutInflater()).toastPersonalization();
            }
        } else {
            new ToastAlert(0, "Error al eliminar el cliente.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }
}
