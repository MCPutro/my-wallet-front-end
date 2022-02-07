package com.my.wallet.activity;

import android.text.Spanned;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.MyData;
import com.my.wallet.model.Wallet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet_List extends AppCompatActivity{
    private MyData md;

    //**component**//
    /**toolbar**/
    private MaterialToolbar _wallet_toolbar;

    /**list**/
    private RecyclerView _wallet_list;

    /**adapter**/
    private Wallet_list_adapter wla;

    private final static int NAV_WALLET_UPDATE = 2001;

    @Override
    public void onBackPressed() {

        /**Intent intent = new Intent();
        intent.putExtra("md", md);
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();**/

        back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

//        getWindow().setStatusBarColor(Color.WHITE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initial();

        setComponentListener();

        setComponentAdapter();
    }

    private void initial() {
        /**component**/
        this._wallet_toolbar = findViewById(R.id.wallet_toolbar);
        this._wallet_list = findViewById(R.id.wallet_list);

    }

    private void setComponentListener(){
        this._wallet_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        this._wallet_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createWallet();
                return true;
            }
        });
    }

    private void setComponentAdapter(){
        wla = new Wallet_list_adapter(this, this.md.getListWallet());
        this._wallet_list.setAdapter(wla);
    }

    private void back(){
        Intent intent = new Intent();
        intent.putExtra("md", md);
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }

    private void createWallet(){
        Intent intent = new Intent();
        intent.putExtra("md", md);
        setResult(RESULT_FIRST_USER, intent);
        finish();
        super.onBackPressed();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == NAV_WALLET_UPDATE) {
            assert data != null;
            this.md = (MyData) data.getSerializableExtra("md");
            setComponentAdapter();
            //Toast.makeText(this, "update : "+resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    private class Wallet_list_adapter extends RecyclerView.Adapter<Wallet_list_adapter.ViewHolder>{
        private Context context;
        private LayoutInflater layoutInflater;
        private Map<String, Wallet> temp_wallet_list;

        private ArrayList<String> keys;

        public Wallet_list_adapter(Context context, HashMap<String, Wallet> temp_wallet_list) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.temp_wallet_list = temp_wallet_list;

            this.keys = new ArrayList<>(this.temp_wallet_list.keySet());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = this.layoutInflater.inflate(R.layout.item_wallet, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder._wallet_item_name.setText(this.temp_wallet_list.get(this.keys.get(position)).getName());
            holder._wallet_item_balance.setText(lov.currencyFormat.format(this.temp_wallet_list.get(this.keys.get(position)).getNominal()));
            if (iconList.wallet_type().get(this.temp_wallet_list.get(this.keys.get(position)).getType()) == null) {
                holder._wallet_item_icon.setVisibility(View.INVISIBLE);
            }else{
                    holder._wallet_item_icon.setImageResource(iconList.wallet_type().get(this.temp_wallet_list.get(this.keys.get(position)).getType()));
            }
            holder._wallet_item_id.setText(this.keys.get(position));

            if (iconList.card_background().get(this.temp_wallet_list.get(this.keys.get(position)).getBackgroundColor()) == null) {
                holder._cardBackground.setBackgroundResource(R.drawable.backgroud1);
            }else {
                holder._cardBackground.setBackgroundResource(iconList.card_background().get(this.temp_wallet_list.get(this.keys.get(position)).getBackgroundColor()).getValue());
            }

            holder._wallet_item_name.setTextColor(Color.parseColor(this.temp_wallet_list.get(this.keys.get(position)).getTextColor()));
            holder._wallet_item_idr.setTextColor(Color.parseColor(this.temp_wallet_list.get(this.keys.get(position)).getTextColor()));
            holder._wallet_item_balance.setTextColor(Color.parseColor(this.temp_wallet_list.get(this.keys.get(position)).getTextColor()));
            holder._wallet_item_id.setTextColor(Color.parseColor(this.temp_wallet_list.get(this.keys.get(position)).getTextColor()));

            holder._wallet_item_option.setOnClickListener(v -> WalletOptionsShow(holder, v));

        }

        @Override
        public int getItemCount() {
            return temp_wallet_list.size();
        }

        private void WalletOptionsShow(ViewHolder holder, View v) {
            //Toast.makeText(this.context, holder.getAdapterPosition()+" <", Toast.LENGTH_SHORT).show();

            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_wallet, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.edit_item) {
                        //Toast.makeText(context, "update 1", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, Wallet_Update.class);
                        i.putExtra("walletId", keys.get(holder.getAdapterPosition()));
                        i.putExtra("md", md);
                        startActivityForResult(i, NAV_WALLET_UPDATE);

                        return true;
                    }
                    else if (item.getItemId() == R.id.delete_item) {
                        showConfirmRestore(holder.getAdapterPosition());
                        return true;
                    }
                    else return false;
                }
            });
            popup.show();
        }

        private void showConfirmRestore(int position){
            String msg = "Do you want to delete <b>"+this.temp_wallet_list.get(this.keys.get(position)).getName()+"</b> ? After deleting you can't undo!";

            Spanned ss;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ss = Html.fromHtml(msg,  Html.FROM_HTML_MODE_LEGACY);
            } else {
                ss = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY);
            }

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                    .setTitle("Delete Wallet")
                    .setMessage(ss)
                    .setNegativeButton("Yes", (dialog, which) -> confirmRemoveWallet(position))
                    .setPositiveButton("No", (dialog, which) -> {
                        dialog.cancel();
                        dialog.dismiss();
                    });
            materialAlertDialogBuilder.show();
        }

        private void confirmRemoveWallet(int position){
            String url = api.url+api.path_walletRemove;
            StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                    response -> {
                        try {
                            JSONObject resp = new JSONObject(response);
                            md.getListWallet().remove(keys.get(position));
                            notifyItemRemoved(position);
                            dataStore.getInstance().saveData(null, null, null, null, md);
                        }catch (Throwable t){}
                    },
                    error -> { }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("walletId", md.getListWallet().get(keys.get(position)).getId());
                    params.put("Authorization", "Bearer "+dataStore.getInstance().getData().getToken());
                    return params;
                }
            };

            deleteRequest.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(deleteRequest);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout _cardBackground;
            private TextView _wallet_item_name;
            private ImageButton _wallet_item_option;
            private ImageView _wallet_item_icon;
            private TextView _wallet_item_idr;
            private TextView _wallet_item_balance;
            TextView _wallet_item_id;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                _cardBackground	= itemView.findViewById(R.id.cardBackground);
                _wallet_item_name = itemView.findViewById(R.id.wallet_item_name);
                _wallet_item_option = itemView.findViewById(R.id.wallet_item_option);
                _wallet_item_icon = itemView.findViewById(R.id.wallet_item_icon);
                _wallet_item_idr = itemView.findViewById(R.id.wallet_item_idr);
                _wallet_item_balance = itemView.findViewById(R.id.wallet_item_balance);
                _wallet_item_id = itemView.findViewById(R.id.wallet_item_id);
            }
        }
    }
}