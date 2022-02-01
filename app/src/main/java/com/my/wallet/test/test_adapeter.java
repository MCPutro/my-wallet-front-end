package com.my.wallet.test;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.my.wallet.R;
import com.my.wallet.env.category;
import com.my.wallet.env.iconList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class test_adapeter extends RecyclerView.Adapter<test_adapeter.holderKu>{

    private Context context;
    private Map<String, category> categorTest;
    private List<String> keys;

    public test_adapeter(Context context, Map<String, category> categorTest) {
        this.context = context;
        this.categorTest = categorTest;
        this.keys = new ArrayList<>(this.categorTest.keySet());
    }

    @NonNull
    @NotNull
    @Override
    public holderKu onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new holderKu(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull holderKu h, int position) {
        String key = this.keys.get(position);
        h.logo3.setImageResource(iconList.expense_category().get(key).getLogo_category());

        h.textCategory3.setText(key);

        h.layout3.setOnClickListener(v -> {
            showPop(key);
        });
    }

    private void showPop(String key){
        List<String> test = new ArrayList<>(iconList.expense_category().get(key).getSub_category().keySet());
//        customSimpleAdapter arrayAdapter = new customSimpleAdapter(this.context, key, test);

        ListAdapter arrayAdapter = new ArrayAdapter(this.context, 0, test){
            @SuppressLint("ViewHolder")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.test3, parent, false);
                String subCategory = (String) getItem(position);
                ((TextView) v.findViewById(R.id.textCategory3)).setText(subCategory);
                if (iconList.expense_category().get(key).getSub_category().get(subCategory) != null) {
                    ((ImageView) v.findViewById(R.id.logo3)).setImageResource(iconList.expense_category().get(key).getSub_category().get(subCategory));
                }

                return v;
            }
        };

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this.context, R.style.AlertDialogTheme)
                .setTitle(key).setAdapter(arrayAdapter, (dialog, which) -> {
                    Toast.makeText(context, "Category: "+key, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "Sub Category: "+test.get(which), Toast.LENGTH_SHORT).show();
                })
                ;
        materialAlertDialogBuilder.show();
    }

    @Override
    public int getItemCount() {
        return this.categorTest.size();
    }

    public class holderKu extends RecyclerView.ViewHolder {
        CardView layout3;
        ImageView logo3;
        TextView textCategory3;

        private holderKu(@NonNull @NotNull View iV) {
            super(iV);
            this.layout3 = iV.findViewById(R.id.layout3);
            this.logo3 = iV.findViewById(R.id.logo3);
            textCategory3 = iV.findViewById(R.id.textCategory3);
        }
    }
}
