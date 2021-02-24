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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.client.MyOrderDayAdapter;
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
import com.grupo5.quevaquerer.user.HomeUserActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrderFragment extends Fragment implements CallbackResponse, OrderItemClickListener {
    Client client;
    View v;
    ConstraintLayout c1, c2;
    RecyclerView orderList;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<OrderHeader> list;
    ArrayList<DetailsOrder> list2;
    MyOrderDayAdapter myOrderDayAdapter;

    //obtención de los datos
    Sgl sgl;
    UrlOrder urlOrder;

    public MyOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_order, container, false);
        c1 = v.findViewById(R.id.datospedido);
        c2 = v.findViewById(R.id.contenedorvacio);
        orderList = v.findViewById(R.id.todospedidos);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                myOrderList();
            }
        });
        myOrderList();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sgl = new Sgl(getContext(), this);
        urlOrder = new UrlOrder();
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

    public void myOrderList() {
        sgl.get(urlOrder.getMyOrderDay(client.getId()), null, "myOrderList");
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("myOrderList")) {

            ArrayList<OrderHeader> temp = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(data);
                int status = object.getInt("status");
                String message = object.getString("message");
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

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();
    }

    public void seeContainer() {
        fillRecyclerView();
        if (list.size() > 0) {
            c1.setVisibility(View.VISIBLE);
            c2.setVisibility(View.GONE);
        } else {
            c1.setVisibility(View.GONE);
            c2.setVisibility(View.VISIBLE);
        }
    }

    public void fillRecyclerView() {
        myOrderDayAdapter = new MyOrderDayAdapter(getContext(), list, this);
        orderList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        orderList.setAdapter(myOrderDayAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onViesDetailsProduct(View view, OrderHeader orderHeader) {
        if (orderHeader != null) {
            Intent i = new Intent(getContext(), MyOrderActivity.class);
            i.putExtra("orderHeader", orderHeader);
            startActivity(i);
        } else {
            new ToastAlert(0, "Error, no se puede visualizar el detalle del pedido.", getContext(), getLayoutInflater()).toastPersonalization();
        }
    }

    @Override
    public void quantityOrder(View view, Product product, String option, TextView quantity, int position) {

    }

    @Override
    public void setStatusOrder(View view, OrderHeader orderHeader, int status) {

    }
}
