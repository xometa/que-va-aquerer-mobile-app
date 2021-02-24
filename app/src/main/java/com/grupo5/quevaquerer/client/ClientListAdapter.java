package com.grupo5.quevaquerer.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.props.SharedPreferencesManager;
import com.grupo5.quevaquerer.url.UrlClient;

import java.util.List;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.MyViewHolder> {
    Context ctx;
    List<Client> list;
    ClientItemClickListener clientItemClickListener;

    public ClientListAdapter(Context ctx, List<Client> list, ClientItemClickListener clientItemClickListener) {
        this.ctx = ctx;
        this.list = list;
        this.clientItemClickListener = clientItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_listclient, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Client client = SharedPreferencesManager.getSomeSetValue("client");
        if (client.getId() != list.get(position).getId()) {
            holder.cardView.setVisibility(View.VISIBLE);
            String url = list.get(position).getImage();
            if (!url.equals("") && url != null && url != "null") {
                Glide.with(ctx).load(UrlClient.urlPhoto + url.substring(2, url.length())).into(holder.imageclient);
            } else {
                holder.imageclient.setImageResource(R.drawable.sinfoto);
            }
            holder.tvnamelcient.setText(list.get(position).getName() + " " + list.get(position).getLastname());
            if (list.get(position).getRol() == 1) {
                if (list.get(position).getLongitude() == 0 && list.get(position).getLatitude() == 0) {
                    holder.tvdirectionclient.setText("No se ha registrado una dirección");
                } else {
                    holder.tvdirectionclient.setText(Props.getLocationName(ctx, list.get(position).getLatitude(), list.get(position).getLongitude()));
                }
            } else {
                holder.tvdirectionclient.setText("Administrador");
            }
            holder.tvphoneclien.setText(list.get(position).getPhone());
        } else {
            holder.cardView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageclient;
        private ImageView moreoptions;
        private TextView tvnamelcient;
        private TextView tvdirectionclient;
        private TextView tvphoneclien;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageclient = itemView.findViewById(R.id.imgcliente);
            moreoptions = itemView.findViewById(R.id.masopcionescliente);
            tvnamelcient = itemView.findViewById(R.id.tvnombrecliente);
            tvdirectionclient = itemView.findViewById(R.id.tvdireccioncliente);
            tvphoneclien = itemView.findViewById(R.id.tvtelefonocliente);
            cardView = itemView.findViewById(R.id.cardViewclient);
            moreoptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clientItemClickListener.onClientClick(list.get(getAdapterPosition()), v);
                }
            });
        }
    }
}
