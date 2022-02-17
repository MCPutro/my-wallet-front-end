package com.my.wallet.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;
import com.my.wallet.model.Wallet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Load_Data extends AppCompatActivity implements TextWatcher, Response.Listener<JSONObject>, Response.ErrorListener {

    private MyData md;
    private Boolean isNewUser;

    private dataStore ds;

    private TextView tv;

    private String urlAvatar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isNewUser = this.getIntent().getBooleanExtra("isNewUser", false);
        this.urlAvatar = this.getIntent().getStringExtra("urlAvatar");

        //getWindow().setStatusBarColor(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //api.getInstance(this);
        this.ds = dataStore.getInstance();

        this.initComponent();
        this.setComponentListener();


        //cek refresh token is expired
        String err = this.checkTokenHasExp(ds.getData().getRefreshToken(), lov.tokenType.REFRESH_TOKEN);
        if (err != null) {//if (err.equalsIgnoreCase("expired") || err.equalsIgnoreCase("error")) {
            Toast.makeText(this, "Your refresh token has expired.\nPlease re-signin!", Toast.LENGTH_SHORT).show();
            //this.ds.clear();
            Intent i = new Intent(this, Sign_In.class);
            startActivity(i);
            finish();
        }else{
            //cek token is expired
            err = this.checkTokenHasExp(ds.getData().getToken(), lov.tokenType.TOKEN);
            if (err != null) {
                refreshToken(ds.getData().getRefreshToken());
            }else{
                tv.setText("Load data ");
            }

        }
    }

    private void initComponent(){
        this.tv = findViewById(R.id.mainText);
    }

    private void setComponentListener(){
        this.tv.addTextChangedListener(this);
    }

    private String checkTokenHasExp(String token, lov.tokenType tokenType){
        try {
            Date currentDate = Calendar.getInstance().getTime();
            String expRefTokenString = new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), StandardCharsets.UTF_8);

            if (tokenType == lov.tokenType.REFRESH_TOKEN) {
                //String expRefTokenString = new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), StandardCharsets.UTF_8);
                long expRefToken = Long.parseLong(expRefTokenString);
                Date d1 = new Date(expRefToken * 1000);
                if (currentDate.after(d1))/**has expired**/{
                    return "expired";
                }
                return null;
            }else
            if (tokenType == lov.tokenType.TOKEN) {
                //String expRefTokenString = new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(expRefTokenString);
                long seconds = Long.parseLong(json.getString("exp"));
                Date d2 = new Date(seconds * 1000);

                if (currentDate.after(d2)){
                    return "expired";
                }
                return null;
            }
            else
                return null;

        }catch (Throwable t){
            Toast.makeText(this, "error1"+t.getMessage(), Toast.LENGTH_LONG).show();
            return "error";
        }
    }

    private void refreshToken(String refreshToken){
        try {
            String url = api.url+api.path_refreshToken;
            JSONObject requestMessage = lov.refreshTokenRequest(refreshToken);
//            new JSONObject(); //new JSONObject("{\"refreshToken\": \""+refreshToken+"\"} ");
//            requestMessage.put("refreshToken", refreshToken);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, requestMessage, this, this

            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);

        }catch (Throwable t){
            Toast.makeText(this, "error2 "+t.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void toDashboard(){
        //this.md.setUrlAvatar(this.urlAvatar);

        this.ds.saveData(null, null, null, null, this.md);
        finish();
        Intent i = new Intent(this, Dashboard.class);
        i.putExtra("md", md);
        startActivity(i);
    }

    private void generateDefaultData(String uid, String username){
        this.md = new MyData(uid, username, this.urlAvatar);
        this.ds.saveData(null, null, null,null, this.md);
    }

    private void getWalletList(String uid, String username, String token){
        this.md = new MyData(uid, username, this.urlAvatar);
        String url = api.url+api.path_getWalletByUID;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        if (!response.equals("[]")){
                            JSONArray json = new JSONArray(response);
                            Wallet tmp;
                            Gson g = new Gson();
                            for (int i = 0; i < json.length(); i++) {
                                tmp = g.fromJson(json.get(i).toString(), Wallet.class) ;
                                md.getListWallet().put(tmp.getId(), tmp);
                            }
                            ds.saveData(null, null, null, null, md);
                            //toDashboard();
                        }else{
                            //toDashboard();
                        }
                        //String period = Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1);
                        String month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
                        getRecentActivity(uid, Calendar.getInstance().get(Calendar.YEAR)+ "-" + ((month.length() == 1) ? ("0"+month) : month), token);
                    }catch (Throwable t){}
                },
                error -> Toast.makeText(Load_Data.this, "error3: "+error.getMessage(), Toast.LENGTH_LONG).show()
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("UID", uid);
                params.put("Authorization", "Bearer "+token);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                api.timeOut_ms,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        api.getInstance().getQueue().add(request);
    }

    private void getRecentActivity(String uid, String period, String token) {
        StringRequest request = new StringRequest(Request.Method.GET,
                api.url + api.path_activity,
                response -> {
                    try {
                        JSONObject resp = new JSONObject(response);
                        String status = resp.getString("status");
                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray act = resp.getJSONObject("data").getJSONArray("activities");
                            JSONObject tmp;
                            for (int i = 0; i < act.length(); i++) {
                                tmp = (JSONObject) act.get(i);
                                Activities a = Activities.builder()
                                        .id(tmp.getString("id"))
                                        .walletId(tmp.getString("walletId"))
                                        .walletName(tmp.getString("walletName"))
                                        .titleActivities(tmp.getString("title"))
                                        .category(tmp.getString("category"))
                                        .descActivities(tmp.getString("desc"))
                                        .nominalActivities(tmp.getDouble("nominal"))
                                        .dateActivities(lov.dateFormatter4.parse(tmp.getString("date")))
                                        .type(lov.activityType.valueOf(tmp.getString("type")))//.income(tmp.getBoolean("income"))
                                        .build();

                                //Toast.makeText(this, "tmp : "+a.toString(), Toast.LENGTH_SHORT).show();
                                md.getListActivities().put(a.getId(), a);
                            }
                            toDashboard();
                        }else{
                            Toast.makeText(this, "resp 2", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, response+"", Toast.LENGTH_LONG).show();
                        }
                    }catch (Throwable t){
                        Toast.makeText(this, "resp "+t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error5 : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("UID", uid);
                params.put("period", period);
                params.put("Authorization", "Bearer "+token);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                api.timeOut_ms,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        api.getInstance().getQueue().add(request);
    }

    private void loadData(){

        try {
            if (!TextUtils.isEmpty(this.ds.getData().getMyData())  ) {
                //this.md = new Gson().fromJson(this.ds.getData().getMyData(), MyData.class);
                this.md = this.ds.getG().fromJson(this.ds.getData().getMyData(), MyData.class);

                if (this.ds.getData().getUID().equals(this.md.getUID()))
                    toDashboard();
                else
                    getWalletList(this.ds.getData().getUID(), this.ds.getData().getUsername(), this.ds.getData().getToken());

            }else{
                if (isNewUser) {
                    generateDefaultData(this.ds.getData().getUID(), this.ds.getData().getUsername());
                    toDashboard();
                } else {
                    getWalletList(this.ds.getData().getUID(), this.ds.getData().getUsername(), this.ds.getData().getToken());
                }
            }
        }catch (Throwable t){
            Toast.makeText(this, "error7 :" +t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            this.ds.saveData(null, null, data.getString("token"), null, null);

            this.tv.setText("Load data ");
        } catch (Throwable t) {
            Toast.makeText(Load_Data.this, "error 8 : "+t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(Load_Data.this, "error 9 : "+error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable editable) {
        loadData();
    }
}