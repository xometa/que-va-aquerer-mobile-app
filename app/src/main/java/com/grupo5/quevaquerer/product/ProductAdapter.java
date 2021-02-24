package com.grupo5.quevaquerer.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.url.UrlProduct;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context ctx;
    List<Product> list;
    DecimalFormat df = new DecimalFormat("0.00");
    ProductItemClickListener itemClickListener;

    public ProductAdapter(Context ctx, List<Product> list, ProductItemClickListener clickListener) {
        this.ctx = ctx;
        this.list = list;
        this.itemClickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_product, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.image.setClipToOutline(true);
        String url = list.get(position).getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(ctx).load(UrlProduct.urlPhoto + url.substring(2, url.length())).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sinfoto);
        }
        holder.t1.setText(list.get(position).getName());
        holder.t2.setText("$ " + String.valueOf(df.format(list.get(position).getPrice())));
        holder.t3.setText(list.get(position).getCategory().getName());
        holder.t4.setText("Cant. " + String.valueOf(list.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView t1;
        private TextView t2;
        private TextView t3;
        private TextView t4;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageproduct);
            t1 = itemView.findViewById(R.id.nameproduct);
            t2 = itemView.findViewById(R.id.preciproduct);
            t3 = itemView.findViewById(R.id.categoryproduct);
            t4 = itemView.findViewById(R.id.cantidaddisponible);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClickOptionsProduct(list.get(getAdapterPosition()), v);
                }
            });
        }
    }
}
