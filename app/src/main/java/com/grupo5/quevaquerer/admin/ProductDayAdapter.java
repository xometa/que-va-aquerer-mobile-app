package com.grupo5.quevaquerer.admin;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.url.UrlProduct;

import java.util.List;

public class ProductDayAdapter extends RecyclerView.Adapter<ProductDayAdapter.MyHolder> {
    private Context c;
    private List<Product> list;

    public ProductDayAdapter(Context c, List<Product> list) {
        this.c = c;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductDayAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_productday, parent, false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDayAdapter.MyHolder holder, int position) {
        //cargando los datos a los elementos
        String path = list.get(position).getImage();
        if (!path.equals("") && path != null && path != "null") {
            Glide.with(c).load(UrlProduct.urlPhoto + path.substring(2, path.length())).into(holder.imageproductday);
        } else {
            holder.imageproductday.setImageResource(R.drawable.sinfoto);
        }
        holder.nameproductday.setText(list.get(position).getName().toString());
        holder.quantityproductday.setText(Html.fromHtml("<font color='#000000'><b>Cantidad:</b> </font>"+String.valueOf(list.get(position).getQuantity())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView imageproductday;
        TextView nameproductday;
        TextView quantityproductday;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageproductday = itemView.findViewById(R.id.imagenproductodia);
            nameproductday = itemView.findViewById(R.id.nombreproductodia);
            quantityproductday = itemView.findViewById(R.id.cantidadproductodia);
        }
    }
}
