package com.grupo5.quevaquerer.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.url.UrlCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    Context ctx;
    List<Category> list;
    CategoryItemClickListener cc;

    public CategoryAdapter(Context ctx, List<Category> list, CategoryItemClickListener cc) {
        this.ctx = ctx;
        this.list = list;
        this.cc = cc;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_category, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.img.setClipToOutline(true);
        holder.category.setText(list.get(position).getName());
        String url = list.get(position).getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(ctx).load(UrlCategory.urlPhoto + url.substring(2, url.length())).into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.sinfoto);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView category;
        private ImageView img;
        private ConstraintLayout content;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.namecategory);
            img = (ImageView) itemView.findViewById(R.id.imagecategory);
            content = itemView.findViewById(R.id.contenedor);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cc.onCategoryClick(list.get(getAdapterPosition()), v);
                }
            });
        }
    }
}
