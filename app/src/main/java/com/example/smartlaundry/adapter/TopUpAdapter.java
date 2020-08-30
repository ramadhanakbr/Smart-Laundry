package com.example.smartlaundry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartlaundry.R;
import com.example.smartlaundry.models.TopUp;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopUpAdapter extends RecyclerView.Adapter<TopUpAdapter.TopUpViewHolder> {
    private NumberFormat formatRupiah;
    private Locale localeID;
    private ArrayList<TopUp> topup;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public TopUpAdapter(Context context, ArrayList<TopUp> topup) {
        this.topup = topup;
        this.context = context;
    }

    @NonNull
    @Override
    public TopUpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new TopUpViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TopUpViewHolder holder, int position) {
        TopUp dataLaundry = topup.get(position);
        int saldo = dataLaundry.getSaldo();
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.textViewnama.setText(dataLaundry.getNama());
        holder.textViewSaldo.setText(formatRupiah.format((double)saldo));
        Glide.with(context).load(dataLaundry.getFoto()).into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return topup.size();
    }


    static class TopUpViewHolder extends RecyclerView.ViewHolder {
        TextView textViewnama,textViewSaldo;
        CircleImageView circleImageView;
        TopUpViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewnama = itemView.findViewById(R.id.nametopup);
            textViewSaldo = itemView.findViewById(R.id.saldotopup);
            circleImageView = itemView.findViewById(R.id.imgtopup);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position1 = getAdapterPosition();
                    if (position1 != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position1);
                    }
                }
            });
        }
    }
}
