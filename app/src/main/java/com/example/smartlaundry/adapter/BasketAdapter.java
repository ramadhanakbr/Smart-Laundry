package com.example.smartlaundry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.example.smartlaundry.R;
import com.example.smartlaundry.models.DataLaundry;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartlaundry.utils.Path.step1;
import static com.example.smartlaundry.utils.Path.step2;
import static com.example.smartlaundry.utils.Path.step3;
import static com.example.smartlaundry.utils.Path.step4;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.LaundryViewHolder> {

    private ArrayList<DataLaundry> laundries;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public BasketAdapter(Context context, ArrayList<DataLaundry> laundries) {
        this.laundries = laundries;
        this.context = context;
    }

    @NonNull
    @Override
    public LaundryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_laundry, parent, false);
        return new LaundryViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryViewHolder holder, int position) {
        DataLaundry dataLaundry = laundries.get(position);
        int berat = dataLaundry.getBerat();
        int lembab = dataLaundry.getKelembaban();
        if (berat<2 && lembab < 60){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        holder.textView.setText(dataLaundry.getTanggal());
        String pembayaran = dataLaundry.getStatus();
        switch (pembayaran) {
            case "Belum Dibayar":
                step1 = 0;
                step2 = -1;
                step3 = -1;
                step4 = -1;
                break;
            case "Sudah Dibayar":
                step1 = 1;
                step2 = 0;
                step3 = -1;
                step4 = -1;
                break;
            case "Jemput Pakaian":
                step1 = 1;
                step2 = 1;
                step3 = 0;
                step4 = -1;
                break;
            case "Pencucian":
                step1 = 1;
                step2 = 1;
                step3 = 1;
                step4 = 0;
                break;
            case "Selesai":
                step1 = 1;
                step2 = 1;
                step3 = 1;
                step4 = 1;
                break;
        }
        List<StepBean> stepBeans = new ArrayList<>();
        stepBeans.add(new StepBean("Payment",step1));
        stepBeans.add(new StepBean("PickUp",step2));
        stepBeans.add(new StepBean("Laundry",step3));
        stepBeans.add(new StepBean("Finish",step4));

        holder.horizontalStepView.setStepViewTexts(stepBeans)
                .setTextSize(14)
                .setStepsViewIndicatorCompletedLineColor(context.getColor(R.color.orange))
                .setStepsViewIndicatorUnCompletedLineColor(context.getColor(R.color.white))
                .setStepViewComplectedTextColor(context.getColor(R.color.white))
                .setStepViewUnComplectedTextColor(context.getColor(R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(context.getDrawable(R.drawable.sukses))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(context, R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(context, R.drawable.attention));
    }

    @Override
    public int getItemCount() {
        return laundries.size();
    }

    static class LaundryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        HorizontalStepView horizontalStepView;
        LaundryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.row_tanggal);
            horizontalStepView = itemView.findViewById(R.id.horistep);
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
