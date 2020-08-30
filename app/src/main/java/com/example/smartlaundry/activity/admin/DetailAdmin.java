package com.example.smartlaundry.activity.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.user.DetailUser;
import com.example.smartlaundry.activity.user.MainActivity;
import com.example.smartlaundry.activity.user.MyBasketList;
import com.example.smartlaundry.models.DataLaundry;
import com.example.smartlaundry.utils.Path;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.ByOrder;
import static com.example.smartlaundry.utils.Path.LAUNDRY;
import static com.example.smartlaundry.utils.Path.NaiveBayes;
import static com.example.smartlaundry.utils.Path.step;
import static com.example.smartlaundry.utils.Path.step1;
import static com.example.smartlaundry.utils.Path.step2;
import static com.example.smartlaundry.utils.Path.step3;
import static com.example.smartlaundry.utils.Path.step4;

public class DetailAdmin extends AppCompatActivity {
    private TextView tvnama,tvtanggal,tvberat,tvalamat,tvnohp,tvlembab,tvharga;
    private CircleImageView circleImageView;
    private int harga,berat,lembab;
    private String metode,spin;
    private Locale localeID;
    private String keyId;;
    private DatabaseReference databaseReference;
    private Query query;
    private android.app.AlertDialog alertDialog;
    private Button update;
    private NumberFormat formatRupiah;
    private HorizontalStepView horizontalStepView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_admin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        metode = getIntent().getStringExtra("metode");
        init();
        stepview();


        update.setOnClickListener(v -> {
            Dialog dialogupdate = new Dialog(this);
            dialogupdate.setContentView(R.layout.alert_dialog_update_admin);
            dialogupdate.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogupdate.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT
                    , WindowManager.LayoutParams.WRAP_CONTENT);
            dialogupdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogupdate.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialogupdate.show();
            TextView spinner = dialogupdate.findViewById(R.id.kirimupdate);
            Button btncanel,btnupdate;
            btncanel = dialogupdate.findViewById(R.id.cancelupdateproses);
            btnupdate = dialogupdate.findViewById(R.id.submitupdateproses);
            if (step.equals("Sudah Dibayar")){
               spinner.setText("Jemput Pakaian");
                spin = spinner.getText().toString();
            }
            else if (step.equals("Jemput Pakaian")){
                spinner.setText("Pencucian");
                spin = spinner.getText().toString();
            }
            else {
                spinner.setText("Selesai");
                spin = spinner.getText().toString();
            }


            btncanel.setOnClickListener(v1 -> {
                dialogupdate.dismiss();
            });
            btnupdate.setOnClickListener(v12 -> {
                new AlertDialog.Builder(DetailAdmin.this)
                        .setTitle("Konfirmasi!")
                        .setIcon(R.mipmap.ic_apk)
                        .setMessage("Update Prosess ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            alertDialog.show();
                            if (spin.equals("-Pilih-")){
                                Toasty.warning(getApplicationContext(),"Anda Belum Memilih Proses",Toasty.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                            else {
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot updatedata : dataSnapshot.getChildren()){
                                            updatedata.getRef().child("status").setValue(spin);

                                        }
                                        if (metode.equals("Naive")){
                                            Intent intent = new Intent(getApplicationContext(), ListBasketAdmin.class);
                                            startActivity(intent);
                                            finishAffinity();
                                            finish();
                                            CustomIntent.customType(DetailAdmin.this,"right-to-left");
                                        }else {
                                            Intent intent = new Intent(getApplicationContext(), ListOrderAdmin.class);
                                            startActivity(intent);
                                            finishAffinity();
                                            finish();
                                            CustomIntent.customType(DetailAdmin.this,"right-to-left");
                                        }
                                        alertDialog.dismiss();
                                        dialogupdate.dismiss();
                                        Toasty.success(getApplicationContext(),"Update Prosess Berhasil",Toasty.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
                                    }
                                });
                            }


                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            });

        });
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvnama = findViewById(R.id.namadetialadmin);
        tvtanggal = findViewById(R.id.tanggaldetailadmin);
        tvberat = findViewById(R.id.beratdetailadmin);
        tvlembab = findViewById(R.id.detailhumiadmin);
        tvalamat = findViewById(R.id.alamatdetailadmin);
        tvnohp = findViewById(R.id.notelpadmin);
        tvharga = findViewById(R.id.hargadetailadmin);
        circleImageView = findViewById(R.id.adminimgdetail);
        horizontalStepView = findViewById(R.id.horidetailadmin);
        update = findViewById(R.id.updatedetailadmin);
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        alertDialog = new SpotsDialog.Builder().setContext(DetailAdmin.this).build();
        alertDialog.setMessage("Mohon Tunggu");
        alertDialog.setIcon(R.mipmap.ic_apk);
        if (metode.equals("Naive")){
            databaseReference = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(NaiveBayes);
        }else {
            databaseReference = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(ByOrder);
        }

        Intent intent = getIntent();
        DataLaundry terimadata = intent.getParcelableExtra(Path.DATA);
        if (terimadata!=null){
            keyId = terimadata.getKey();
            harga = terimadata.getHarga();
            lembab = terimadata.getKelembaban();
            berat = terimadata.getBerat();
            step = terimadata.getStatus();
            tvnama.setText(terimadata.getNama());
            tvalamat.setText(terimadata.getAlamat());
            tvtanggal.setText(terimadata.getTanggal());
            tvnohp.setText(terimadata.getNohp());
            tvharga.setText(formatRupiah.format((double)harga));
            tvberat.setText(berat+" KG");
            tvlembab.setText(lembab+" %");
            query = databaseReference.orderByChild("key").equalTo(keyId);
            if (step.equals("Belum Dibayar") || step.equals("Selesai")){
               update.setVisibility(View.GONE);
            }

        }

    }
    private void stepview() {
        switch (step) {
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

        horizontalStepView.setStepViewTexts(stepBeans)
                .setTextSize(13)
                .setStepsViewIndicatorCompletedLineColor(getApplicationContext().getColor(R.color.background))
                .setStepsViewIndicatorUnCompletedLineColor(getApplicationContext().getColor(R.color.orange))
                .setStepViewComplectedTextColor(getApplicationContext().getColor(R.color.black))
                .setStepViewUnComplectedTextColor(getApplicationContext().getColor(R.color.black))
                .setStepsViewIndicatorCompleteIcon(getApplicationContext().getDrawable(R.drawable.sukses))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.attention));
    }



}
