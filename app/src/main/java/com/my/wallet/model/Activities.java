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
    //private boolean income;
    private lov.activityType type;

}

