package com.my.wallet;

import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.maltaisn.calcdialog.CalcDialog;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.MyData;
import com.my.wallet.model.Wallet;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Activity_Wallet_Update extends AppCompatActivity implements View.OnClickListener, CalcDialog.CalcDialogCallback{
    private MyData md;

    private String walletId = null;
    private final String errorColor = "#F4463F";
    private final CharSequence[] list_wallet_type = {"Bank Account", "Cash", "E-Money"};
    private String selected_background_card = "background1";
    private String text_color = "#FFFFFFFF";
    private AlertDialog popUpDialog;

    private enum fields{
        WALLET_TYPE,
        WALLET_NAME,
        WALLET_NOMINAL,
        ALL
    }

    /**calculator**/
    private CalcDialog calcDialog;
    private BigDecimal value_nominal = null;

    /**component**/
    private MaterialToolbar _wallet_update_toolbar;

    private TextView _wallet_update_id;
    private MaterialCardView _wallet_update_type_box;
    private TextView _wallet_update_type;
    private MaterialCardView _wallet_update_name_box;
    private TextView _wallet_update_name;
    private MaterialCardView _wallet_update_nominal_box;
    private TextView _wallet_update_nominal;
    private ImageView _wallet_update_color_box;

    @Override
    public void onBackPressed() {
        //finish();
        //super.onBackPressed();
        back();
    }

    private void back(){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);

        finish();
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_update);

