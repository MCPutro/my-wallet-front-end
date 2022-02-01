package com.my.wallet.model;

import android.icu.util.TimeZone;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.my.wallet.env.lov;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import lombok.*;

@Setter
@Getter
@Builder
public class Activities implements //Comparable<Activities>,
        Serializable {
    private String id;
    private String walletId;
    private String walletName;
    private String titleActivities, descActivities, category;
    private Double nominalActivities;
    private Date dateActivities;
    private boolean income;


    @Override
    public String toString() {
        String date;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            date = lov.dateFormatter3.format(dateActivities) + ":00" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT_GMT);
        }else{
            date = lov.dateFormatter3.format(dateActivities) + ":00" + lov.dateFormatterZoneOnly.format(Calendar.getInstance().getTime());
        }
        return "{" +
                "\"id\" : \""           + id                + "\"," +
                "\"walletId\" : \""     + walletId          + "\"," +
                "\"walletName\" : \""   + walletName        + "\"," +
                "\"title\" : \""        + titleActivities   + "\"," +
                "\"category\" : \""     + category          + "\"," +
                "\"desc\" : \""         + descActivities    + "\"," +
                "\"nominal\" : "        + nominalActivities + ", " +
                "\"date\" : \""         + date              + "\"," +
                "\"income\" : "         + income            +

                "}";
    }
}

