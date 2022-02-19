package com.my.wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.my.wallet.R;
import com.my.wallet.activity.Activity_New;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.MyData;

import java.util.ArrayList;
import java.util.List;

public class Adapter_List_Wallet extends RecyclerView.Adapter<Adapter_List_Wallet.walletHolder>{
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> keys;
    private MyData md;
    private lov.trf trf;
    private lov.activityType type;

    public Adapter_List_Wallet(Context context, MyData md, lov.trf trf, lov.activityType type) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);

        this.md = md;
        this.keys = new ArrayList<>(md.getListWallet().keySet());

        this.trf = trf;
        this.type = type;
    }

    @NonNull
    @Override
    public walletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new walletHolder(this.layoutInflater.inflate(R.layout.item_wallet_trf, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull walletHolder holder, int position) {
        String name = md.getListWallet().get(this.keys.get(position)).getName();
        holder.name.setText(name);

        holder.id.setText(md.getListWallet().get(this.keys.get(position)).getId());

        holder.nominal.setText(context.getString(R.string.currency)+" "+ lov.currencyFormat.format(md.getListWallet().get(this.keys.get(position)).getNominal()));

        holder.logo.setImageResource(iconList.wallet_type_color().get(md.getListWallet().get(this.keys.get(position)).getType()));


        holder.layout.setOnClickListener(v -> {
            if (this.type == lov.activityType.TRANSFER) {
                ((Activity_New) context).updateTransferData(this.trf, keys.get(holder.getAdapterPosition()));
            }else {
                //Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
                ((Activity_New) context).setSelWallet(position, name);
            }
        });


    }

    @Override
    public int getItemCount() {
        return md.getListWallet().size();
    }

    public class walletHolder extends RecyclerView.ViewHolder {
        MaterialCardView layout;
        ImageView logo;
        TextView name, nominal, id;

        public walletHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.item_wallet_trf_layout);
            this.logo = itemView.findViewById(R.id.item_wallet_trf_icon);
            this.name = itemView.findViewById(R.id.item_wallet_trf_name);
            this.id = itemView.findViewById(R.id.item_wallet_trf_id);
            this.nominal = itemView.findViewById(R.id.item_wallet_trf_nominal);

        }
    }
}


