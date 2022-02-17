package com.my.wallet.env;

import android.annotation.SuppressLint;

import android.icu.util.TimeZone;
import com.google.gson.Gson;
import com.my.wallet.model.Activities;
import com.my.wallet.model.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class lov {
    public static String uid = "UID";
    public static String username = "USERNAME";
    public static String token = "TOKEN";
    public static String refreshtoken = "REFRESHTOKEN";

    public static String md = "MD";

    public static Gson g = new Gson();

    public static CharSequence[] list_wallet_type = {"Bank Account", "Cash", "E-Money"};

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

    private static JSONObject message;

    public static JSONObject signInRequest(String email, String password) throws JSONException {
        message  = new JSONObject();

        message.put("email", email);
        message.put("password", password);


        return message;
    }

    public static JSONObject signUpRequest(String email, String password, String username, String deviceId) throws JSONException {
         message  = new JSONObject();

        message.put("email", email);
        message.put("password", password);
        message.put("username", username);
        message.put("deviceId", deviceId);


        return message;
    }

    public static JSONObject refreshTokenRequest(String refreshToken) throws JSONException {
         message  = new JSONObject();

        message.put("refreshToken", refreshToken);

        return message;
    }

    public static JSONObject transferRequest(String from_id, String to_id, String nominal, String fee, Date date) throws JSONException {
        message = new JSONObject();

        message.put("id", UUID.randomUUID().toString());
        message.put("walletIdSource", from_id);
        message.put("walletIdDestination", to_id);
        message.put("nominal", nominal);
        message.put("fee", fee);
        message.put("date", dateFormatter4.format(date));

        return message;
    }

    public static JSONObject removeTransferRequest(String id, String from_id, String to_id, String nominal, String fee, String date) throws JSONException {
        message  = new JSONObject();

        message.put("id", id);
        message.put("walletIdSource", from_id);
        message.put("walletIdDestination", to_id);
        message.put("nominal", nominal);
        message.put("fee", fee);
        message.put("date", date);

        return message;
    }

    public static JSONObject createWalletRequest(Wallet w) throws JSONException {
        message  = new JSONObject();

        message.put("id", w.getId());
        message.put("backgroundColor", w.getBackgroundColor());
        message.put("name", w.getName());
        message.put("nominal", w.getNominal());
        message.put("textColor", w.getTextColor());
        message.put("type", w.getType());

        return message;
    }

    public static JSONObject newActivityRequest(Activities na) throws JSONException {
        message  = new JSONObject();
        String date;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            date = lov.dateFormatter3.format(na.getDateActivities()) + ":00" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT_GMT);
        }else{
            date = lov.dateFormatter3.format(na.getDateActivities()) + ":00" + lov.dateFormatterZoneOnly.format(Calendar.getInstance().getTime());
        }
        message.put("id" , na.getId() );
        message.put("walletId" , na.getWalletId());
        message.put("walletName" , na.getWalletName()   );
        message.put("title" , na.getTitleActivities()   );
        message.put("category" , na.getCategory());
        message.put("desc" , na.getDescActivities());
        message.put("nominal" , na.getNominalActivities() );
        message.put("date" , date );
        message.put("type" , na.getType() );

        return message;
    }

    public static JSONObject updateUserInfoRequest(String email, String password, String uid, String username, String urlAvatar) throws JSONException {
        message  = new JSONObject();

        message.put("email" ,    email);
        message.put("password" , password);
        message.put("uid" ,      uid);
        message.put("username",  username);
        message.put("urlAvatar", urlAvatar );

        return message;
    }
}
