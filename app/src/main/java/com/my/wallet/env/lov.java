package com.my.wallet.env;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class lov {
    public static String uid = "UID";
    public static String username = "USERNAME";
    public static String token = "TOKEN";
    public static String refreshtoken = "REFRESHTOKEN";

    public static String md = "MD";

    public static Gson g = new Gson();

    public static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.ENGLISH);//new Locale("in", "ID"));

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatter1 = new SimpleDateFormat("dd MMM yyyy");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatter2 = new SimpleDateFormat("dd MMM yyyy HH:mm");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatter3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatter4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatter5 = new SimpleDateFormat("MMM dd, yyyy");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormatterZoneOnly = new SimpleDateFormat("Z");

    /****/
    private static char[] c = new char[]{'K', 'M', 'B', 'T'};
    public static String prettyCount(double n, int iteration) {
        if (n <= 999){
            return lov.currencyFormat.format(n);
        }
        else{
            double d = ((long) n / 100) / 10.0;
            boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
            return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                    ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                            (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                    ) + "" + c[iteration])
                    : prettyCount(d, iteration+1));
        }
    }

    public enum tokenType {
        REFRESH_TOKEN,
        TOKEN
    }

    public enum popUpType{
        LOADING,
        POSITIF,
        NEGATIF
    }

    public enum activityType{
        INCOME,
        EXPENSE,
        TRANSFER
    }

    public enum trf{
        SOURCE,
        DESTINATION
    }
}
