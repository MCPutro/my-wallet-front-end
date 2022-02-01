package com.my.wallet;

import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;
import com.my.wallet.model.MyData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Activity_Account_Setting extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    private MyData md;
    private String regexEmail;
    private AlertDialog popUpDialog;

    /**temp variable**/
    private String tmpemail, tmppass;
    private boolean isNeedReSignIn = false;

    private CardView _user_setting_back;
    private TextView _user_setting_save;
    private ImageView _user_setting_photo;
    private TextView _user_setting_username;
    private TextView _user_setting_uid;
    private EditText _user_setting_username_et;
    private EditText _user_setting_email_et;
    private EditText _user_setting_password_et;
    private ImageView _user_setting_password_show;
    private EditText _user_setting_n_view_et;
    private CardView _user_setting_signout;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        finish();
        Intent i = new Intent(this, Activity_Dashboard.class);
        i.putExtra("md", md);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        this.initialVariable();

        this.setListenerComponent();


        this.setTextComponent();

        //Toast.makeText(this, this.md.getUrlAvatar(), Toast.LENGTH_SHORT).show();

        dataStore.getInstance().saveData(null, null, null, null, this.md);

    }

    private void initialVariable() {
        this._user_setting_back = findViewById(R.id.user_setting_back );
        this._user_setting_save = findViewById(R.id.user_setting_save);
        this._user_setting_photo = findViewById(R.id.user_setting_photo );
        this._user_setting_username = findViewById(R.id.user_setting_username);
        this._user_setting_uid = findViewById(R.id.user_setting_uid );
        this._user_setting_username_et = findViewById(R.id.user_setting_username_et );
        this._user_setting_email_et = findViewById(R.id.user_setting_email_et );
        this._user_setting_password_et = findViewById(R.id.user_setting_password_et );
        this._user_setting_password_show = findViewById(R.id.user_setting_password_show );
        this._user_setting_n_view_et = findViewById(R.id.user_setting_n_view_et );
        this._user_setting_signout = findViewById(R.id.user_setting_signout);


        this.regexEmail = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z0-9]{2,6}$";


    }

    private void setListenerComponent() {
        this._user_setting_password_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_user_setting_password_et.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    _user_setting_password_show.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    //Show Password
                    _user_setting_password_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    _user_setting_password_show.setImageResource(R.drawable.ic_baseline_visibility_on_24);
                    //Hide Password
                    _user_setting_password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        this._user_setting_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        this._user_setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this._user_setting_save.setOnClickListener(this);
    }

    private void setTextComponent() {
        Picasso.with(this).load(this.md.getUrlAvatar())
                .transform(new CircleTransform())
                .into(this._user_setting_photo);

        this._user_setting_username.setText(this.md.getUsername());

        this._user_setting_uid.setText(this.md.getUID());

        this._user_setting_username_et.setText(this.md.getUsername());


        Pattern email_pattern = Pattern.compile(regexEmail, Pattern.CASE_INSENSITIVE);

        for (String s : dataStore.getInstance().getUserCredential()) {
            if (email_pattern.matcher(s).find()) {
                this._user_setting_email_et.setText(s);
                this.tmpemail = s;
            }
            else {
                this._user_setting_password_et.setText(s);
                this.tmppass = s;
            }
        }

        this._user_setting_n_view_et.setText(String.valueOf(this.md.getN_show_list()));
    }

    private void signOut(){
        dataStore.getInstance().clear();
        finish();
        Intent i = new Intent(this, Activity_Sign_In.class);
        startActivity(i);
    }


    private void showPopUpNotif(lov.popUpType type, String text){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog_NoActionBar);
        boolean isCancelByOut = true;

        View layoutView = null;
        TextView notifText;
        ImageView notifIcon;
        TextView statNotif;
        MaterialButton popup_notif_text_btn;

        switch (type){
            case LOADING:
                layoutView = getLayoutInflater().inflate(R.layout.popup_loading, null);
                isCancelByOut = false;
                break;
            case NEGATIF:
                layoutView = getLayoutInflater().inflate(R.layout.popup_notification, null);

                notifText = layoutView.findViewById(R.id.popup_notif_text);
                notifIcon = layoutView.findViewById(R.id.popup_notif_icon);
                statNotif = layoutView.findViewById(R.id.popup_notif_status);
                popup_notif_text_btn = layoutView.findViewById(R.id.popup_notif_text_btn);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popup_notif_text_btn.setBackgroundTintList(getColorStateList(R.color.error));
                }else {
                    popup_notif_text_btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.error));
                }
                popup_notif_text_btn.setOnClickListener(v -> closePopUpNotif());

                notifText.setText(text);

                statNotif.setText("Oh No...");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statNotif.setTextColor(getColor(R.color.error));
                }else{
                    statNotif.setTextColor(ContextCompat.getColor(this, R.color.error));
                }

                notifIcon.setImageResource(R.drawable.ic_cancel_circle_24_red);

                break;
            case POSITIF:
                layoutView = getLayoutInflater().inflate(R.layout.popup_notification, null);

                notifText = layoutView.findViewById(R.id.popup_notif_text);
                notifIcon = layoutView.findViewById(R.id.popup_notif_icon);
                popup_notif_text_btn = layoutView.findViewById(R.id.popup_notif_text_btn);

                popup_notif_text_btn.setOnClickListener(v -> {
                    closePopUpNotif();
                    if (isNeedReSignIn) signOut();
                });

                notifText.setText(text);

                notifIcon.setImageResource(R.drawable.ic_check_circle_24_green);

                break;

        }
        dialogBuilder.setView(layoutView);
        dialogBuilder.setCancelable(isCancelByOut);
        popUpDialog = dialogBuilder.create();
        popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpDialog.setCanceledOnTouchOutside(isCancelByOut);
        popUpDialog.setCancelable(isCancelByOut);

        popUpDialog.show();
    }

    private void closePopUpNotif(){
        this.popUpDialog.cancel();
        this.popUpDialog.dismiss();
    }

    public void save(String username, String email, String password){
        try {
            showPopUpNotif(lov.popUpType.LOADING, null);

            JSONObject message = new JSONObject("{" +
                    "\"email\" : \""    +email                  +"\"," +
                    "\"password\" : \"" +password               +"\"," +
                    "\"uid\" : \""      +this.md.getUID()       +"\"," +
                    "\"username\": \""  +username               +"\"," +
                    "\"urlAvatar\": \"" +this.md.getUrlAvatar() +"\"" +
                    "}");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, api.url+api.path_userUpdate,
                    message, this, this
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer "+dataStore.getInstance().getData().getToken());
                    return  params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);
        }catch (Throwable t){
            Toast.makeText(this, "1 error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this._user_setting_save.getId()) {
            this.save(
                    this._user_setting_username_et.getText().toString(),
                    this._user_setting_email_et.getText().toString(),
                    this._user_setting_password_et.getText().toString()
                    );
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            closePopUpNotif();
            String status = response.getString("status");
            if (status.equalsIgnoreCase("success")) {
                this.md.setUsername(this._user_setting_username_et.getText().toString());
                this.md.setN_show_list(Integer.parseInt(this._user_setting_n_view_et.getText().toString()));

                //Pattern email_pattern = Pattern.compile(regexEmail, Pattern.CASE_INSENSITIVE);

                dataStore.getInstance().setUserCredential(this._user_setting_email_et.getText().toString(), this._user_setting_password_et.getText().toString());

                dataStore.getInstance().saveData(null, null, null, null, md);

                if (!this.tmpemail.equals(this._user_setting_email_et.getText().toString()) ||
                        !this.tmppass.equals(this._user_setting_password_et.getText().toString()))  {
                    this.isNeedReSignIn = true;
                    showPopUpNotif(lov.popUpType.POSITIF, "Data saved. Please re-login!");
                }else
                    showPopUpNotif(lov.popUpType.POSITIF, "Data saved");

                setTextComponent();

                //finish();
                //Intent i = new Intent(this, Activity_Dashboard.class);
                //i.putExtra("md", md);
                //startActivity(i);
            }else{
                String msg = response.getString("message");
                showPopUpNotif(lov.popUpType.NEGATIF, msg);
            }
        }catch (Throwable r){
            Toast.makeText(this, "2 error : "+r.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        closePopUpNotif();
        showPopUpNotif(lov.popUpType.NEGATIF, error.getMessage());
    }

    class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}