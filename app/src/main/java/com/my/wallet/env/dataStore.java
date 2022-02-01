package com.my.wallet.env;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.my.wallet.model.MyData;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class dataStore {

    private SharedPreferences sharedPref;
    private Context context;
    private Gson g;

    private dataStore(Context c) {
        this.sharedPref = c.getSharedPreferences(api.pref_name, Context.MODE_PRIVATE);
        this.context = c;
        this.g = new Gson();
    }

    public dataStoreModel getData(){
        return new dataStoreModel(
                sharedPref.getString(lov.uid, null),
                sharedPref.getString(lov.username, null),
                sharedPref.getString(lov.token, null),
                sharedPref.getString(lov.refreshtoken, null),
                sharedPref.getString(lov.md, null)
        );
    }

    public Gson getG() {
        return g;
    }

    public Set<String> getUserCredential(){
        Set<String> s = new HashSet<>();
        s.add(sharedPref.getString("uname", null));
        s.add(sharedPref.getString("pword", null));

        return s;
    }

    public void setUserCredential(String uname, String pw){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uname", uname);
        editor.putString("pword", pw);
        editor.apply();
    }

    public void saveData(String uid, String username, String token, String refreshToken, MyData myData){
        SharedPreferences.Editor editor = sharedPref.edit();

        if (uid != null) { editor.putString(lov.uid, uid); }
        if (username != null) { editor.putString(lov.username, username); }
        if (token != null) { editor.putString(lov.token, token); }
        if (refreshToken != null) { editor.putString(lov.refreshtoken, refreshToken); }
        if (myData != null) {
            try {
                Gson g = new Gson();
                editor.putString(lov.md, g.toJson(myData));
            }catch (Throwable t) {
                Toast.makeText(this.context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        editor.apply();
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }


    private static dataStore ds;

    public static synchronized dataStore getInstance(Context c){
        if (ds == null){
            ds = new dataStore(c);
        }
        return ds;
    }

    public static synchronized dataStore getInstance(){
        if (ds == null){
            throw new IllegalStateException(api.class.getSimpleName() + " is not initialized, call getInstance(this) first");
        }
        return ds;
    }

    @Setter
    @Getter
    @ToString
    public static class dataStoreModel{
        private String UID;
        private String Username;
        private String Token;
        private String RefreshToken;
        private String myData;

        private dataStoreModel(){}

        private dataStoreModel(String UID, String username, String token, String refreshToken, String myData) {
            this.UID = UID;
            Username = username;
            Token = token;
            RefreshToken = refreshToken;
            this.myData = myData;
        }
    }

}
