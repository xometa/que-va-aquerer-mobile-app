package com.grupo5.quevaquerer.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.client.ClientItemClickListener;
import com.grupo5.quevaquerer.client.ClientListAdapter;
import com.grupo5.quevaquerer.client.DialogUser;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.messages.ClickMessages;
import com.grupo5.quevaquerer.messages.ConfirmationDialog;
import com.grupo5.quevaquerer.product.DialogProduct;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment implements CallbackResponse, ClientItemClickListener, View.OnClickListener, ClickMessages {
    View v;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ClientListAdapter clientAdapter;

    //para los datos
    Sgl sgl;
    RequestParams params;
    UrlClient urlClient;
    Client client;
    ArrayList<Client> listclient;

    ConfirmationDialog bottom = null;
    DialogUser du = null;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_user_list, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        recyclerView = v.findViewById(R.id.recyclerusuarios);
        floatingActionButton = v.findViewById(R.id.btnnuevousuario);
        floatingActionButton.setOnClickListener(this);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                userList();
            }
        });
        userList();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        urlClient = new UrlClient();
        listclient = new ArrayList<>();
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

    public void userList() {
        sgl.get(urlClient.getListClients("listClients"), null, "userAdmin");
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("userAdmin")) {
            ArrayList<Client> temp = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray listJSON = new JSONArray(obj.getString("listClients"));
                for (int c = 0; c < listJSON.length(); c++) {
                    if (listJSON.getJSONObject(c).getInt("rol") == 0) {
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
                int status = obj.getInt("status");
                if (status == 1) {
                    new ToastAlert(status, "El usuario ha sido eliminado.", getContext(), getLayoutInflater()).toastPersonalization();
                } else if (status == 2) {
                    new ToastAlert(status, "La petición solicitada ha fallado.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    new ToastAlert(status, "Erro al eliminar el usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            userList();
        }

        if (option.equals("saveClient")) {
            try {
                JSONObject obj = new JSONObject(data);
                int status = obj.getInt("status");
                String message = obj.getString("message");
                String[] cad = message.split(" ");
                if (status == 1) {
                    if (cad.length == 6) {
                        new ToastAlert(status, "El usuario ha sido registrado.", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        new ToastAlert(status, "Los datos del usuarios han sido actualizados.", getContext(), getLayoutInflater()).toastPersonalization();
                    }
                } else if (status == 2) {
                    new ToastAlert(status, "La petición solicitada ha fallado.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    if (cad[2].equals("registrar")) {
                        new ToastAlert(status, "Error al registrar el usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        new ToastAlert(status, "Error al modificar el usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resetElements();
            userList();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();

    }

    public void chargeRecyclerVew() {
        clientAdapter = new ClientListAdapter(getContext(), listclient, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(clientAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClientClick(Client client, View view) {
        final Client clientop = client;
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
                        optionsClient(clientop, "modified");
                        return true;
                    case R.id.delete_operation:
                        optionsClient(clientop, "delete");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void optionsClient(Client client, String option) {
        if (option.equals("see")) {
        } else if (option.equals("modified")) {
            if (client != null) {
                du = new DialogUser(client, this, "Editar usuario");
                du.show(getFragmentManager(), "");
            } else {
                new ToastAlert(0, "El usuario no se puede modificar.", getContext(), getLayoutInflater()).toastPersonalization();
            }
        } else if (option.equals("delete")) {
            bottom = new ConfirmationDialog(this, getContext(), R.drawable.warning, "Eliminar usuario", "¿Estás seguro de eliminar al usuario " + client.getName() + "?", "Aceptar", client);
            bottom.show(getFragmentManager(), "");
        }
    }

    @Override
    public void onActionDialogClient(View view) {
        params = new RequestParams();
        if (du != null) {
            if (du.getName().getText().toString().isEmpty()) {
                new ToastAlert(0, "Ingrese los nombres del usuario.", getContext(), getLayoutInflater()).toastPersonalization();
            } else {
                if (du.getLastname().getText().toString().isEmpty()) {
                    new ToastAlert(0, "Ingrese los apellidos del usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                } else {
                    if (du.getPhone().getText().toString().isEmpty()) {
                        new ToastAlert(0, "Ingrese el número de teléfono del usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                    } else {
                        if (du.getUser().getText().toString().isEmpty()) {
                            new ToastAlert(0, "Ingrese el usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                        } else {
                            if (du.getPassword().getEditText().getText().toString().isEmpty()) {
                                new ToastAlert(0, "Ingrese la contraseña del usuario.", getContext(), getLayoutInflater()).toastPersonalization();
                            } else {
                                params.put("option", "saveClient");
                                if (du.getClient() != null) {
                                    params.put("idclient", du.getClient().getId());
                                }
                                params.put("name", du.getName().getText().toString());
                                params.put("lastname", du.getLastname().getText().toString());
                                params.put("phone", du.getPhone().getText().toString());
                                params.put("phone", du.getPhone().getText().toString());
                                params.put("username", du.getUser().getText().toString());
                                params.put("userpassword", du.getPassword().getEditText().getText().toString());
                                params.put("latitude", 0);
                                params.put("longitude", 0);
                                params.put("rol", 0);
                                sgl.post(urlClient.postClient(), params, "saveClient");
                            }
                        }
                    }
                }
            }
        }
    }

    private void resetElements() {
        if (du != null) {
            du.getName().requestFocus();
            du.setClient(null);
            du.getTitle().setText("Nuevo usuario");
            du.getBtnAction().setText("Guardar");
            du.getName().setText("");
            du.getLastname().setText("");
            du.getPhone().setText("");
            du.getUser().setText("");
            du.getPassword().getEditText().setText("");
        }
    }

    @Override
    public void onCancelDialogClient(View view) {
        if (du != null) {
            du.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnnuevousuario) {
            du = new DialogUser(null, this, "Nuevo usuario");
            du.show(getFragmentManager(), "");
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
        if (bottom != null) {
            Client client = (Client) object;
            if (client.getId() > 0) {
                sgl.delete(urlClient.deleteClient(client.getId()), null, "deleteClient");
                bottom.dismiss();
            } else {
                new ToastAlert(0, "No se ha seleccionado ningún usuario, vuelva a intentarlo.", getContext(), getLayoutInflater()).toastPersonalization();
            }
        } else {
            new ToastAlert(0, "Error al eliminar el usuario.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }
}
