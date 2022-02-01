package com.my.wallet.test;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.my.wallet.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ArtistViewHolder> {

    private List<Artist> artists;
    private Context context;

    public RecyclerViewAdapter(List<Artist> artists, Context context) {
        this.artists = artists;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.picLogo.setImageResource(artist.getPhoto());
        holder.textName.setText(artist.getName());
        holder.textBio.setText(artist.getBio());

        holder.layoutRootContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(context, "Anda menyukai "
                        + holder.textName.getText().toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {

        ImageView picLogo;
        TextView textName, textBio;
        LinearLayout layoutRootContainer;

        ArtistViewHolder(View itemView) {
            super(itemView);
            picLogo = itemView.findViewById(R.id.picLogo);
            textName = itemView.findViewById(R.id.textName);
            textBio = itemView.findViewById(R.id.textBio);
            layoutRootContainer = itemView.findViewById(R.id.layoutRootContainer);
        }

    }

}
