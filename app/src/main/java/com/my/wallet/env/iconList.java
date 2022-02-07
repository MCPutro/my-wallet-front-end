package com.my.wallet.env;

import androidx.annotation.NonNull;

import com.my.wallet.R;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class iconList {


    @NotNull
    public static Map<String, Integer> wallet_type(){
        Map<String, Integer> tmp = new HashMap<>();
        tmp.put("Bank Account", R.mipmap.icon_bank);
        tmp.put("Cash", R.mipmap.icon_cash);
        tmp.put("E-Money", R.mipmap.ic_e_money);
        return tmp;
    }

    public static Map<String, Integer> wallet_type_color(){
        Map<String, Integer> tmp = new HashMap<>();
        tmp.put("Bank Account", R.mipmap.ic_trf_bank);
        tmp.put("Cash", R.mipmap.ic_trf_cash);
        tmp.put("E-Money", R.mipmap.ic_trf_emoney);
        return tmp;
    }


    public static Map<String, Integer> income_list_map(){
        Map<String, Integer> temp = new LinkedHashMap<>();
        temp.put("Salary", R.mipmap.ic_salery);
        temp.put("Gift", R.mipmap.ic_hadiah);
        temp.put("Refunds", R.mipmap.ic_refund);
        temp.put("Investment", R.mipmap.ic_inver);
        temp.put("Reward", R.mipmap.ic_reward);
        temp.put("Other", R.mipmap.ic_other_income);
        return temp;
    }



    public static int transfer_icon(){
        return R.mipmap.ic_transfer;
    }

    @NotNull
    public static Map<String, category> expense_category(){
        Map<String, category> test = new LinkedHashMap<>();//HashMap<>();
        test.put("House", new category(R.mipmap.ic_house,
                new LinkedHashMap<String, Integer>(){{
                    put("Electricity Bill", R.mipmap.ic_electricity_bills);
                    put("Maintenance, Repair", R.mipmap.ic_home_repair);
                    put("Mineral Water", R.mipmap.ic_mineral_water);
                    put("Mortgage", R.mipmap.ic_cicilan_rumah);
                    put("Rental", R.mipmap.ic_sewa_rumah);
                }}
        ));
        test.put("Education", new category(R.mipmap.ic_education,
                new LinkedHashMap<String, Integer>(){{
                    put("Online Course", R.mipmap.ic_online_course);
                    put("Software Key", R.mipmap.ic_software_key);
                    put("Education", R.mipmap.ic_education);
                }}
        ));
        test.put("Food & Drink", new category(R.mipmap.ic_makan,
                new LinkedHashMap<String, Integer>(){{
                    put("Food & Drink", R.mipmap.ic_makan);
                    put("Fast-food", R.mipmap.ic_fast_food);
                    put("Snack", R.mipmap.ic_snack);
                    put("Fruit", R.mipmap.ic_fruit);
                    put("Restaurant", R.mipmap.ic_restaurant);
                }}
        ));
        test.put("Shopping", new category(R.mipmap.ic_shop,
                new LinkedHashMap<String, Integer>(){{
                    put("Shopping", R.mipmap.ic_shop);
                }}
        ));
        test.put("Transportation", new category(R.mipmap.ic_transport,
                new LinkedHashMap<String, Integer>(){{
                    put("Travel Ticket", R.mipmap.ic_travel_ticket);
                    put("Taxi", R.mipmap.ic_taxi);
                    put("Fuel", R.mipmap.ic_gas);
                    put("Transport", R.mipmap.ic_transport);
                    put("Top Up E-Money", R.mipmap.ic_emoney);
                }}
        ));
        test.put("Life & Entertainment", new category(R.mipmap.ic_life,
                new LinkedHashMap<String, Integer>(){{
                    put("Holiday", R.mipmap.ic_holiday);
                    put("Movie", R.mipmap.ic_movie);
                    put("Health Care, Doctor", R.mipmap.ic_medicine);
                    put("Life Events", R.mipmap.ic_life_event);
                    put("Game", R.mipmap.ic_game);
                    put("Bill", R.mipmap.ic_bill);
                    put("Toys", R.mipmap.ic_toys);
                    put("Bicycle", R.mipmap.ic_bicycle);
                    put("Credit & Internet", R.mipmap.ic_pulsa_n_inet);
                    put("Admin Costs, Fee", R.mipmap.ic_adminis_costs);
                    put("Investment", R.mipmap.ic_investasi);
                    put("Other", R.mipmap.ic_other_income);
                }}
        ));
//        test.put("Lifestyle", new category(R.mipmap.ic_transport,
//                new HashMap<String, Integer>(){{
//                }}
//        ));

        return test;

    }

    @NotNull
    public static ArrayList<String> list_expense_category(){
        ArrayList<String> tmp = new ArrayList<>();
        for (Map.Entry<String, category> entry : expense_category().entrySet()) {
            tmp.addAll(entry.getValue().getSub_category().keySet());
        }
        Collections.sort(tmp);
        tmp.add(0, "All");
        return tmp;
    }

    public static Map<String, dataEntry<Integer>>card_background(){
        Map<String, dataEntry<Integer>> temp = new HashMap<>();
        temp.put("background1", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud1));
        temp.put("background2", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud2));
        temp.put("background3", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud3));
        temp.put("background4", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud4));
        temp.put("background5", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud5));
        temp.put("background6", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud6));
        temp.put("background7", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud7));
        temp.put("background8", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud8));
        temp.put("background9", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud9));
        temp.put("background10", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud10));
        temp.put("background11", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud11));
        temp.put("background12", new dataEntry<>("#FFFFFFFF", R.drawable.backgroud12));


        return temp;
    }

    public static class dataEntry<T> {
        private String key;
        private T value;

        public dataEntry(String key, T value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
