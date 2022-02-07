package com.my.wallet.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;

public class Splash_Screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        Window w = getWindow();


        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        api.getInstance(this);
        dataStore ds = dataStore.getInstance(this);


        if (ds.getData().getUID() == null && ds.getData().getRefreshToken() == null) {
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, Sign_In.class);
                startActivity(i);
                finish();
            },2000);
        }else{
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, Load_Data.class);
                //i.putExtra("isNewUser", false);
                startActivity(i);
                finish();

            },2000);
        }

    }
}