package com.my.wallet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.my.wallet.R;
import com.my.wallet.activity.Dashboard;
import com.my.wallet.activity.Show_More_Activity;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;
import com.my.wallet.popUpNotification;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.*;

public class Adapter_List_Activities extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private MyData md;
    private int limitListView;
    private Map<String, Activities> temp_activity_list;
    private ArrayList<String> keys;
    private String source;
    private Boolean []listExpand;

    public Adapter_List_Activities(Context context, int limitListView, MyData md, Date startDate, Date endDate, String source) {
        this.context = context;
        this.md = md;
        this.limitListView = limitListView;
        this.source = source;

        this.temp_activity_list = new LinkedHashMap<>();

        for (Map.Entry<String, Activities> act : this.md.getListActivities().entrySet()) {
            if (act.getValue().getDateActivities().after(startDate) && act.getValue().getDateActivities().before(endDate)){
                this.temp_activity_list.put(act.getKey(), act.getValue());
            }
        }

        if (this.temp_activity_list.size() > 0) {
            this.keys = new ArrayList<>(this.temp_activity_list.keySet());
            if (this.source.equalsIgnoreCase("dashboard")) {
                ((Dashboard)context).setTextComponent(this.md.getListWallet(), this.temp_activity_list);
                ((Dashboard)context).showNotifNoData(false);
            } else {
                ((Show_More_Activity)context).showNotifNoData(false);;
            }
        }else{
            this.keys = new ArrayList<>();
            if (this.source.equalsIgnoreCase("dashboard")) {
                ((Dashboard)context).showNotifNoData(true);
            } else {
                ((Show_More_Activity)context).showNotifNoData(true);
            }
        }

        this.listExpand = new Boolean[this.keys.size()];

        Arrays.fill(this.listExpand, false);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (this.source.equalsIgnoreCase("dashboard")) {
            if (viewType == TYPE_ITEM) {
                //Inflating recycle view item layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
                return new ItemViewHolder(itemView);
            } else if (viewType == TYPE_FOOTER) {
                //Inflating footer view
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
                return new FooterViewHolder(itemView);
            } else return null;
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (this.source.equalsIgnoreCase("dashboard")) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                printItemList(itemViewHolder, position);
            }
            else
            if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerHolder = (FooterViewHolder) holder;
                footerHolder.footerText.setOnClickListener(v -> {
                    ((Dashboard)context).showMoreActivity();
                });
            }
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            printItemList(itemViewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (this.source.equalsIgnoreCase("dashboard")) {
            return Math.min(this.keys.size(), this.limitListView) + 1;
        } else {
            return this.keys.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == Math.min(this.keys.size(), this.limitListView)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }





    private void printItemList(ItemViewHolder itemViewHolder, int position){
        if (!this.source.equalsIgnoreCase("dashboard")) {
            //itemViewHolder._activity_row_cardview.setCardElevation(2.0f);
        }

        if (this.temp_activity_list.get(this.keys.get(position)).getTitleActivities().equalsIgnoreCase("Transfer")) {
            itemViewHolder._activity_row_title.setText("Transfer");
            itemViewHolder._activity_row_icon.setImageResource(iconList.transfer_icon());

            itemViewHolder._activity_row_amount.setTextColor(context.getColor(R.color.primerDark));

            itemViewHolder._activity_row_delete.setOnClickListener(v -> {
                confirmDelete(position, lov.activityType.TRANSFER);
            });
        }
        else{
            itemViewHolder._activity_row_title.setText(this.temp_activity_list.get(this.keys.get(position)).getTitleActivities());

            if (this.temp_activity_list.get(this.keys.get(position)).isIncome()) {
                itemViewHolder._activity_row_amount.setTextColor(Color.parseColor("#4CAF50"));
                itemViewHolder._activity_row_icon.setImageResource(iconList.income_list_map().get(this.temp_activity_list.get(this.keys.get(position)).getTitleActivities()));
            } else {
                itemViewHolder._activity_row_amount.setTextColor(Color.parseColor("#E91E63"));
                //itemViewHolder._activity_row_icon.setImageResource(iconList.expense_list_map().get(this.temp_activity_list.get(this.keys.get(position)).getTitleActivities()));

                String k = this.temp_activity_list.get(keys.get(position)).getCategory();
                String sk = this.temp_activity_list.get(this.keys.get(position)).getTitleActivities();
                itemViewHolder._activity_row_icon.setImageResource(iconList.expense_category().get(k).getSub_category().get(sk));

            }

            itemViewHolder._activity_row_delete.setOnClickListener(v -> {
                confirmDelete(position, lov.activityType.INCOME);
            });
        }

        itemViewHolder._activity_row_wallet.setText(this.temp_activity_list.get(this.keys.get(position)).getWalletName());
            itemViewHolder._activity_row_desc.setText(this.temp_activity_list.get(this.keys.get(position)).getDescActivities());

        boolean isExpand = this.listExpand[position];//this.temp_activity_list.get(this.keys.get(position)).isExpands();
        itemViewHolder._activity_row_more_layout.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        if (itemViewHolder._activity_row_more_layout.getVisibility() == View.GONE) {
            itemViewHolder._activity_row_date.setText(lov.dateFormatter1.format(this.temp_activity_list.get(this.keys.get(position)).getDateActivities()));
            itemViewHolder._activity_row_amount.setText(context.getString(R.string.currency)+" "+lov.prettyCount(this.temp_activity_list.get(this.keys.get(position)).getNominalActivities(),0));
        } else {
            itemViewHolder._activity_row_date.setText(lov.dateFormatter2.format(this.temp_activity_list.get(this.keys.get(position)).getDateActivities()));
            itemViewHolder._activity_row_amount.setText(context.getString(R.string.currency)+" "+lov.currencyFormat.format(this.temp_activity_list.get(this.keys.get(position)).getNominalActivities()));
        }


    }

    private void confirmDelete(int position, lov.activityType f){
        String msg = "Delete this transaction (<b>"+this.temp_activity_list.get(this.keys.get(position)).getTitleActivities()+"</b>) ? ";

        Spanned ss = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ss = Html.fromHtml(msg,  Html.FROM_HTML_MODE_LEGACY);
        } else {
            ss = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY);
        }
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                .setTitle("Delete")
                .setMessage(ss)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    dialog.dismiss();
                })
                .setPositiveButton("Yes",(dialog, which) -> {
                    if (f == lov.activityType.TRANSFER)
                        DeleteTranfer(this.temp_activity_list.get(keys.get(position)), position);
                    else
                        DeleteActivity(position);
                });
        materialAlertDialogBuilder.show();
    }

    private void DeleteActivity(int position){
        try {
            popUpNotification.show(this.context, lov.popUpType.LOADING, "");

            String period = lov.dateFormatter3.format(this.temp_activity_list.get(keys.get(position)).getDateActivities()).substring(0, 7);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                    api.url + api.path_activity, null,
                    response -> {
                        popUpNotification.close();
                        try {
                            String status = response.getString("status");
                            if ("success".equalsIgnoreCase( status)) {
                                JSONObject data = response.getJSONObject("data");

                                md.getListActivities().remove(keys.get(position));

                                md.getListWallet().get(data.getString("walletid")).setNominal(data.getDouble("remainingBalance"));

                                dataStore.getInstance().saveData(null, null, null, null, md);

                                if (this.source.equalsIgnoreCase("dashboard")) {
                                    ((Dashboard) context).setMd(this.md);
                                    ((Dashboard) context).setTextComponent(this.md.getListWallet(), this.temp_activity_list);
                                    ((Dashboard) context).showRecentActivity();
                                }else{
                                    ((Show_More_Activity) context).setMd(this.md);
                                    this.temp_activity_list.remove(keys.get(position));
                                    this.keys.remove(position);
                                    notifyItemRemoved(position);
                                }

                            }else{
                                Toast.makeText(this.context, "error1 : "+response.toString(), Toast.LENGTH_LONG).show();
                            }
                        }catch (Throwable t){
                            Toast.makeText(this.context, "error2 : "+t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        popUpNotification.close();
                        Toast.makeText(this.context, "error3 : "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("UID", md.getUID());
                    params.put("period", period);
                    params.put("activityId", md.getListActivities().get(keys.get(position)).getId());
                    params.put("Authorization", "Bearer "+dataStore.getInstance().getData().getToken());
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);
        }catch (Throwable t){}
    }

    private void DeleteTranfer(Activities activities, int position){
        try {
            popUpNotification.show(this.context, lov.popUpType.LOADING, "");

            String period = lov.dateFormatter3.format(activities.getDateActivities()).substring(0, 7);

            JSONObject message = new JSONObject("{" +
                    "\"id\" : \""+activities.getId()+"\"," +
                    "\"walletIdSource\" : \""+activities.getWalletId().split("##")[0]+"\"," +
                    "\"walletIdDestination\" : \""+activities.getWalletId().split("##")[1]+"\"," +
                    "\"nominal\" : "+activities.getNominalActivities()+"," +
                    "\"fee\" : "+activities.getDescActivities().split("Fee:")[1].trim()+"" +
                    "}");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, api.url + api.path_walletCancelTransfer, message,
                    response -> {
                        try {
                            popUpNotification.close();
                            String status = response.getString("status");
                            if ("success".equalsIgnoreCase(status)){
                                JSONObject data = response.getJSONObject("data");
                                JSONObject newWalletData = data.getJSONObject("newWalletData");

                                md.getListWallet().get(newWalletData.getString("walletIdSource")).setNominal(newWalletData.getDouble("nominalSource"));
                                md.getListWallet().get(newWalletData.getString("walletIdDestination")).setNominal(newWalletData.getDouble("nominalDestination"));

                                md.getListActivities().remove(data.getString("id"));

                                dataStore.getInstance().saveData(null, null, null, null, md);

                                if (this.source.equalsIgnoreCase("dashboard")) {
                                    ((Dashboard) context).setMd(this.md);
                                    ((Dashboard) context).setTextComponent(this.md.getListWallet(), this.temp_activity_list);
                                    ((Dashboard) context).showRecentActivity();
                                }else{
                                    ((Show_More_Activity) context).setMd(this.md);
                                    this.temp_activity_list.remove(data.getString("id"));
                                    this.keys.remove(position);
                                    notifyItemRemoved(position);
                                }

                            } else {
                                Toast.makeText(context, "3 cancelTranfer: "+response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Throwable t){
                            Toast.makeText(context, "2 cancelTranfer: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        popUpNotification.close();
                        Toast.makeText(context, "0 cancelTranfer: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("UID", md.getUID());
                    params.put("period", period);
                    params.put("Authorization", "Bearer "+ dataStore.getInstance().getData().getToken());
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);
        }catch (Throwable t){
            Toast.makeText(context, "1 cancelTranfer: "+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /****/

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = view.findViewById(R.id.footer_text);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView _activity_row_cardview;

        ImageView _activity_row_icon;
        TextView _activity_row_title;
        TextView _activity_row_wallet;
        TextView _activity_row_amount;
        TextView _activity_row_date;

        //ImageButton _activity_row_opti;

        RelativeLayout _activity_row_more_layout;

        TextView _activity_row_desc;
        MaterialButton _activity_row_delete;
        MaterialButton _activity_row_edit;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this._activity_row_cardview = itemView.findViewById(R.id.activity_row_cardview);
            this._activity_row_cardview.setOnClickListener(v -> {
                listExpand[getAdapterPosition()] = !listExpand[getAdapterPosition()];
                notifyItemChanged(getAdapterPosition());
            });

            this._activity_row_icon = itemView.findViewById(R.id.activity_row_icon);
            this._activity_row_title = itemView.findViewById(R.id.activity_row_title);
            this._activity_row_wallet = itemView.findViewById(R.id.activity_row_wallet);
            this._activity_row_amount = itemView.findViewById(R.id.activity_row_amount);
            this._activity_row_date = itemView.findViewById(R.id.activity_row_date);

            //this._activity_row_opti = itemView.findViewById(R.id.activity_row_opti);

            this._activity_row_more_layout = itemView.findViewById(R.id.activity_row_more_layout);

            this._activity_row_desc = itemView.findViewById(R.id.activity_row_desc);
            this._activity_row_delete = itemView.findViewById(R.id.activity_row_delete);
            this._activity_row_edit = itemView.findViewById(R.id.activity_row_edit);
        }
    }
}
