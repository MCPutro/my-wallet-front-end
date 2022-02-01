package com.my.wallet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MyData implements Serializable {
    private String UID;
    private String username;
    private HashMap<String, Wallet> listWallet;
    private LinkedHashMap<String, Activities> listActivities;
    private String urlAvatar;

    private int n_show_list;

    public MyData() {
        this.n_show_list = 10;
    }

    public MyData(String UID, String username, String urlAvatar) {
        this.username = username;
        this.UID = UID;
        this.listWallet = new HashMap<>();
        this.listActivities = new LinkedHashMap<>();

        this.urlAvatar = urlAvatar;

        this.n_show_list = 10;
    }

    public MyData(String UID, String username, HashMap<String, Wallet> listWallet, LinkedHashMap<String, Activities> listActivities) {
        this.UID = UID;
        this.username = username;
        this.listWallet = listWallet;
        this.listActivities = listActivities;
    }

    public void addWallet(Wallet d) {
        this.listWallet.put(d.getId(), d);
    }

    @Override
    public String toString() {
        return "MyData{\n" +
                "UID='" + UID + '\'' +
                ",\n username='" + username + '\'' +
                ",\n listWallet=" + listWallet +
                ",\n listActivities=" + listActivities +
                ",\n n_show_list=" + n_show_list +
                //",\n formatDateTime='" + formatDateTime + '\'' +
                "\n}";
    }
}