//        getWindow().setStatusBarColor(Color.WHITE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        
        initVariable();
        
        setComponentListener();
        
        setComponentText();


        //edit existing wallet
        walletId = getIntent().getStringExtra("walletId");
        if (walletId != null) {
            Wallet w = this.md.getListWallet().get(walletId);
            this._wallet_update_id.setText(w.getId());
            this._wallet_update_type.setText(w.getType());
            this._wallet_update_name.setText(w.getName());
            this._wallet_update_nominal.setText(BigDecimal.valueOf(w.getNominal()).toPlainString());
            this.selected_background_card = w.getBackgroundColor();

            this._wallet_update_color_box.setBackgroundResource(iconList.card_background().get(w.getBackgroundColor()).getValue());

            this.value_nominal = BigDecimal.valueOf(w.getNominal());

        }


    }

    private void initVariable() {
        this._wallet_update_toolbar = findViewById(R.id.wallet_update_toolbar);


        this._wallet_update_id = findViewById(R.id.wallet_update_id);

        this._wallet_update_type_box = findViewById(R.id.wallet_update_type_box);
        this._wallet_update_type = findViewById(R.id.wallet_update_type);

        this._wallet_update_name_box = findViewById(R.id.wallet_update_name_box);
        this._wallet_update_name = findViewById(R.id.wallet_update_name);

        this._wallet_update_nominal_box = findViewById(R.id.wallet_update_nominal_box);
        this._wallet_update_nominal = findViewById(R.id.wallet_update_nominal);

        this._wallet_update_color_box = findViewById(R.id.wallet_update_color_box);

        /**variable**/
        this.calcDialog = new CalcDialog();
    }

    private void setComponentListener() {
        this._wallet_update_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        this._wallet_update_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveWallet();
                return true;
            }
        });


        this._wallet_update_type_box.setOnClickListener(this);

        this._wallet_update_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validationsField(fields.WALLET_NAME);
            }
        });
        this._wallet_update_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {validationsField(fields.WALLET_NAME);}
        });


        this._wallet_update_nominal.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                calcDialog.getSettings().setInitialValue(value_nominal);
                calcDialog.show(getSupportFragmentManager(), "calc_dialog");
            }
        });
        this._wallet_update_nominal.setOnClickListener(v -> {
            calcDialog.getSettings().setInitialValue(value_nominal);
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        });
        this._wallet_update_nominal.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {validationsField(fields.WALLET_NOMINAL);}
        });


        this._wallet_update_color_box.setOnClickListener(this);
    }

    private void setComponentText() {
        this._wallet_update_id.setText(UUID.randomUUID().toString());
    }

    @NonNull
    private String validationsField(fields check){
        try {
            int allValid = 0;

            if (check == fields.ALL || check == fields.WALLET_TYPE) {
                if (this._wallet_update_type.getText().length() == 0) {
                    this._wallet_update_type_box.setCardBackgroundColor(Color.parseColor(this.errorColor));
                    allValid--;
                }else {
                    this._wallet_update_type_box.setCardBackgroundColor(null);
                    allValid++;
                }
            }

            if (check == fields.ALL || check == fields.WALLET_NAME) {
                if (this._wallet_update_name.getText().length() == 0) {
                    this._wallet_update_name_box.setStrokeColor(Color.parseColor(this.errorColor));
                    this._wallet_update_name.setError("Can't be empty");
                    allValid--;
                } else {
                    this._wallet_update_name_box.setStrokeColor(_wallet_update_type_box.getStrokeColorStateList());
                    this._wallet_update_name.setError(null);
                    allValid++;
                }
            }

            if (check == fields.ALL || check == fields.WALLET_NOMINAL) {
                if (this._wallet_update_nominal.getText().length() == 0) {
                    this._wallet_update_nominal_box.setStrokeColor(Color.parseColor(this.errorColor));
                    this._wallet_update_nominal.setError("Can't be empty");
                    allValid--;
                } else if (this._wallet_update_nominal.getText().length() > 0 && Double.parseDouble(this._wallet_update_nominal.getText().toString()) == 0.0) {
                    this._wallet_update_nominal_box.setStrokeColor(Color.parseColor(this.errorColor));
                    this._wallet_update_nominal.setError("Can't be 0 (zero)");
                    allValid--;
                } else {
                    this._wallet_update_nominal_box.setStrokeColor(_wallet_update_type_box.getStrokeColorStateList());
                    this._wallet_update_nominal.setError(null);
                    allValid++;
                }
            }

            if (allValid == 3) {
                return "valid";
            }else{
                return "invalid";
            }

        }catch (Throwable t){
            return "invalid";
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == this._wallet_update_type_box.getId()) {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Type")
                    .setItems(list_wallet_type, (dialog, which) -> {
                        //_textInput_type.setText(list_wallet_type[which]);
                        _wallet_update_type.setText(list_wallet_type[which]);
                        _wallet_update_type_box.setCardBackgroundColor(_wallet_update_name_box.getCardBackgroundColor());
                    })
                    ;
            materialAlertDialogBuilder.show();
        }else
        if (id == this._wallet_update_color_box.getId()) {
            show_background_card_list();
        }
    }

    private void saveToLocal(Wallet w) {
        this.md.getListWallet().put(w.getId(), w);

        dataStore.getInstance().saveData(null, null, null, null, this.md);

        Intent intent = new Intent();
        intent.putExtra("md", this.md);
        setResult(RESULT_OK, intent);

        finish();
        super.onBackPressed();
    }

    private void saveWallet(){
        popUpNotification.show(this, lov.popUpType.LOADING, "");
        if (validationsField(fields.ALL).equalsIgnoreCase("valid")) {
            try {
                Wallet w = new Wallet(
                        this._wallet_update_id.getText().toString(),
                        selected_background_card,
                        this._wallet_update_name.getText().toString(),
                        Double.parseDouble(this._wallet_update_nominal.getText().toString()),
                        this.text_color,
                        this._wallet_update_type.getText().toString());


                String url = api.url+api.path_walletUpdate;

                JSONObject requestMessage = new JSONObject(w.toString());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        url, requestMessage,
                        response -> {
                            try {
                                String status = response.getString("status");
                                if (status.equalsIgnoreCase("success")){
                                    saveToLocal(w);
                                }
                            }catch (Throwable t){}
                        },
                        error -> { }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("UID", md.getUID());
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
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            popUpNotification.close();
        }
    }

    private void show_background_card_list(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(R.layout.list_background, null);
        dialogBuilder.setView(layoutView);

        popUpDialog = dialogBuilder.create();
        popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpDialog.setCancelable(false);
        popUpDialog.setCanceledOnTouchOutside(false);

        MaterialButton close = layoutView.findViewById(R.id.card_background_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.dismiss();
            }
        });

        ImageButton[] imgButton = new ImageButton[12];
        imgButton[0] = layoutView.findViewById(R.id.wallet_background_card_1);
        imgButton[1] = layoutView.findViewById(R.id.wallet_background_card_2);
        imgButton[2] = layoutView.findViewById(R.id.wallet_background_card_3);
        imgButton[3] = layoutView.findViewById(R.id.wallet_background_card_4);
        imgButton[4] = layoutView.findViewById(R.id.wallet_background_card_5);
        imgButton[5] = layoutView.findViewById(R.id.wallet_background_card_6);
        imgButton[6] = layoutView.findViewById(R.id.wallet_background_card_7);
        imgButton[7] = layoutView.findViewById(R.id.wallet_background_card_8);
        imgButton[8] = layoutView.findViewById(R.id.wallet_background_card_9);
        imgButton[9] = layoutView.findViewById(R.id.wallet_background_card_10);
        imgButton[10] = layoutView.findViewById(R.id.wallet_background_card_11);
        imgButton[11] = layoutView.findViewById(R.id.wallet_background_card_12);



        List<String> keys = new ArrayList<>(iconList.card_background().keySet());

        for (int i = 0; i < imgButton.length; i++) {
            imgButton[i].setBackgroundResource(iconList.card_background().get(keys.get(i)).getValue());
        }
        


        imgButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(0)).getKey();
                selected_background_card = keys.get(0);
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(0)).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(1)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(1); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(1)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(2)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(2); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(2)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(3)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(3); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(3)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(4)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(4); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(4)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(5)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(5); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(5)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(6)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(6); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(6)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(7)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(7); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(7)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(8)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(8); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(8)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(9)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(9); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(9)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(10)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(10); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(10)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });
        imgButton[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_color = iconList.card_background().get(keys.get(11)).getKey(); //text_color = "#FFFFFFFF";
                selected_background_card = keys.get(11); //selected_background_card = iconList.card_background().get(0).getKey();
                _wallet_update_color_box.setBackgroundResource(iconList.card_background().get(keys.get(11)).getValue()); //_wallet_update_color_box.setBackgroundResource(iconList.card_background().get(0).getValue());
                popUpDialog.dismiss();
            }
        });

        this.popUpDialog.show();
    }

    @Override
    public void onValueEntered(int requestCode, @Nullable BigDecimal value) {
        this.value_nominal = value;
        this._wallet_update_nominal.setText(this.value_nominal.toPlainString());
    }
}