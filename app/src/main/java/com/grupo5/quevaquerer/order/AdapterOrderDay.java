package com.grupo5.quevaquerer.order;

import android.content.Context;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.url.UrlOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterOrderDay extends RecyclerView.Adapter<AdapterOrderDay.MyViewHolder> {
    Context c;
    ArrayList<OrderHeader> list;
    DecimalFormat df = new DecimalFormat("0.00");
    AdapterSubItem adapterSubItem;
    Geocoder geocoder;
    OrderItemClickListener click;

    public AdapterOrderDay(Context c, ArrayList<OrderHeader> list, OrderItemClickListener click) {
        this.c = c;
        this.list = list;
        this.click = click;
    }

    @NonNull
    @Override
    public AdapterOrderDay.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_orderday, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderDay.MyViewHolder h, int position) {
        String url = list.get(position).getClient().getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(c).load(UrlOrder.urlPhoto + url.substring(2, url.length())).into(h.image_cliente);
        } else {
            h.image_cliente.setImageResource(R.drawable.sinfoto);
        }

        h.name_client.setText(list.get(position).getClient().getName() + " " + list.get(position).getClient().getLastname());
        if (list.get(position).getLatitude() == 0 && list.get(position).getLongitude() == 0) {
            h.address_client.setText("No se ha especificado una dirección");
        } else {

            h.address_client.setText(Props.getLocationName(c, list.get(position).getLatitude(), list.get(position).getLongitude()));
        }
        if (list.get(position).getDetailsOrders().size() > 0 && list.get(position).getDetailsOrders() != null) {
            h.quantity_product.setText(String.valueOf(list.get(position).getDetailsOrders().size()));
        } else {
            h.quantity_product.setText(String.valueOf(0));
        }
        h.money.setText("$ " + String.valueOf(df.format(list.get(position).getTotalorder())));

        if (list.get(position).getStatusorder() == 1) {
            h.status.setChecked(true);
        } else {
            h.status.setChecked(false);
        }
        //cargando el adaptador con los productos
        adapterSubItem = new AdapterSubItem(c, list.get(position).getDetailsOrders());
        h.listproducts.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false));
        h.listproducts.setAdapter(adapterSubItem);

        boolean isExpanded = list.get(position).isExpanded();
        h.cl.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_cliente;
        private TextView name_client;
        private TextView address_client;
        private TextView quantity_product;
        private TextView money;
        private RecyclerView listproducts;
        private ConstraintLayout cl;
        private ConstraintLayout cv;
        private Switch status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_cliente = itemView.findViewById(R.id.imageodcliente);
            name_client = itemView.findViewById(R.id.nombreodcliente);
            address_client = itemView.findViewById(R.id.oddireccioncliente);
            quantity_product = itemView.findViewById(R.id.odcantidad);
            money = itemView.findViewById(R.id.odmonto);
            listproducts = itemView.findViewById(R.id.rv_subitemproduct);
            cl = itemView.findViewById(R.id.expandible);
            status = itemView.findViewById(R.id.estadopedido);
            cv = itemView.findViewById(R.id.si);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderHeader oh = list.get(getAdapterPosition());
                    oh.setExpanded(!oh.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.setStatusOrder(v, list.get(getAdapterPosition()), status.isChecked() ? 1 : 0);
                }
            });
        }
    }
}
