package com.grupo5.quevaquerer.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.admin.HomeAdminActivity;
import com.grupo5.quevaquerer.entity.DetailsOrder;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.props.Props;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailsOrderActivity extends AppCompatActivity {

    AdapterDetailProducts adapter;
    ArrayList<DetailsOrder> list;
    RecyclerView recyclerView;
    TextView date, details_total;
    OrderHeader orderHeader;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_order);
        setTitle("Detalles del pedido");
        toolbar=findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_dpedidos);
        date = findViewById(R.id.fecha);
        details_total = findViewById(R.id.totaldetalle);
        list = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderHeader = (OrderHeader) getIntent().getExtras().getSerializable("orderHeader");
        if (orderHeader != null) {
            date.setText(Props.formatDate(orderHeader.getDateorder()));
            list = orderHeader.getDetailsOrders();
            if (list.size() > 0 && list != null) {
                adapter = new AdapterDetailProducts(this, list);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                operationsDetails(list);
            }
        }
    }

    public void onBack(View v) {
        action();
    }

    @Override
    public void onBackPressed() {
        action();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                action();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void action() {
        HomeAdminActivity.back = "AllOrders";
        finish();
        /*Intent i = new Intent(this, HomeAdminActivity.class);
        startActivity(i);*/
    }

    public void operationsDetails(ArrayList<DetailsOrder> l) {
        DecimalFormat df = new DecimalFormat("0.00");
        double total = 0;
        for (int i = 0; i < l.size(); i++) {
            total += (l.get(i).getQuantity() * l.get(i).getPrice());
        }
        details_total.setText(String.valueOf(df.format(total)));
    }
}
