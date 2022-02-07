package com.my.wallet.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.maltaisn.calcdialog.CalcDialog;
import com.my.wallet.adapter.Adapter_List_Category;
import com.my.wallet.R;
import com.my.wallet.env.api;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;
import com.my.wallet.model.Wallet;
import com.my.wallet.popUpNotification;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

public class Activity_New extends AppCompatActivity implements CalcDialog.CalcDialogCallback{
    private MyData md;
    private String inColor = "#4CAF50";
    private String exColor = "#E91E63";
    private String tempDate;
    private int selWallet = -1;
    private String category;

    private ArrayList<String> keys;

    private lov.trf trf;

    /**activity type**/
    private lov.activityType type;
    /**calculator**/
    private CalcDialog calcDialog;
    private BigDecimal value_nominal = null;

    // loading animation
    // private AlertDialog popUpDialog;

    /**declare component**/
    private MaterialToolbar _create_history_toolBar;
    private TabLayout _create_new_history_tab;
    /**nominal**/
    private TextInputLayout _create_history_nominal_layout;
    private TextInputEditText _create_history_nominal;
    private ImageButton _create_history_nominal_calc;
    /**category**/
    private TextInputLayout _create_history_category_layout;
    private AutoCompleteTextView _create_history_category;
    /**wallet**/
    private TextInputLayout _create_history_wallet_layout;
    private AutoCompleteTextView _create_history_wallet;
    /**date**/
    private TextInputLayout _create_history_date_layout;
    private TextInputEditText _create_history_date;
    /**desc**/
    //private TextInputLayout _create_history_desc_layout;
    private TextInputEditText _create_history_desc;

    /**layout type**/
    private ScrollView _activity_new_layout_inex;
    private LinearLayoutCompat _activity_new_layout_trf;

    /**transfer layout**/
    private MaterialCardView _trf_from;
    private MaterialCardView _trf_to;
    private ImageView _trf_from_icon;
    private TextView _trf_from_name, _trf_from_id, _trf_from_nominal;
    private ImageView _trf_to_icon;
    private TextView _trf_to_name, _trf_to_id, _trf_to_nominal;
    private AppCompatEditText _trf_nominal, _trf_nominal_fee;

    public void setCategory(String category, String subCategory) {
        this.category = category;
        this._create_history_category.setText(subCategory);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

//        getWindow().setStatusBarColor(Color.WHITE);
//        Window window = getWindow();
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //
        initVariable();

        setAdapterActivityType();

        setAdapterWalletList();

        setListenerComponent();

    }

    private void initVariable() {
        this._create_history_toolBar = findViewById(R.id.create_history_toolbar);
        this._create_new_history_tab = findViewById(R.id.create_new_history_tab);
        this._create_history_nominal_layout = findViewById(R.id.create_history_nominal_layout);
        this._create_history_nominal = findViewById(R.id.create_history_nominal);
        this._create_history_nominal_calc = findViewById(R.id.create_history_nominal_calc);
        this._create_history_category_layout = findViewById(R.id.create_history_category_layout);
        this._create_history_category = findViewById(R.id.create_history_category);
        this._create_history_wallet_layout = findViewById(R.id.create_history_wallet_layout);
        this._create_history_wallet = findViewById(R.id.create_history_wallet);
        this._create_history_date_layout = findViewById(R.id.create_history_date_layout);
        this._create_history_date = findViewById(R.id.create_history_date);
        //this._create_history_desc_layout = findViewById(R.id.create_history_desc_layout);
        this._create_history_desc = findViewById(R.id.create_history_desc);
        this._activity_new_layout_inex = findViewById(R.id.activity_new_layout_inex);
        this._activity_new_layout_trf = findViewById(R.id.activity_new_layout_trf);
        /**transfer layout**/
        this._trf_from = findViewById(R.id.trf_from);
        this._trf_to = findViewById(R.id.trf_to);
        this._trf_from_icon = findViewById(R.id.trf_from_icon);
        this._trf_from_name = findViewById(R.id.trf_from_name);
        this._trf_from_id = findViewById(R.id.trf_from_id);
        this._trf_from_nominal = findViewById(R.id.trf_from_nominal);
        this._trf_to_icon = findViewById(R.id.trf_to_icon);
        this._trf_to_name = findViewById(R.id.trf_to_name);
        this._trf_to_id = findViewById(R.id.trf_to_id);
        this._trf_to_nominal = findViewById(R.id.trf_to_nominal);
        this._trf_nominal = findViewById(R.id.trf_nominal);
        this._trf_nominal_fee = findViewById(R.id.trf_nominal_fee);

        /**variable**/
        this.calcDialog = new CalcDialog();
        this.type = lov.activityType.INCOME;
        this.keys = new ArrayList<>(this.md.getListWallet().keySet());
        //this.popUpDialog = new Dialog(this);
    }

