package com.grupo5.quevaquerer.order;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.DetailsOrder;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.url.UrlOrder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllOrdersFragment extends Fragment implements OrderItemClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher, CallbackResponse {
    View v;
    private int idclient = 0;
    private String name = "";
    private ImageView btnDate1, btnDate2;
    private TextView clientname;
    private EditText inicio, fin;
    private Spinner spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_orders;
    private ArrayList<OrderHeader> list;
    private ArrayList<DetailsOrder> list2;
    private ArrayList<Client> listClient;
    private AdapterAllOrders adapterOrders;

    //para la obtención de datos
    private UrlClient urlClient;
    private UrlOrder urlOrder;
    private RequestParams params;
    private Sgl sgl;
    private Client client;

    //contenedores
    ConstraintLayout container1, container2, container3;

    public AllOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_all_orders, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipe_pedidos);
        rv_orders = v.findViewById(R.id.rv_pedidos);
        spinner = v.findViewById(R.id.spinner);
        inicio = v.findViewById(R.id.inicio);
        fin = v.findViewById(R.id.fin);
        btnDate1 = v.findViewById(R.id.imageView2);
        btnDate2 = v.findViewById(R.id.imageView3);
        container1 = v.findViewById(R.id.contenedor1);
        container2 = v.findViewById(R.id.contenedor2);
        container3 = v.findViewById(R.id.contenedor3);
        clientname = v.findViewById(R.id.textView50);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                listAllOrders(0, "", "");
                listClients();
                emptyElements();
            }
        });
        spinner.setOnItemSelectedListener(this);
        btnDate1.setOnClickListener(this);
        btnDate2.setOnClickListener(this);
        inicio.addTextChangedListener(this);
        fin.addTextChangedListener(this);
        fin.addTextChangedListener(this);
        listAllOrders(0, "", "");
        listClients();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        listClient = new ArrayList<>();
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        urlOrder = new UrlOrder();
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
        sgl.get(urlClient.getListClients("listClients"), null, "listClients");
    }

    public void listAllOrders(int idclient, String inicio, String fin) {
        params = new RequestParams();
        params.put("idclient", idclient);
        if (!inicio.equals("")) {
            inicio = Props.formatDate1(Props.parseDate(inicio));
            params.put("inicio", inicio);
        }
        if (!fin.equals("")) {
            fin = Props.formatDate1(Props.parseDate(fin));
            params.put("fin", fin);
        }
        sgl.get(urlOrder.getListAllOrdes("allOrders"), params, "allOrders");
    }

    @Override
    public void onViesDetailsProduct(View view, OrderHeader orderHeader) {
        if (orderHeader.getDetailsOrders().size() > 0) {
            Intent intent = new Intent(getActivity(), DetailsOrderActivity.class);
            intent.putExtra("orderHeader", orderHeader);
            startActivity(intent);
        } else {
            new ToastAlert(2, "El pedido al que hace referencia, no posee productos", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void quantityOrder(View view, Product product, String option, TextView quantity, int position) {

    }

    @Override
    public void setStatusOrder(View view, OrderHeader orderHeader, int status) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            Client cl = (Client) spinner.getItemAtPosition(position);
            idclient = cl.getId();
            name = cl.getName();
            listAllOrders(cl.getId(), inicio.getText().toString(), fin.getText().toString());
        } else {
            idclient = 0;
            name = "";
            listAllOrders(0, inicio.getText().toString(), fin.getText().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2:
                datePicker("inicio");
                break;
            case R.id.imageView3:
                datePicker("fin");
                break;
        }
    }

    public void datePicker(String option) {
        final String op = option;
        final Calendar c = Calendar.getInstance();
        int day, month, year;
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (op == "inicio") {
                    inicio.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                } else {
                    fin.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                }
            }
        }, year, month, day);
        dpd.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            if (inicio.getText().toString().length() > 0 || fin.getText().toString().length() > 0) {
                listAllOrders(idclient, inicio.getText().toString(), fin.getText().toString());
            } else {
                new ToastAlert(2, "Ingrese las fechas respectivas", getContext(), getLayoutInflater()).toastPersonalization();
            }
        } else {
            fin.setEnabled(false);
            listAllOrders(idclient, "", "");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void emptyElements() {
        inicio.setText("");
        fin.setText("");
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("listClients")) {
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
            fillSpinner(temp);
        }

        if (option.equals("allOrders")) {

            ArrayList<OrderHeader> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                JSONArray aux, listp;
                aux = new JSONArray(object.getString("allOrders"));
                for (int i = 0; i < aux.length(); i++) {
                    listp = new JSONArray(aux.getJSONObject(i).getString("detailsOrders"));
                    list2 = new ArrayList<>();
                    for (int j = 0; j < listp.length(); j++) {
                        list2.add(new DetailsOrder(listp.getJSONObject(j).getInt("iddetailsorder"),
                                listp.getJSONObject(j).getInt("idproduct"),
                                listp.getJSONObject(j).getDouble("price"),
                                listp.getJSONObject(j).getInt("quantity"),
                                new Product(listp.getJSONObject(j).getInt("idproduct"),
                                        listp.getJSONObject(j).getString("imageproduct"),
                                        listp.getJSONObject(j).getString("nameproduct"),
                                        listp.getJSONObject(j).getInt("quantity"),
                                        listp.getJSONObject(j).getDouble("price"))));
                    }
                    Date dateorder = Props.parseDate(aux.getJSONObject(i).getString("dateorder"));
                    temp.add(new OrderHeader(aux.getJSONObject(i).getInt("idorderheader"),
                            aux.getJSONObject(i).getInt("idclient"),
                            aux.getJSONObject(i).getDouble("latitude"),
                            aux.getJSONObject(i).getDouble("longitude"),
                            dateorder,
                            aux.getJSONObject(i).getDouble("totalorder"),
                            aux.getJSONObject(i).getInt("statusorder"),
                            new Client(aux.getJSONObject(i).getInt("idclient"),
                                    aux.getJSONObject(i).getString("name"),
                                    aux.getJSONObject(i).getString("lastname"),
                                    aux.getJSONObject(i).getString("image")), list2));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list = temp;
            seeContainer();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada, no pudo ser procesada", getContext(), getLayoutInflater()).toastPersonalization();
    }

    public void seeContainer() {
        fillRecyclerView();
        if (list.size() > 0) {
            container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.GONE);
            container3.setVisibility(View.GONE);
        } else {
            if (idclient > 0 && !name.equals("")) {
                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                container3.setVisibility(View.VISIBLE);
                clientname.setText(name + " no ha solicitado nada aún");
            } else {
                container2.setVisibility(View.VISIBLE);
                container1.setVisibility(View.GONE);
                container3.setVisibility(View.GONE);
            }
        }
    }

    public void fillSpinner(ArrayList<Client> temp) {
        listClient = new ArrayList<>();
        listClient.add(0, new Client("Seleccione un cliente", ""));
        listClient.addAll(temp);
        ArrayAdapter<Client> adapter = new ArrayAdapter<Client>(getContext(), android.R.layout.simple_spinner_dropdown_item, listClient);
        spinner.setAdapter(adapter);
    }

    public void fillRecyclerView() {
        adapterOrders = new AdapterAllOrders(getContext(), list, this);
        rv_orders.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_orders.setAdapter(adapterOrders);
        swipeRefreshLayout.setRefreshing(false);
    }
}
