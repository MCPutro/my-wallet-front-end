package com.my.wallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Activity_Sign_In extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {


    /*** component ***/
    private TextView _version;
    private EditText _sign_in_email;
    private EditText _sign_in_password;
    private MaterialButton _sign_in_btn_signin;
    private MaterialButton _sign_in_2_sign_up;

//    /*** loading animation ***/
//    private AlertDialog popUpDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        this.getWindow().setStatusBarColor(Color.TRANSPARENT);

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //api.getInstance(this);

        this.initComponent();
        this.componentListener();
        _version.setText("v."+BuildConfig.VERSION_NAME);
    }

    private void initComponent() {
        _version = findViewById(R.id.app_version);
        this._sign_in_email = findViewById(R.id.sign_in_email);
        this._sign_in_password = findViewById(R.id.sign_in_password);
        this._sign_in_btn_signin = findViewById(R.id.sign_in_btn_signin);
        this._sign_in_2_sign_up = findViewById(R.id.sign_in_2_sign_up);


    }

    private void componentListener(){
        this._sign_in_2_sign_up.setOnClickListener(this);
        this._sign_in_btn_signin.setOnClickListener(this);
    }

    private String checkField(){
        int i = 0;
        if (this._sign_in_email.getText().toString().length() == 0) {
            this._sign_in_email.setError("Email can't be empty");
        }else{
            i++;
        }

        if (this._sign_in_password.getText().toString().length() == 0) {
            this._sign_in_password.setError("Password can't be empty");
        }else {
            i++;
        }

        if (i == 2) { return "valid";
        }else{ return "not_valid"; }

    }

    private void loginWithEmail(String email, String pass){
        try {
            String url = api.url+api.path_signIn;
            JSONObject requestMessage = new JSONObject("{\"email\": \""+email+"\",\"password\": \""+pass+"\"} ");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, requestMessage, this, this
            );

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
    public void onClick(View view) {
        int id = view.getId();
        if (id == this._sign_in_2_sign_up.getId()) {
            Intent i = new Intent(this, Activity_Sign_Up.class);
            startActivity(i);

        }
        else if (id == this._sign_in_btn_signin.getId()){
            if (this.checkField().equalsIgnoreCase("valid")){
                popUpNotification.show(this, lov.popUpType.LOADING, null);
                loginWithEmail(this._sign_in_email.getText().toString(), this._sign_in_password.getText().toString());
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        popUpNotification.close();
        try {

            String status = response.getString("status");
            String message = response.getString("message");

            if (!status.equalsIgnoreCase("success")){

                popUpNotification.show(this, lov.popUpType.NEGATIF, message);

            }else{
                JSONObject data = response.getJSONObject("data");
                dataStore.getInstance().saveData(
                        data.getString("uid"),
                        data.getString("username"),
                        data.getString("token"),
                        data.getString("refreshToken"),
                        null
                );

                dataStore.getInstance().setUserCredential(this._sign_in_email.getText().toString(), this._sign_in_password.getText().toString());

                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("urlAvatar", data.getString("urlAvatar"));
                startActivity(i);
                finish();
            }


        } catch (JSONException e) {
            popUpNotification.show(this, lov.popUpType.NEGATIF, e.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        popUpNotification.close();
        popUpNotification.show(this, lov.popUpType.NEGATIF, error.getMessage());
    }
}