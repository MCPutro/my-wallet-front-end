package com.my.wallet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.my.wallet.env.lov;

public class popUpNotification {

    /*** loading animation ***/
    private static AlertDialog popUpDialog;

    public static void show(Context context, lov.popUpType type, String text){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        boolean isCancelByOut = true;

        View layoutView = null;
        TextView notifText;
        ImageView notifIcon;
        TextView statNotif;
        MaterialButton popup_notif_text_btn;

        switch (type){
            case LOADING:
                layoutView = LayoutInflater.from(context).inflate(R.layout.popup_loading, null);
                isCancelByOut = false;
                break;
            case NEGATIF:
                layoutView = LayoutInflater.from(context).inflate(R.layout.popup_notification, null);

                notifText = layoutView.findViewById(R.id.popup_notif_text);
                notifIcon = layoutView.findViewById(R.id.popup_notif_icon);
                statNotif = layoutView.findViewById(R.id.popup_notif_status);
                popup_notif_text_btn = layoutView.findViewById(R.id.popup_notif_text_btn);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popup_notif_text_btn.setBackgroundTintList(context.getColorStateList(R.color.error));
                }else {
                    popup_notif_text_btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.error));
                }
                popup_notif_text_btn.setOnClickListener(v -> close());

                notifText.setText(text);

                statNotif.setText("Oh No...");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    statNotif.setTextColor(context.getColor(R.color.error));
                }else{
                    statNotif.setTextColor(ContextCompat.getColor(context, R.color.error));
                }

                notifIcon.setImageResource(R.drawable.ic_cancel_circle_24_red);

                break;
            case POSITIF:
                layoutView = LayoutInflater.from(context).inflate(R.layout.popup_notification, null);

                notifText = layoutView.findViewById(R.id.popup_notif_text);
                notifIcon = layoutView.findViewById(R.id.popup_notif_icon);
                popup_notif_text_btn = layoutView.findViewById(R.id.popup_notif_text_btn);

                popup_notif_text_btn.setOnClickListener(v -> {
                    close();
                });

                notifText.setText(text);

                notifIcon.setImageResource(R.drawable.ic_check_circle_24_green);

                break;

        }
        dialogBuilder.setView(layoutView);
        dialogBuilder.setCancelable(isCancelByOut);
        popUpDialog = dialogBuilder.create();
        popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpDialog.setCanceledOnTouchOutside(isCancelByOut);
        popUpDialog.setCancelable(isCancelByOut);

        popUpDialog.show();
    }

    public static void close(){popUpDialog.dismiss();}
}
