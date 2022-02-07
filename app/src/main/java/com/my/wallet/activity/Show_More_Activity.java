package com.my.wallet.activity;

import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.my.wallet.adapter.Adapter_List_Activities;
import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;

import com.my.wallet.popUpNotification;
import com.whiteelephant.monthpicker.MonthPickerDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Show_More_Activity extends AppCompatActivity {
    private MyData md;
    private Date startDate, endDate;
    private Adapter_List_Activities adapterList;
    //private String custem_periode;


    private RecyclerView _Activity_show_more_list;
    private LinearLayout _Activity_show_more_list_no_data;
    private TabLayout _Activity_show_more_tab;
    private MaterialToolbar _Activity_show_more_toolbar;
    private SwipeRefreshLayout _Activity_show_more_list_refresh;
    private TextView _Activity_show_more_periode;

    public void setMd(MyData md) {
        this.md = md;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("md", md);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more);

//        getWindow().setStatusBarColor(Color.WHITE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initVariable();

        setDateRange(0, null);

        setListener();

        reload();


    }

    private void initVariable(){
        this._Activity_show_more_toolbar = findViewById(R.id.Activity_show_more_toolbar);
        this._Activity_show_more_tab = findViewById(R.id.Activity_show_more_tab);
        this._Activity_show_more_list = findViewById(R.id.Activity_show_more_list);
        this._Activity_show_more_list_no_data = findViewById(R.id.Activity_show_more_list_no_data);
        this._Activity_show_more_list_refresh = findViewById(R.id.Activity_show_more_list_refresh);
        this._Activity_show_more_periode = findViewById(R.id.Activity_show_more_periode);
    }

    private void setListener(){
        this._Activity_show_more_toolbar.setNavigationOnClickListener(v -> onBackPressed());

        this._Activity_show_more_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals(getString(R.string.this_month))){
                    //_Activity_show_more_list_refresh.setEnabled(true);
                    _Activity_show_more_periode.setVisibility(View.GONE);
                    showList(0);
                } else if (tab.getText().toString().equals(getString(R.string.last_month))) {
                    //_Activity_show_more_list_refresh.setEnabled(true);
                    _Activity_show_more_periode.setVisibility(View.GONE);
                    showList(-1);
                } else {
                    //_Activity_show_more_list_refresh.setEnabled(false);
                    _Activity_show_more_periode.setVisibility(View.VISIBLE);
                    showNotifNoData(true);
                    showMonthYearDialog();
                }
            }
        });

        this._Activity_show_more_list_refresh.setOnRefreshListener(this::loadMore);
    }

    private void showList(int i){
        setDateRange(i, null);
        reload();
    }

    private void reload(){
        adapterList = new Adapter_List_Activities( this, this.md.getListActivities().size(), this.md, this.startDate, this.endDate, "show_more");
        this._Activity_show_more_list.setAdapter(adapterList);
    }

    private void loadMore(){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        okhttp3.OkHttpClient client = builder.build();

        String url = api.url+api.path_activity;
        okhttp3.Request request = new okhttp3.Request.Builder()
                //.url("https://jsonplaceholder.typicode.com/todos/1")
                .url(url)
                .addHeader("UID", dataStore.getInstance().getData().getUID())
                .addHeader("Authorization", "Bearer "+dataStore.getInstance().getData().getToken())
                .addHeader("period", lov.dateFormatter3.format(this.startDate).substring(0, 7))
                .get()
                .build();

        popUpNotification.show(this, lov.popUpType.LOADING, "");

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("debuging", "Request Failed."+e.getMessage());
                responseAsync("Request Failed."+e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        Log.d("debuging", responseString);
                        responseAsync(responseString);
                    } else {
                        Log.d("debuging", "Error "+ response);
                        responseAsync("Error "+ response);
                    }
                } catch (IOException e) {
                    Log.d("debuging", "Exception caught : ", e);
                    responseAsync("Error "+ e.getMessage());
                }
            }
        });

        this._Activity_show_more_list_refresh.setRefreshing(false);
    }

    private void responseAsync(final String responseStr) {
        popUpNotification.close();

        runOnUiThread(() -> {
            try {

                JSONObject resp = new JSONObject(responseStr);
                String sta = resp.getString("status");
                if ("success".equalsIgnoreCase(sta)) {
                    JSONArray actList = resp.getJSONObject("data").getJSONArray("activities");

                    for (int i = 0; i < actList.length(); i++) {
                        JSONObject tmp = (JSONObject) actList.get(i);
//                        a = new Activities(
//                                tmp.getString("id"),
//                                tmp.getString("walletId"),
//                                tmp.getString("walletName"),
//
//                                tmp.getString("title"),
//                                tmp.getString("category"),
//
//                                tmp.getDouble("nominal"),
//                                tmp.getString("desc"),
//                                lov.dateFormatter4.parse(tmp.getString("date")),
//                                tmp.getBoolean("income"));
                        Activities a = Activities.builder()
                                .id(tmp.getString("id"))
                                .walletId(tmp.getString("walletId"))
                                .walletName(tmp.getString("walletName"))
                                .titleActivities(tmp.getString("title"))
                                .category(tmp.getString("category"))
                                .descActivities(tmp.getString("desc"))
                                .nominalActivities(tmp.getDouble("nominal"))
                                .dateActivities(lov.dateFormatter4.parse(tmp.getString("date")))
                                .income(tmp.getBoolean("income"))
                                .build();

                        this.md.getListActivities().put(a.getId(), a);

                        //Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show();
                    }
                    dataStore.getInstance().saveData(null, null, null,null, this.md);
                    reload();
                }
                else{

                }
            }catch (Throwable t){
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void setDateRange(int i, Date date){
        Calendar calendar = Calendar.getInstance();

//        if (i == 0) {calendar.add(Calendar.MONTH, 0);}
//        else
//        if (i == -1) {calendar.add(Calendar.MONTH, -1);}

        if (i == 8) {
            calendar.setTime(date);
            //calendar.add(Calendar.MONTH, );
        }
        else
        {
            calendar.add(Calendar.MONTH, i);
        }

        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 10);
        startDate = calendar.getTime();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        endDate = calendar.getTime();


    }

    public void showNotifNoData(boolean status){
        if (status){
            this._Activity_show_more_list.setVisibility(View.GONE);
            this._Activity_show_more_list_no_data.setVisibility(View.VISIBLE);
        }else {
            this._Activity_show_more_list.setVisibility(View.VISIBLE);
            this._Activity_show_more_list_no_data.setVisibility(View.GONE);
        }
    }

    private void showMonthYearDialog(){
        Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(
                this,
                (selectedMonth, selectedYear) -> {
                    try {
                        String bulan = selectedMonth+1+"";
                        String custem_periode = selectedYear+"-"+( (bulan.length() == 1) ?"0"+bulan : bulan );

                        this.startDate = lov.dateFormatter4.parse(custem_periode+"-01T00:00:00.010+07:00");//yyyy-MM-dd'T'HH:mm:ss.SSSZ 2022-01-04T19:15:00.000+00:00

                        setDateRange(8, this.startDate);

                        loadMore();
                        this._Activity_show_more_periode.setText("Periode: "+custem_periode);

                    }catch (Throwable t){
                        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setMinYear(2010)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(2030)
                .setTitle("Select Period")
                .build()
                .show();
    }


}