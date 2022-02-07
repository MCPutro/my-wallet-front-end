package com.my.wallet.test;


import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.my.wallet.R;
import com.my.wallet.env.iconList;
import com.my.wallet.test.*;


import java.util.ArrayList;
import java.util.List;

public class Activity_Test extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


//        Button bb = findViewById(R.id.tombol_test);
//        bb.setOnClickListener(view -> {
//            if (bottomSheetDialog == null) {
//                bottomSheetDialog = new BottomSheetDialog(this);
//                LayoutInflater inflater = LayoutInflater.from(this);
//                View v = inflater.inflate(R.layout.test, (ViewGroup)
//                        findViewById(R.id.artistContainer), false);
//                bottomSheetDialog.setContentView(v);
//
//                setRecyclerViewItem2(v);
//
//                //1
//                FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
//                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
//                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
//
//                int windowHeight = getWindowManager().getDefaultDisplay().getHeight();//getWindowHeight();
//                if (layoutParams != null) {
//                    layoutParams.height = windowHeight;
//                }
//                bottomSheet.setLayoutParams(layoutParams);
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                //
//
//                v.findViewById(R.id.imageClose).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//            }
//            bottomSheetDialog.show();
//        });

    }

    private void setRecyclerViewItem2(View v) {
        RecyclerView bottomRecyclerView = v.findViewById(R.id.bottomRv);
        test_adapeter ta = new test_adapeter(this, iconList.expense_category());
        bottomRecyclerView.setAdapter(ta);
    }


    private void setRecyclerViewItem(View v) {
        RecyclerView bottomRecyclerView = v.findViewById(R.id.bottomRv);
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist(R.mipmap.ic_bicycle, "Katty Perry", "Lahir 25 Oktober 1984, dikenal secara profesional " +
                "sebagai Katy Perry, adalah seorang penyanyi, penulis lagu dan juri televisi berkebangsaan Amerika Serikat."));
        artists.add(new Artist(R.mipmap.ic_adminis_costs, "Robert Downey Jr.", "Lahir 4 April 1965, adalah seorang aktor Amerika. " +
                "Kariernya sudah termasuk keberhasilan dan kepopuleran di masa mudanya, diikuti dengan penyalahgunaan zat terlarang " +
                "dan masalah hukum, serta kesuksesannya pada usia remaja."));
//        artists.add(new Artist(R.mipmap.ic_bill_cc, "Sutisna", "Lahir di Cimahi, 15 November 1976; umur 44 tahun, adalah seorang " +
//                "pelawak, pembawa acara, penyanyi, dan aktor berkebangsaan Indonesia. Ia dikenal karena kemampuannya membuat lelucon " +
//                "spontan yang responsif dan kreatif."));
//        artists.add(new Artist(R.mipmap.ic_calc_2, "Chelsea Elizabeth Islan", "Lahir di Washington, D.C., 2 Juni 1995; umur 25 tahun, " +
//                "merupakan seorang aktris berkebangsaan Indonesia. Dia telah bermain dalam beberapa film seperti Refrain, Street Society, Merry " +
//                "Riana: Mimpi Sejuta Dolar dan Dibalik 98. Dia juga bermain di serial televisi berjudul Tetangga Masa Gitu?"));
//        artists.add(new Artist(R.mipmap.ic_e_money, "Chris Hemsworth", "Lahir di Melbourne, Australia, 11 Agustus 1983; umur 37 tahun, " +
//                "adalah aktor Australia. Ia dikenal atas perannya sebagai Thor dalam film-film Marvel Studios seperti Thor (2011), The Avengers (2012), " +
//                "dan Thor: The Dark World (2013)"));
//        artists.add(new Artist(R.mipmap.ic_education, "Agnes Monica Muljoto", "Lahir di Jakarta, 1 Juli 1986; umur 34 tahun, atau yang sekarang " +
//                "dikenal sebagai Agnez Mo, adalah seorang penyanyi dan artis berkebangsaan Indonesia. Ia memulai kariernya di industri hiburan pada usia " +
//                "enam tahun sebagai seorang penyanyi cilik."));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(artists, this);
        bottomRecyclerView.setAdapter(recyclerViewAdapter);
    }


}