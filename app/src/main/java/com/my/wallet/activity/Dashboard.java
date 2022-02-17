package com.my.wallet.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.my.wallet.adapter.Adapter_List_Activities;
import com.my.wallet.R;
import com.my.wallet.env.dataStore;
import com.my.wallet.env.lov;
import com.my.wallet.menuNavi.DrawerAdapter;
import com.my.wallet.menuNavi.DrawerItem;
import com.my.wallet.menuNavi.SimpleItem;
import com.my.wallet.menuNavi.SpaceItem;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;
import com.my.wallet.model.Wallet;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class Dashboard extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    private MyData md;
    private NumberFormat currencyFormat;
    private boolean doubleBackToExitPressedOnce = false;
    private Date startDate, endDate;

    /**avticity code**/
    private final static int NAV_NEW_ACTIVITIES_CODE = 1001;
    private final static int NAV_WALLET_LIST_CODE = 1002;
    private final static int NAV_WALLET_UPDATE_CODE = 10021;
    private final static int NAV_ACTIVITIES_LIST_SHOW_MORE_CODE = 1003;
    //private final static int MY_REQUEST_CODE = 1;
    //private final static int NOTE_REQUEST_CODE = 1003;

    /**navigation**/
    private static final int NAV_DASHBOARD = 0;
    private static final int NAV_EXPENSE_CHART = 1;
    private static final int NAV_WALLET_LIST = 2;
    private static final int NAV_MY_NOTES = 3;
    private static final int NAV_SETTING = 4;
    private static final int NAV_SIGN_OUT = 6;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav;
    private DrawerAdapter drawerAdapter;
    private ImageView _view_more_list;

    /**component**/
    private MaterialToolbar _main_toolbar;
    private TextView _current_balance;
    private FloatingActionButton _dashboard_new_activities;
    private RecyclerView _dashboard_Recent_Activity_list;
    private TextView _dashboard_income;
    private TextView _dashboard_expense;
    private LinearLayout _dashboard_no_Recent_Activity_list;


    public void setMd(MyData md) {
        this.md = md;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        //if (fragment_active == NAV_DASHBOARD) {
            if (doubleBackToExitPressedOnce) {
                this.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                //android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        //}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.md = (MyData) getIntent().getSerializableExtra("md");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        StatusBarUtil.setTransparent(this);

        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
//
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //initial component
        this.initVariable();

        //call navigation menu
        this.initNavigationMenu(savedInstanceState);

        this.setDateRange();

        //show recent activity
        this.showRecentActivity();

        //set lisneter
        this.setListenerComponent();

        //set text on component
        //this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());


    }

    private void initVariable() {
        //**variable**//
        this.currencyFormat = lov.currencyFormat;
        //remove symbol currency
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat)this.currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        //***component***//
        this._main_toolbar = findViewById(R.id.main_toolbar);
        this._dashboard_new_activities = findViewById(R.id.dashboard_new_activities);
        this._dashboard_Recent_Activity_list = findViewById(R.id.dashboard_Recent_Activity_list);
        this._dashboard_income = findViewById(R.id.dashboard_income);
        this._dashboard_expense = findViewById(R.id.dashboard_expense);
        this._dashboard_no_Recent_Activity_list = findViewById(R.id.dashboard_no_Recent_Activity_list);
        this._view_more_list = findViewById(R.id.view_more_list);
    }

    private void setListenerComponent(){
        this._dashboard_new_activities.setOnClickListener(v -> {
            //finish();
            if (md.getListWallet().size() > 0) {
                Intent i = new Intent(Dashboard.this, Activity_New.class);
                i.putExtra("md", md);
                startActivityForResult(i, NAV_NEW_ACTIVITIES_CODE);
            }else{
                confirm();
            }
        });

        this._view_more_list.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, this._dashboard_expense);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_more, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                showMoreActivity();
                return true;
            });
            popup.show();
        });
    }

    public void showNotifNoData(boolean stat){
        if (stat){//its no data
            this._dashboard_Recent_Activity_list.setVisibility(View.GONE);
            this._dashboard_no_Recent_Activity_list.setVisibility(View.VISIBLE);
            _view_more_list.setVisibility(View.VISIBLE);
        }else{
            this._dashboard_Recent_Activity_list.setVisibility(View.VISIBLE);
            this._dashboard_no_Recent_Activity_list.setVisibility(View.GONE);
            _view_more_list.setVisibility(View.GONE);
        }
    }

    private void initNavigationMenu(Bundle savedInstanceState){
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(_main_toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        drawerAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(NAV_DASHBOARD).setChecked(true),
                createItemFor(NAV_EXPENSE_CHART),
                createItemFor(NAV_WALLET_LIST),
                createItemFor(NAV_MY_NOTES),
                createItemFor(NAV_SETTING),
                new SpaceItem(48),
                createItemFor(NAV_SIGN_OUT)));

        drawerAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawerAdapter);

        drawerAdapter.setSelected(NAV_DASHBOARD);
        _current_balance = findViewById(R.id.current_balance);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(Color.parseColor("#97AAB9"))
                .withTextTint(Color.parseColor("#97AAB9"))

                .withSelectedIconTint(color(R.color.white))
                .withSelectedTextTint(color(R.color.white))
                ;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    private Adapter_List_Activities laa;
    public void showRecentActivity(){
        //test
        //TimeZone tz = TimeZone.getDefault();
        //Toast.makeText(this, "TimeZone : "+tz.getDisplayName(false, TimeZone.SHORT_GMT)+" + Timezone id : " +tz.getID(), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "> "+TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT_GMT), Toast.LENGTH_SHORT).show();

        if (this.md.getListActivities().size() > 0) {
            //Date date1 = new Date();
            //this.md.getListActivities().put("1", new Activities("wallet1", "bank anda", "Snack", 1000.0, "test makan", date1, false));
            //this.md.getListActivities().put("7", new Activities("wallet1", "bank anda", "Transfer", 999.0, "test intenal transfer to XXX, fee 0", date1, false));
            showNotifNoData(false);

            laa = new Adapter_List_Activities(this, this.md.getN_show_list(), this.md, this.startDate, this.endDate, "dashboard");
            this._dashboard_Recent_Activity_list.setAdapter(laa);
        } else {
            setTextComponent(this.md.getListWallet(), md.getListActivities());
            showNotifNoData(true);
        }


        //Toast.makeText(this, ""+this.md.getListActivities().size(), Toast.LENGTH_SHORT).show();
        //for (Map.Entry<String, Activities> entry : this.md.getListActivities().entrySet()) {
        //    Toast.makeText(this, entry.getValue().toString(), Toast.LENGTH_SHORT).show();
        //}
    }

    @SuppressLint("SetTextI18n")
    public void setTextComponent(Map<String, Wallet> listWallet, Map<String, Activities> listActivities) {

        double balance = 0, in = 0, ex = 0;
        for (Map.Entry<String, Wallet> w : listWallet.entrySet()) {
            balance+=w.getValue().getNominal();
        }
        _current_balance.setText(currencyFormat.format(balance));

        for (Map.Entry<String, Activities> entry : listActivities.entrySet()) {
            if (!entry.getValue().getTitleActivities().equalsIgnoreCase("transfer") &&
                    (entry.getValue().getDateActivities().after(this.startDate) && entry.getValue().getDateActivities().before(this.endDate) )
            ) {
                if(entry.getValue().getType() == lov.activityType.INCOME){//if (entry.getValue().isIncome()) {
                    in = in + entry.getValue().getNominalActivities();
                }else{
                    ex = ex + entry.getValue().getNominalActivities();
                }
            }
        }

        this._dashboard_income.setText(getString(R.string.currency)+" "+lov.currencyFormat.format(in));
        this._dashboard_expense.setText(getString(R.string.currency)+" "+lov.currencyFormat.format(ex));
    }



    @Override
    public void onItemSelected(int position) {
        if (position == NAV_SIGN_OUT) {
            dataStore.getInstance().clear();
            finish();
            Intent i = new Intent(this, Sign_In.class);
            startActivity(i);
        }else if (position == NAV_WALLET_LIST) {
            Intent i = new Intent(this, Wallet_List.class);
            i.putExtra("md", this.md);
            startActivityForResult(i, NAV_WALLET_LIST_CODE);
        }else if (position == NAV_SETTING) {
            finish();
            Intent i = new Intent(this, Account_Setting.class);
            i.putExtra("md", this.md);
            startActivity(i);
        }else if (position == NAV_EXPENSE_CHART) {
            Intent i = new Intent(this, Pie_Chart.class);
            i.putExtra("md", this.md);
            i.putExtra("startDate", this.startDate.getTime());
            i.putExtra("endDate", this.endDate.getTime());

            startActivity(i);
            drawerAdapter.setSelected(NAV_DASHBOARD);

        }else if (position == NAV_MY_NOTES) {
            Toast.makeText(this, "Under development", Toast.LENGTH_LONG).show();
            this.drawerAdapter.setSelected(NAV_DASHBOARD);
        }
        slidingRootNav.closeMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.drawerAdapter.setSelected(NAV_DASHBOARD);

        if (requestCode == NAV_ACTIVITIES_LIST_SHOW_MORE_CODE) {
            if (data != null) {
                this.md = (MyData) data.getSerializableExtra("md");
                this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());
                this.showRecentActivity();
            }
        }
        else
        if (requestCode == NAV_WALLET_LIST_CODE) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                if (data != null) {
                    this.md = (MyData) data.getSerializableExtra("md");
                    this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Dashboard.this, Wallet_Update.class);
                            i.putExtra("md", md);
                            startActivityForResult(i, NAV_WALLET_UPDATE_CODE);
                        }
                    },250);
                }
            }
            else
            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    this.md = (MyData) data.getSerializableExtra("md");
                    this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());
                }
            }
        }
        else
        if (requestCode == NAV_WALLET_UPDATE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    this.md = (MyData) data.getSerializableExtra("md");
                    this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Dashboard.this, Wallet_List.class);
                    i.putExtra("md", md);
                    startActivityForResult(i, NAV_WALLET_LIST_CODE);
                }
            },250);
        }
        else
        if (requestCode == NAV_NEW_ACTIVITIES_CODE){
            if (resultCode == Activity.RESULT_OK){
                if (data != null) {
                    this.md = (MyData) data.getSerializableExtra("md");
                    this.setTextComponent(this.md.getListWallet(), this.md.getListActivities());
                    this.showRecentActivity();
                }
            }
        }
    }



    private void confirm(){

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_MaterialComponents)
                .setTitle("Confirm")
                .setMessage("You don't have any wallet. Please create first to add activities!")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Dashboard.this, Wallet_Update.class);
                        i.putExtra("md", md);
                        startActivityForResult(i, NAV_WALLET_UPDATE_CODE);
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
        materialAlertDialogBuilder.show();
    }

    public void showMoreActivity(){
        Intent i = new Intent(this, Show_More_Activity.class);
        i.putExtra("md", this.md);
        startActivityForResult(i, NAV_ACTIVITIES_LIST_SHOW_MORE_CODE);
    }

    private void setDateRange(){
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        startDate = calendar.getTime();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        endDate = calendar.getTime();


    }



}