package com.my.wallet;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;

public class splash_screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_activity);

        Window w = getWindow();


        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        api.getInstance(this);
        dataStore ds = dataStore.getInstance(this);


        if (ds.getData().getUID() == null && ds.getData().getRefreshToken() == null) {
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, Activity_Sign_In.class);
                startActivity(i);
                finish();
            },2000);
        }else{
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, MainActivity.class);
                //i.putExtra("isNewUser", false);
                startActivity(i);
                finish();

            },2000);
        }

    }
}