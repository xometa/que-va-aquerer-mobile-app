package com.grupo5.quevaquerer.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

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
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListOrderDayFragment extends Fragment implements View.OnClickListener, OrderItemClickListener, CallbackResponse {
    View v;
    SwipeRefreshLayout swipeRefreshLayout;
    AdapterOrderDay adapterOrderDay;
    RecyclerView rv_ordersday;
    ArrayList<OrderHeader> list;
    ArrayList<DetailsOrder> list2;
    RadioButton rb1, rb2, rb3;
    //obtención de datos
    Sgl sgl;
    RequestParams params;
    UrlOrder urlOrder;
    Client client;

    //contenedores
    ConstraintLayout container1, container2, container3;

    public ListOrderDayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list_order_day, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipe_ordersday);
        rv_ordersday = v.findViewById(R.id.rv_listapedidos);
        rb1 = v.findViewById(R.id.rbtodo);
        rb2 = v.findViewById(R.id.rbentregados);
        rb3 = v.findViewById(R.id.rbpendientes);
        container1 = v.findViewById(R.id.contenedor1);
        container2 = v.findViewById(R.id.contenedor2);
        container3 = v.findViewById(R.id.contenedor3);
        rb1.setChecked(true);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                rb1.setChecked(true);
                listOrdersDay(0, "");
            }
        });
        listOrdersDay(0, "");
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        sgl = new Sgl(getContext(), this);
        params = new RequestParams();
        urlOrder = new UrlOrder();
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

    public void listOrdersDay(int status, String view) {
        params = new RequestParams();
        params.put("view", view);
        params.put("status", status);
        sgl.get(urlOrder.getListOrderDay("listOrdersDay"), params, "listOrdersDay");
    }

    public void fillRecyclerView() {
        adapterOrderDay = new AdapterOrderDay(getContext(), list, this);
        rv_ordersday.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_ordersday.setAdapter(adapterOrderDay);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbtodo:
            case R.id.rbentregados:
            case R.id.rbpendientes:
                if (rb1.isChecked()) {
                    listOrdersDay(0, "");
                } else if (rb2.isChecked()) {
                    listOrdersDay(1, "status");
                } else if (rb3.isChecked()) {
                    listOrdersDay(0, "status");
                }
                break;
        }
    }

    @Override
    public void onViesDetailsProduct(View view, OrderHeader orderHeader) {

    }

    @Override
    public void quantityOrder(View view, Product product, String option, TextView quantity, int position) {

    }

    @Override
    public void setStatusOrder(View view, OrderHeader orderHeader, int status) {
        params = new RequestParams();
        params.put("option", "modifiedStatusOrder");
        params.put("idorderheader", orderHeader.getIdorderheader());
        params.put("statusorder", status);
        sgl.post(urlOrder.postStatusOrder(), params, "statusOrder");
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("statusOrder")) {
            try {
                JSONObject obj = new JSONObject(data);
                int statusOrder = obj.getInt("status");
                String message = obj.getString("message");
                new ToastAlert(statusOrder, message, getContext(), getLayoutInflater()).toastPersonalization();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (option.equals("listOrdersDay")) {
            ArrayList<OrderHeader> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                JSONArray aux, listp;
                aux = new JSONArray(object.getString("listOrdersDay"));
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

    public void seeContainer() {
        fillRecyclerView();
        if (list.size() > 0) {
            container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.GONE);
            container3.setVisibility(View.GONE);
        } else {
            if (rb2.isChecked()) {
                container1.setVisibility(View.GONE);
                container2.setVisibility(View.GONE);
                container3.setVisibility(View.VISIBLE);
            } else {
                container2.setVisibility(View.VISIBLE);
                container1.setVisibility(View.GONE);
                container3.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();
    }
}
