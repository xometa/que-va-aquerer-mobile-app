package com.grupo5.quevaquerer.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.grupo5.quevaquerer.MainActivity;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.entity.Money;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.props.CallbackResponse;
import com.grupo5.quevaquerer.props.Constantes;
import com.grupo5.quevaquerer.props.MyAxisValueFormatter;
import com.grupo5.quevaquerer.props.Sgl;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.props.ToastAlert;
import com.grupo5.quevaquerer.props.YAxisFor;
import com.grupo5.quevaquerer.url.UrlCategory;
import com.grupo5.quevaquerer.url.UrlClient;
import com.grupo5.quevaquerer.url.UrlOrder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeAdminFragment extends Fragment implements CallbackResponse {
    View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView quantityOrders, quantityClients, quantityMOney;
    private List<Product> listproductsday;
    private ProductDayAdapter productDayAdapter;
    DecimalFormat df = new DecimalFormat("0.00");
    //Clase de las url's
    UrlOrder urlOrder;
    RequestParams params;
    Sgl sgl;
    Client client;
    //contenedores
    CardView container1;
    BarChart barChart;

    public HomeAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home_admin, container, false);
        swipeRefreshLayout = v.findViewById(R.id.swipehome);
        quantityOrders = v.findViewById(R.id.quantityorders);
        quantityClients = v.findViewById(R.id.quantityclients);
        quantityMOney = v.findViewById(R.id.quantitymoney);
        recyclerView = v.findViewById(R.id.grid_productosdia);
        container1 = v.findViewById(R.id.contenedor);
        barChart = v.findViewById(R.id.ingresossemanas);
        dataClient();
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showElements();
            }
        });
        showElements();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlOrder = new UrlOrder();
        params = new RequestParams();
        sgl = new Sgl(getContext(), this);
        listproductsday = new ArrayList<>();
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

    public void showElements() {
        sgl.get(urlOrder.getHomeDetails("homeDetails"), null, "homeDetails");
    }

    @Override
    public void success(String data, String option) {
        if (option.equals("homeDetails")) {

            ArrayList<Product> temp = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(data);
                int quantityClient = obj.getInt("quantityClient");
                int quantityOrder = obj.getInt("quantityorders");
                Double money = obj.getDouble("money");
                JSONArray lpd = new JSONArray(obj.getString("listProductsDay"));
                JSONArray moneyWeek = new JSONArray(obj.getString("dayMoney"));
                if (quantityClient > 0) {
                    quantityClients.setText(String.valueOf(quantityClient));
                } else {
                    quantityClients.setText(String.valueOf(0));
                }
                if (quantityOrder > 0) {
                    quantityOrders.setText(String.valueOf(quantityOrder));
                } else {
                    quantityOrders.setText(String.valueOf(0));
                }
                if (money > 0) {
                    quantityMOney.setText("$ " + String.valueOf(df.format(money)));
                } else {
                    quantityMOney.setText("$ " + String.valueOf(df.format(0)));
                }

                for (int p = 0; p < lpd.length(); p++) {
                    temp.add(new Product(lpd.getJSONObject(p).getInt("idproduct"),
                            lpd.getJSONObject(p).getString("image"),
                            lpd.getJSONObject(p).getString("name"),
                            lpd.getJSONObject(p).getInt("quantity")));
                }//datos para la grafica
                ArrayList<Money> days = new ArrayList<>();
                days.add(new Money("Lunes", 0));
                days.add(new Money("Martes", 0));
                days.add(new Money("Miercoles", 0));
                days.add(new Money("Jueves", 0));
                days.add(new Money("Viernes", 0));
                days.add(new Money("Sabado", 0));
                days.add(new Money("Domingo", 0));
                for (int j = 0; j < days.size(); j++) {
                    String day1 = days.get(j).getDay();
                    for (int i = 0; i < moneyWeek.length(); i++) {
                        String day2 = moneyWeek.getJSONObject(i).getString("day");
                        if (day1.equals(day2)) {
                            days.get(j).setMoney(moneyWeek.getJSONObject(i).getDouble("quantity"));
                        }
                    }
                }
                dataBarChart(days);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listproductsday = temp;
            seeProducts();
        }
    }

    @Override
    public void fail(String data) {
        new ToastAlert(0, "La petición solicitada, no ha podido ser procesada.", getContext(), getLayoutInflater()).toastPersonalization();
    }

    public void dataBarChart(ArrayList<Money> money) {
        ArrayList<String> days = new ArrayList<String>();
        days.add("Lunes");
        days.add("Martes");
        days.add("Miercoles");
        days.add("Jueves");
        days.add("Viernes");
        days.add("Sábado");
        days.add("Domingo");
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < money.size(); i++) {
            entries.add(new BarEntry((float) money.get(i).getMoney(), i));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Ingresos de la semana");
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueFormatter(new MyAxisValueFormatter());
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setValueFormatter(new YAxisFor());
        YAxis yAxiss = barChart.getAxisRight();
        yAxiss.setValueFormatter(new YAxisFor());
        BarData barData = new BarData(days, barDataSet);
        barChart.setData(barData);
        barChart.setDescription("Ingresos de la semana");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animate();

    }

    public void seeProducts() {
        chargeGridView();
        if (listproductsday.size() > 0) {
            container1.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            container1.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void chargeGridView() {
        productDayAdapter = new ProductDayAdapter(getActivity(), listproductsday);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(productDayAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
