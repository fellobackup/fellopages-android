package com.uniprogy.outquiz.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LbAdapter extends RecyclerView.Adapter<LbAdapter.LbViewHolder> {

    private JSONArray players;

    public static class LbViewHolder extends RecyclerView.ViewHolder {
        public TextView rowAmount, rowUsername, rowRank;
        ImageView rowAvatar;
        View separator;

        public LbViewHolder(View view) {
            super(view);
            rowAmount = view.findViewById(R.id.rowAmount);
            rowUsername = view.findViewById(R.id.rowUsername);
            rowRank = view.findViewById(R.id.rowRank);
            rowAvatar = view.findViewById(R.id.rowAvatar);
            separator = view.findViewById(R.id.separator);
        }
    }

    public LbAdapter(JSONArray players) {
        this.players = players;
    }

    public void update(JSONArray players)
    {
        this.players = players;
        notifyDataSetChanged();
    }

    @Override
    public LbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_leaderboard, parent, false);

        return new LbViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LbViewHolder holder, int position) {
        try {
            JSONObject obj = players.getJSONObject(position);
            Player player = new Player(obj);
            holder.rowRank.setText(String.valueOf(position+1));
            holder.rowUsername.setText(player.username);
            player.setAvatar(holder.rowAvatar);
            int amount = obj.getInt("total");
            holder.rowAmount.setText(Misc.moneyFormat(amount));
            holder.separator.setVisibility(position == players.length()-1 ? View.INVISIBLE : View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return players != null ? players.length() : 0;
    }

}
