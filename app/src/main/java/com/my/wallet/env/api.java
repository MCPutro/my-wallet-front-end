package com.my.wallet.env;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class api {

    private RequestQueue queue;
    public RequestQueue getQueue() {
        return queue;
    }

    private static api instance = null;

    private api(Context c) {
        this.queue = Volley.newRequestQueue(c);
    }

    public static synchronized api getInstance(Context context) {
        if (instance == null) {
            instance = new api(context);
        }
        return instance;
    }

    public static synchronized api getInstance() {
        if (null == instance) {
            throw new IllegalStateException(api.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public static String pref_name = "p3RsoNal_Pref";

    public static String url = "https://rest-api-my-wallet.herokuapp.com";

    public static String path_signUp = "/api/user/signup";
    public static String path_signIn = "/api/user/signin";
    public static String path_createAndLogin = "/api/user/createAndLogin";
    public static String path_refreshToken = "/api/user/refresh-token";
    public static String path_userUpdate = "/api/user/update";


    public static String path_getWalletByUID = "/api/wallet/";
    public static String path_walletUpdate = "/api/wallet/";
    public static String path_walletRemove = "/api/wallet/";
    public static String path_walletTransfer = "/api/wallet/transfer";
    public static String path_walletCancelTransfer = "/api/wallet/cancelTransfer";

    public static String path_activity = "/api/activity/";

    public static int timeOut_ms = 30000;


}
