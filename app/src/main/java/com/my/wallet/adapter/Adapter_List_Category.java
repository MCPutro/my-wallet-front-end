package com.my.wallet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.my.wallet.activity.Activity_New;
import com.my.wallet.env.category;
import com.my.wallet.env.iconList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Adapter_List_Category extends RecyclerView.Adapter<Adapter_List_Category.myHolder>{

    private final Context context;
    //private Map<String, category> category;
    private final List<String> keys;

    public Adapter_List_Category(Context context, Map<String, category> category) {
        this.context = context;
        //this.category = category;
        this.keys = new ArrayList<>(category.keySet());
    }

    @NonNull
    @NotNull
    @Override
    public myHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       return new myHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myHolder h, int position) {
        String key = this.keys.get(position);
        h.logo_category.setImageResource(iconList.expense_category().get(key).getLogo_category());

        h.text_category.setText(key);

        h.category_layout.setOnClickListener(v -> {
            showSubCategory(key);
        });
    }

    private void showSubCategory(String key) {
        List<String> keys_sub = new ArrayList<>(iconList.expense_category().get(key).getSub_category().keySet());
//        customSimpleAdapter arrayAdapter = new customSimpleAdapter(this.context, key, test);

        ListAdapter adapter = new ArrayAdapter(this.context, 0, keys_sub){
            @SuppressLint("ViewHolder")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
                String subCategory = (String) getItem(position);
                ((TextView) v.findViewById(R.id.text_category)).setText(subCategory);
                if (iconList.expense_category().get(key).getSub_category().get(subCategory) != null) {
                    ((ImageView) v.findViewById(R.id.logo_category)).setImageResource(iconList.expense_category().get(key).getSub_category().get(subCategory));
                }

                return v;
            }
        };

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this.context, R.style.AlertDialogTheme)
                .setTitle(key).setAdapter(adapter, (dialog, which) -> {
                    //Toast.makeText(context, "Category: "+key, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "Sub Category: "+keys_sub.get(which), Toast.LENGTH_SHORT).show();
                    if (this.context instanceof Activity_New) {
                        ((Activity_New) this.context).setCategory(key, keys_sub.get(which));
                        ((Activity_New) this.context).closeBottomSheet();
                    } else {}
                })
                ;
        materialAlertDialogBuilder.show();
    }

    @Override
    public int getItemCount() {
        return this.keys.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        CardView category_layout;
        ImageView logo_category;
        TextView text_category;

        public myHolder(@NonNull @NotNull View iV) {
            super(iV);
            this.category_layout = iV.findViewById(R.id.category_layout);
            this.logo_category = iV.findViewById(R.id.logo_category);
            text_category = iV.findViewById(R.id.text_category);
        }
    }
}
