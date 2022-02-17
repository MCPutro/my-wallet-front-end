package com.my.wallet.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;

import com.my.wallet.popUpNotification;
import org.json.JSONObject;

import java.util.Objects;

public class Sign_Up extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    /*** component ***/
    private ImageButton _sign_up_back_btn;

    private EditText _sign_up_username;
    private EditText _sign_up_email;
    private EditText _sign_up_password;
    private EditText _sign_up_confirm_password;

    private MaterialButton _sign_up_btn_signup;

    /*** loading animation ***/
    //private AlertDialog popUpDialog;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.initComponent();
        this.componentListener();
    }

    private void initComponent() {
        this._sign_up_back_btn = findViewById(R.id.sign_up_back_btn);

        this._sign_up_username = findViewById(R.id.sign_up_username);
        this._sign_up_email = findViewById(R.id.sign_up_email);
        this._sign_up_password = findViewById(R.id.sign_up_password);
        this._sign_up_confirm_password = findViewById(R.id.sign_up_confirm_password);

        this._sign_up_btn_signup = findViewById(R.id.sign_up_btn_signup);


    }

    private void componentListener(){
        this._sign_up_back_btn.setOnClickListener(this);
        this._sign_up_btn_signup.setOnClickListener(this);
    }

    private String validate(){
        int i = 0;
        if (TextUtils.isEmpty(this._sign_up_email.getText())){
            this._sign_up_email.setError("Email is required!");
        }else{i++;}

        if (TextUtils.isEmpty(this._sign_up_username.getText())){
            this._sign_up_username.setError("Username is required!");
        }else{i++;}

        if (!TextUtils.isEmpty(this._sign_up_password.getText())) {
            if (!this._sign_up_password.getText().toString().equals(this._sign_up_confirm_password.getText().toString())) {
                this._sign_up_password.setError("Password doesn't match!");
                this._sign_up_confirm_password.setError("Password doesn't match!");
            } else {
                i++;
            }
        }else{
            this._sign_up_password.setError("Password is required!");
        }

        if (TextUtils.isEmpty(this._sign_up_confirm_password.getText())){
            this._sign_up_confirm_password.setError("Confirm password is required!");
        }else{i++;}


        if (i == 4) return "valid";
        else return "error";
    }

    private void createNewUser(String email, String pass, String username){
        try {
            int idc = 1123;
            String url = api.url+api.path_createAndLogin;
            JSONObject reqMessage = lov.signUpRequest(email, pass, username, idc+"");//new JSONObject("{\"email\": \""+email+"\",\"password\": \""+pass+"\",\"username\": \""+username+"\",\"deviceId\": \"" +idc+ "\" }");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, reqMessage, this, this);

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);
        }catch (Throwable t){
            popUpNotification.show(this, lov.popUpType.NEGATIF, t.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == this._sign_up_btn_signup.getId()) {
            //sign up
            if (Objects.equals(this.validate(), "valid")){
                popUpNotification.show(this, lov.popUpType.LOADING, null);
                this.createNewUser(
                        this._sign_up_email.getText().toString(),
                        this._sign_up_password.getText().toString(),
                        this._sign_up_username.getText().toString()
                );
            }
        }else if (id == this._sign_up_back_btn.getId()){
            onBackPressed();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            String message = response.getString("message");

            if (!status.equalsIgnoreCase("success")){
                popUpNotification.close();
                popUpNotification.show(this, lov.popUpType.NEGATIF, message);
            }else{
                JSONObject data = response.getJSONObject("data");

                dataStore.getInstance().saveData(
                        data.getString("uid"),
                        data.getString("username"),
                        data.getString("token"),
                        data.getString("refreshToken"),
                        null);

                dataStore.getInstance().setUserCredential(this._sign_up_email.getText().toString(), this._sign_up_password.getText().toString());

                popUpNotification.close();

                Intent i = new Intent(Sign_Up.this, Load_Data.class);
                i.putExtra("isNewUser", true);
                i.putExtra("urlAvatar", data.getString("urlAvatar"));
                startActivity(i);
                finish();
            }
        }catch (Throwable t){
            popUpNotification.show(this, lov.popUpType.NEGATIF, t.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        popUpNotification.close();
        popUpNotification.show(this, lov.popUpType.NEGATIF, error.getMessage());
    }
}