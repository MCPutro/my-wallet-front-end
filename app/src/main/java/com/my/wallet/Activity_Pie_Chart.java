package com.my.wallet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.util.Pair;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.my.wallet.env.iconList;
import com.my.wallet.env.lov;
import com.my.wallet.model.Activities;
import com.my.wallet.model.MyData;

import java.util.*;


public class Activity_Pie_Chart extends AppCompatActivity implements OnChartValueSelectedListener {
    private MyData md;
    private Date startDate;
    private Date endDate;
    private Double total = 0.0;
    private ArrayList<PieEntry> dataEntries;
    private Calendar calendar;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private final CharSequence[] items = iconList.list_expense_category().toArray(new CharSequence[iconList.list_expense_category().size()]);
    private final boolean[] itemsCheck = new boolean[items.length];
    private ArrayList<String> itemsSelect = new ArrayList<>();
    String s = "Select All";

    /**component**/
    private TextInputEditText _period;
    private PieChart _mChart;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            this.md = (MyData) getIntent().getSerializableExtra("md");

            long x = getIntent().getLongExtra("startDate", -1);
            long y = getIntent().getLongExtra("endDate", -1);

            if (x > -1 && y > -1) {
                this.startDate = new Date(x);
                this.endDate = new Date(y);
            }else{
                this.startDate = new Date();
                this.endDate = new Date();
            }

        }catch (Throwable t){
            Toast.makeText(this, "error1 : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        /** **/
        this.calendar = Calendar.getInstance();

        MaterialToolbar _activity_pie_chart_toolbar = findViewById(R.id.activity_pie_chart_toolbar);
        _activity_pie_chart_toolbar.setNavigationOnClickListener(v -> onBackPressed());


        this._period = findViewById(R.id.activity_pie_chart_period);
        this._period.setText(lov.dateFormatter5.format(this.startDate)+" - "+lov.dateFormatter5.format(this.endDate));
        this._period.setOnClickListener(v -> {
            showDateRangePick();
        });
        this._period.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDateRangePick();
        });


        this._mChart = findViewById(R.id.pie_chart);
        this._mChart.setCenterTextColor(Color.parseColor("#FF0000"));
        this._mChart.setOnChartValueSelectedListener(this);


        setDataPie(new ArrayList<>());

        //testing
        Arrays.fill(this.itemsCheck, false);
        this.itemsCheck[0] = true;
        this.itemsSelect.add((String) this.items[0]);
        this.alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Category")
                .setMultiChoiceItems(this.items, this.itemsCheck, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        this.itemsSelect.add((String) this.items[which]);
                    }else{
                        this.itemsSelect.remove((String) this.items[which]);
                    }

                    this.itemsCheck[which] = isChecked;
                })
                .setPositiveButton("Ok",(dialog, which) -> {
                    //Toast.makeText(this, ""+this.itemsSelect, Toast.LENGTH_SHORT).show();
                    setDataPie(this.itemsSelect);
                    dialog.dismiss();
                })
        ;

        LinearLayout pie_category = findViewById(R.id.pie_category);
        pie_category.setOnClickListener(v ->  showCategory());

    }

    private void showCategory(){
        this.alertDialogBuilder.show();
    }

    private void showDateRangePick(){
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select a date")
                .build()
                ;

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    setPeriod(selection.first, selection.second);
                    this._period.setText(lov.dateFormatter5.format(this.startDate)+" - "+lov.dateFormatter5.format(this.endDate));
                    setDataPie(itemsSelect);
                });
    }

    private void setDataPie(ArrayList<String> category2show){
        if (category2show.contains("All") && category2show.size() == 1) {
            category2show = new ArrayList<>();
        }


        dataEntries = new ArrayList<>();
        this.total = 0.0;
        //?
        groupingMapByKey<String, Double> tg = new groupingMapByKey<>();

        for (Map.Entry<String, Activities> act : this.md.getListActivities().entrySet()) {
            if (
                    (act.getValue().getDateActivities().after(startDate) && act.getValue().getDateActivities().before(endDate))
                            &&
                            !act.getValue().isIncome()
                            &&
                            !"Transfer".equalsIgnoreCase(act.getValue().getTitleActivities())
            ){
                if (category2show.isEmpty()) {
                    tg.add(act.getValue().getTitleActivities(), act.getValue().getNominalActivities());
                }else{
                    if (category2show.contains(act.getValue().getTitleActivities())) {
                        tg.add(act.getValue().getTitleActivities(), act.getValue().getNominalActivities());
                    }
                }
                this.total += act.getValue().getNominalActivities();

            }


        }

        for (String key : tg.keySet()) {
            dataEntries.add(new PieEntry((float) sum(tg.get(key)), key));
        }


        if (dataEntries.size()>0) {showPieChart(dataEntries);}
        else{
            _mChart.clear();
        }
    }

    public double sum(ArrayList<Double> xx){
        Double temp = 0.0;
        for (int i = 0; i < xx.size(); i++) {
            temp += xx.get(i);
        }
        return temp;
    }

    private void showPieChart(ArrayList<PieEntry> dataEntries) {
        /**data entry*/
        PieDataSet dataSet = new PieDataSet(dataEntries, "test1");

        this._mChart.setCenterText("All\n"+getString(R.string.currency)+" "+ lov.currencyFormat.format(this.total)   );

        /**color pie**/
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        /**declare chart**/

        _mChart.setEntryLabelColor(getColor(R.color.textDiff));
        //_mChart.setUsePercentValues(true);
        _mChart.getDescription().setEnabled(false);
        _mChart.setExtraOffsets(5, 5, 5, 5);

        _mChart.setDragDecelerationFrictionCoef(0.95f);
        _mChart.setDrawHoleEnabled(true);
        _mChart.setHoleColor(Color.alpha(0));
        _mChart.setTransparentCircleColor(Color.WHITE);
        _mChart.setTransparentCircleAlpha(110);

        _mChart.setHoleRadius(50f);
        _mChart.setTransparentCircleRadius(61f);

        _mChart.setDrawCenterText(true);

        _mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        _mChart.setRotationEnabled(true);
        _mChart.setHighlightPerTapEnabled(true);

        /**add dataEntry to chart**/
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        _mChart.setData(data);

        // undo all highlights
        _mChart.highlightValues(null);

        _mChart.invalidate();

        _mChart.animateY(1400, Easing.EaseInOutQuad);


        // entry label styling
        _mChart.setEntryLabelColor(Color.BLUE);

        _mChart.setEntryLabelTextSize(12f);

        //legend
        _mChart.getLegend().setEnabled(false);
//        Legend l = _mChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(true);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);


    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) return;
        //Toast.makeText(this, "VAL SELECTED Value: " + e.getY() + ", index: " + h.getX()+ ", DataSet index: " + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
        _mChart.setCenterText(this.dataEntries.get((int)h.getX()).getLabel() + "\n" + getString(R.string.currency) + " " + lov.currencyFormat.format(e.getY())  );
        _mChart.setCenterTextColor(Color.parseColor("#FF0000"));
    }

    @Override
    public void onNothingSelected() {
        _mChart.setCenterText("All\n" + getString(R.string.currency) + " " + lov.currencyFormat.format(total));
    }

    private void setPeriod(long start, long end){
        calendar.setTimeInMillis(start);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDate = calendar.getTime();

        calendar.setTimeInMillis(end);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        this.endDate = calendar.getTime();


    }
}