    private BottomSheetDialog bottomSheetDialog;
    private void setListenerComponent() {

        this._create_history_toolBar.setOnMenuItemClickListener(item -> {
            try {
                if (type != lov.activityType.TRANSFER){
                    if (cekNull("all").equalsIgnoreCase("VALID")){
                        saveActivity(
                                Activities.builder()
                                        .id(UUID.randomUUID().toString())
                                        .walletId(md.getListWallet().get(keys.get(selWallet)).getId())
                                        .walletName(_create_history_wallet.getText().toString())
                                        .titleActivities(_create_history_category.getText().toString().trim())
                                        .category(this.category)
                                        .nominalActivities(Double.parseDouble(_create_history_nominal.getText().toString()))
                                        .descActivities(_create_history_desc.getText().toString())
                                        .dateActivities(lov.dateFormatter2.parse(_create_history_date.getText().toString()))
                                        .income((type == lov.activityType.INCOME))
                                        .build(),
                                dataStore.getInstance().getData().getToken());
                    }
                    return true;
                }
                else
                {
                    String s = transferValidation();
                    if (s == null) {
                        transferBalance();
                    }else{
                        popUpNotification.show(this, lov.popUpType.NEGATIF, s);
                    }
                    return true;
                }
            }catch (Throwable t){}
            return false;
        });

        this._create_history_toolBar.setNavigationOnClickListener(v -> onBackPressed());

        this._create_new_history_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equalsIgnoreCase("income")) {
                    type = lov.activityType.INCOME;
                    setAdapterActivityType();

                    _create_history_toolBar.setTitle("New activity");

                    _create_history_nominal.setTextColor(Color.parseColor(inColor));

                    _activity_new_layout_inex.setVisibility(View.VISIBLE);
                    _activity_new_layout_trf.setVisibility(View.GONE);
                }else
                if (tab.getText().toString().equalsIgnoreCase("expense")) {
                    type = lov.activityType.EXPENSE;
                    setAdapterActivityType();

                    _create_history_toolBar.setTitle("New activity");

                    _create_history_nominal.setTextColor(Color.parseColor(exColor));

                    _activity_new_layout_inex.setVisibility(View.VISIBLE);
                    _activity_new_layout_trf.setVisibility(View.GONE);
                }else
                if (tab.getText().toString().equalsIgnoreCase("transfer")) {
                    type = lov.activityType.TRANSFER;

                    _create_history_toolBar.setTitle("Transfer between own wallet");

                    _activity_new_layout_inex.setVisibility(View.GONE);
                    _activity_new_layout_trf.setVisibility(View.VISIBLE);
                }
            }
        });

        this._create_history_nominal_calc.setOnClickListener(v -> showCalculator());
        this._create_history_nominal.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showCalculator();
        });

        _create_history_category.setOnClickListener(v -> {
            if (type == lov.activityType.EXPENSE) {
                showBottomSheetDialog("expense-category");
            }
        });

        this._create_history_wallet.setOnItemClickListener((parent, view, position, id) -> selWallet = position);
        this._create_history_wallet.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                cekNull("wallet");
            }
        });

        this._create_history_date.setOnClickListener(v -> showDateDialog());
        this._create_history_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDateDialog();
        });
        this._create_history_date.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                cekNull("date");
            }
        });

        /**trf**/
        _trf_from.setOnClickListener(view -> {
            trf = lov.trf.SOURCE;
            showBottomSheetDialog("wallet");
        });
        _trf_to.setOnClickListener(v -> {
            trf = lov.trf.DESTINATION;
            showBottomSheetDialog("wallet");
        });
    }

    private void showBottomSheetDialog(String type){
        bottomSheetDialog = new BottomSheetDialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View v = inflater.inflate(R.layout.bottom_sheet_recycle_list, findViewById(R.id.bottomSheetWalletListLayout), false);
        bottomSheetDialog.setContentView(v);
        RecyclerView bottomRecyclerView = v.findViewById(R.id.bottomSheetListView);
        TextView title = v.findViewById(R.id.bottomSheetTitle);

        //set tinggi layout
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight =  getWindowManager().getDefaultDisplay().getHeight();
        if (layoutParams != null) {
            layoutParams.height = (int) (windowHeight * 0.85);
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //

        if (type.equalsIgnoreCase("wallet")) {
            //set list view
            title.setText("My Wallet");
            walletListAdapter wla = new walletListAdapter(this);
            bottomRecyclerView.setAdapter(wla);
        }
        else
        if (type.equalsIgnoreCase("expense-category")){
            title.setText("Category");
            Adapter_List_Category ALC = new Adapter_List_Category(this, iconList.expense_category());
            bottomRecyclerView.setAdapter(ALC);
        }
        bottomSheetDialog.show();
    }

    private String transferValidation(){
        String status = "";
        int i = 0;


        if (this._trf_from_id.getText().equals(this._trf_to_id.getText()) && this._trf_from_id.getText().length() > 0 && this._trf_to_id.getText().length() > 0) {
            status += "source and destination can't be the same, ";
        }
        else i++;

        if (this._trf_from_id.getText().length() == 0) {
            status += "source wallet not selected, ";
        }else i++;

        if (this._trf_to_id.getText().length() == 0) {
            status += "destination wallet not selected, ";
        }else i++;

        if (this._trf_nominal.getText().length() == 0 || Double.parseDouble(this._trf_nominal.getText().toString()) == 0.0) {
            status += "amount can't be empty/zero";
        } else i++;


        if (i == 4) {
            return null;
        }
        else{
            return status;
        }
    }


    private void transferBalance(){
        try {
            popUpNotification.show(this, lov.popUpType.LOADING, null);

            String fee = ((_trf_nominal_fee.getText().toString().length() == 0) ? "0" : _trf_nominal_fee.getText().toString());
            JSONObject message = new JSONObject("{" +
                    "\"id\" : \""                   + UUID.randomUUID().toString()          + "\"," +
                    "\"walletIdSource\" : \""       + _trf_from_id.getText().toString()     + "\"," +
                    "\"walletIdDestination\" : \""  + _trf_to_id.getText().toString()       + "\"," +
                    "\"nominal\" : "                + _trf_nominal.getText().toString()     + "," +
                    "\"fee\" : "                    + fee +
                    "}");

            String url = api.url + api.path_walletTransfer;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, message,
                    response -> {
                        popUpNotification.close();
                        try {
                            //Toast.makeText(Activity_New.this, "00 transferBalance : "+response.toString(), Toast.LENGTH_LONG).show();
                            String status = response.getString("status");
                            if ("success".equalsIgnoreCase(status)){
                                JSONObject data = response.getJSONObject("data");
                                JSONObject newWalletData = data.getJSONObject("newWalletData");
                                JSONObject newActivity = data.getJSONObject("newActivity");

                                md.getListWallet().get(newWalletData.getString("walletIdSource")).setNominal(newWalletData.getDouble("nominalSource"));
                                md.getListWallet().get(newWalletData.getString("walletIdDestination")).setNominal(newWalletData.getDouble("nominalDestination"));

                                Activities a = Activities.builder()
                                        .id(newActivity.getString("id"))
                                        .walletId(newActivity.getString("walletId"))
                                        .walletName(newActivity.getString("walletName"))
                                        .titleActivities(newActivity.getString("title"))
                                        .category(newActivity.getString("category"))
                                        .descActivities(newActivity.getString("desc"))
                                        .nominalActivities(newActivity.getDouble("nominal"))
                                        .dateActivities(lov.dateFormatter4.parse(newActivity.getString("date")))
                                        .income(newActivity.getBoolean("income"))
                                        .build();

                                md.getListActivities().put(a.getId(), a);

                                md.setListActivities(sorting(md.getListActivities()));

                                dataStore.getInstance().saveData(null, null, null, null, md);

                                Intent intent = new Intent();
                                intent.putExtra("md", md);
                                setResult(RESULT_OK, intent);
                                onBackPressed();

                            }else{
                                Toast.makeText(Activity_New.this, "1 transferBalance : "+response.toString(), Toast.LENGTH_LONG).show();
                            }
                        }catch (Throwable t){
                            Toast.makeText(Activity_New.this, "2 transferBalance : "+t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        popUpNotification.close();
                        Toast.makeText(Activity_New.this, "3. transferBalance : "+error.toString(), Toast.LENGTH_LONG).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("UID", md.getUID());
                    params.put("Authorization", "Bearer "+dataStore.getInstance().getData().getToken());
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);
        }catch (Throwable t){
            popUpNotification.close();
            Toast.makeText(Activity_New.this, "0 transferBalance : "+t.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void updateTransferData(lov.trf t, String id){
        if (t == lov.trf.SOURCE) {
            this._trf_from_icon.setImageResource(iconList.wallet_type_color().get(md.getListWallet().get(id).getType()));
            this._trf_from_name.setText(md.getListWallet().get(id).getName());
            this._trf_from_id.setText(md.getListWallet().get(id).getId());
            this._trf_from_nominal.setText(getString(R.string.currency)+" "+lov.currencyFormat.format(md.getListWallet().get(id).getNominal()));
        }
        else
        if (t == lov.trf.DESTINATION) {
            this._trf_to_icon.setImageResource(iconList.wallet_type_color().get(md.getListWallet().get(id).getType()));
            this._trf_to_name.setText(md.getListWallet().get(id).getName());
            this._trf_to_id.setText(md.getListWallet().get(id).getId());
            this._trf_to_nominal.setText(getString(R.string.currency)+" "+lov.currencyFormat.format(md.getListWallet().get(id).getNominal()));
        }
        closeBottomSheet();
    }

    public void closeBottomSheet (){
        this.bottomSheetDialog.cancel();
        this.bottomSheetDialog.dismiss();
    }

    private void setAdapterActivityType() {
        ArrayAdapter<String> adapter = null;
        ArrayList<String> list;
        this._create_history_category.setText(null);
        switch (this.type){
            case INCOME:
                list = new ArrayList<>(iconList.income_list_map().keySet());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
                this._create_history_nominal.setTextColor(Color.parseColor(this.inColor));
                break;
            case EXPENSE:
                //list = new ArrayList<>(iconList.expense_list_map().keySet());
                //adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
                //this._create_history_nominal.setTextColor(Color.parseColor(this.exColor));
                break;
            default:
                break;
        }
        this._create_history_category.setAdapter(adapter);


    }

    private void setAdapterWalletList(){
        ArrayList<String> walletList = new ArrayList<>();
        for (Map.Entry<String, Wallet> w : this.md.getListWallet().entrySet()) {
            walletList.add(w.getValue().getName());
        }

        ArrayAdapter<String> adapterWalletList = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, walletList);
        this._create_history_wallet.setAdapter(adapterWalletList);

    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);

                tempDate = lov.dateFormatter1.format(newDate.getTime());//_new_activities_date.setText(dateFormatter.format(newDate.getTime()));


                showTimeDialog();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimeDialog(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String menit = "";
                String jam = "";
                if (selectedMinute < 10) { menit = "0"+selectedMinute; }else{ menit = ""+selectedMinute; }
                if (selectedHour < 10) {jam = "0"+selectedHour;}else{jam = ""+selectedHour;}

                //tempDate2 += "T"+jam + ":" + menit+":00"+ TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT_GMT);

                _create_history_date.setText(tempDate + " " + jam + ":" + menit);
                //_new_activities_date_layout.setStartIconDrawable(R.drawable.ic_baseline_date_range_24_black);
            }
        }, hour, minute, true);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void showCalculator(){
        _create_history_nominal_layout.setError(null);
        calcDialog.getSettings().setInitialValue(value_nominal);
        calcDialog.show(getSupportFragmentManager(), "calc_dialog");
    }

    @Override
    public void onValueEntered(int requestCode, @Nullable BigDecimal value) {
        this.value_nominal = value;
        this._create_history_nominal.setText(this.value_nominal.toPlainString());
    }

    @NonNull
    private String cekNull(@NonNull String checkOn) {
        int c = 0;
        if (checkOn.equalsIgnoreCase("nominal") || checkOn.equalsIgnoreCase("all")) {
            if (this._create_history_nominal.getText().length() <= 0 || this._create_history_nominal.getText().toString().equals("0")) {
                this._create_history_nominal_layout.setError("Nominal field can't be empty or zero"); c += -1;
            }
            else {
                this._create_history_nominal_layout.setError(null); c += 1;
            }
        }

        if (checkOn.equalsIgnoreCase("category") || checkOn.equalsIgnoreCase("all")) {
            if (this._create_history_category.getText().length() <= 0 ) {
                this._create_history_category_layout.setError("Please select the category"); c += -1;
            }
            else {
                this._create_history_category_layout.setError(null); c += 1;
            }
        }

        if (checkOn.equalsIgnoreCase("wallet") || checkOn.equalsIgnoreCase("all")) {
            if (this._create_history_wallet.getText().length() <= 0 ) {
                this._create_history_wallet_layout.setError("Please select the wallet"); c += -1;
            }
            else {
                this._create_history_wallet_layout.setError(null); c += 1;
            }
        }

        if (checkOn.equalsIgnoreCase("date") || checkOn.equalsIgnoreCase("all")) {
            if (this._create_history_date.getText().length() <= 0) {
                this._create_history_date_layout.setError("Date field can't be empty"); c += -1;
            } else {
                this._create_history_date_layout.setError(null); c += 1;
            }
        }

        if (c == 4) {return "valid";}
        else return "invalid";
    }

    private void saveActivity(Activities newActivity, String token){
        try {
            popUpNotification.show(this, lov.popUpType.LOADING, null);

            //Toast.makeText(Activity_New.this, newActivity.toString(), Toast.LENGTH_LONG).show();
            JSONObject requestMessage = new JSONObject(newActivity.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    api.url + api.path_activity,
                    requestMessage,
                    response -> {
                        //Toast.makeText(Activity_New.this, "o saveActivity : "+response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            popUpNotification.close();
                            //Toast.makeText(Activity_New.this, response.toString(), Toast.LENGTH_LONG).show();
                            String status = response.getString("status");
                            if ("success".equalsIgnoreCase( status)) {
                                JSONObject data = response.getJSONObject("data");
                                md.getListActivities().put(newActivity.getId(), newActivity);

                                md.setListActivities(sorting(md.getListActivities()));

                                md.getListWallet().get(data.getString("walletId")).setNominal(data.getDouble("remainingBalance"));

                                dataStore.getInstance().saveData(null, null, null, null, md);

                                Intent intent = new Intent();
                                intent.putExtra("md", md);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            }else{
                                String msg = response.getString("message");
                                popUpNotification.show(this, lov.popUpType.NEGATIF, msg);
                            }
                        }catch (Throwable t){
                            Toast.makeText(Activity_New.this, "1 saveActivity : "+t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(Activity_New.this, "2 saveActivity : "+error.getMessage(), Toast.LENGTH_LONG).show()
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("UID", md.getUID());
                    params.put("Authorization", "Bearer "+token);
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    api.timeOut_ms,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            api.getInstance().getQueue().add(request);

        }catch (Throwable t){
            Toast.makeText(this, "3 saveActivity : "+t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private LinkedHashMap<String, Activities> sorting(LinkedHashMap<String, Activities> list_map){
        LinkedHashMap<String, Activities> temp = new LinkedHashMap<>();

        // 2. convert LinkedHashMap to List of Map.Entry
        List<Map.Entry<String, Activities>> companyFounderListEntry =
                new ArrayList<Map.Entry<String, Activities>>(list_map.entrySet());

        // 3. sort list of entries using Collections class'
        Collections.sort(companyFounderListEntry,
                new Comparator<Map.Entry<String, Activities>>() {
                    @Override
                    public int compare(Map.Entry<String, Activities> es1,
                                       Map.Entry<String, Activities> es2) {
                        return es2.getValue().getDateActivities().compareTo(es1.getValue().getDateActivities());
                    }
                });

        // 4. clear original LinkedHashMap md.getListActivities().clear();

        // 5. iterating list and storing in LinkedHahsMap
        for(Map.Entry<String, Activities> map : companyFounderListEntry){
            temp.put(map.getKey(), map.getValue());
        }

        return temp;
    }

    class walletListAdapter extends RecyclerView.Adapter<walletListAdapter.walletHolder>{
        private final Context context;
        private final LayoutInflater layoutInflater;
        private final List<String> keys = new ArrayList<>(md.getListWallet().keySet());

        public walletListAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
        }

        @NonNull
        @Override
        public walletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new walletHolder(this.layoutInflater.inflate(R.layout.item_wallet_trf, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull walletHolder holder, int position) {
            holder.name.setText(md.getListWallet().get(this.keys.get(position)).getName());

            holder.id.setText(md.getListWallet().get(this.keys.get(position)).getId());

            holder.nominal.setText(getString(R.string.currency)+" "+lov.currencyFormat.format(md.getListWallet().get(this.keys.get(position)).getNominal()));

            holder.logo.setImageResource(iconList.wallet_type_color().get(md.getListWallet().get(this.keys.get(position)).getType()));

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(Activity_New.this, "dari : "+trf.name(), Toast.LENGTH_SHORT).show();
                    updateTransferData(trf, keys.get(holder.getAdapterPosition()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return md.getListWallet().size();
        }

        public class walletHolder extends RecyclerView.ViewHolder {
            MaterialCardView layout;
            ImageView logo;
            TextView name, nominal, id;

            public walletHolder(@NonNull View itemView) {
                super(itemView);
                this.layout = itemView.findViewById(R.id.item_wallet_trf_layout);
                this.logo = itemView.findViewById(R.id.item_wallet_trf_icon);
                this.name = itemView.findViewById(R.id.item_wallet_trf_name);
                this.id = itemView.findViewById(R.id.item_wallet_trf_id);
                this.nominal = itemView.findViewById(R.id.item_wallet_trf_nominal);

            }
        }
    }

}