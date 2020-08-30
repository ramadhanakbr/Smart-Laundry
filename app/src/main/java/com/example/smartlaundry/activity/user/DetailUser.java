package com.example.smartlaundry.activity.user;

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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.example.smartlaundry.R;
import com.example.smartlaundry.models.DataLaundry;
import com.example.smartlaundry.utils.Path;
import com.example.smartlaundry.utils.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import id.co.telkom.iot.AntaresHTTPAPI;
import id.co.telkom.iot.AntaresResponse;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.USERS;
import static com.example.smartlaundry.utils.Path.step;
import static com.example.smartlaundry.utils.Path.step1;
import static com.example.smartlaundry.utils.Path.step2;
import static com.example.smartlaundry.utils.Path.step3;
import static com.example.smartlaundry.utils.Path.step4;

public class DetailUser extends AppCompatActivity implements AntaresHTTPAPI.OnResponseListener{

    private static final String TAG = "Detail Antares";
    private TextView tvkglama,tvkgbaru,tvhumilama,tvtanggal,tvhargalama,tvhargabaru;
    private int hargalama,hargabaru,hasilharga,kglama,kgbaru,hasilkg,humilama,humibaru,saldo,sisasaldo;
    private Session session;
    private Locale localeID;
    private DatabaseReference databaseReference,databaseReference2;
    private Query query,querybayar;
    private String dataDevice,getkey;
    private android.app.AlertDialog alertDialog;
    private Button getdata,update;
    private NumberFormat formatRupiah;
    private AntaresHTTPAPI antaresAPIHTTP;
    private HorizontalStepView horizontalStepView;
    private String currentDate;
    private LinearLayout linearLayout;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        init();
        getdata.setOnClickListener(v ->{
            alertDialog.show();
            antaresAPIHTTP.getLatestDataofDevice(" ad45eb67934eb34f:92afc1fdefa0ff55","SmartLaundry","SmartLaundry");
        });

        update.setOnClickListener(v ->{
            Dialog dialogupdate = new Dialog(this);
            dialogupdate.setContentView(R.layout.alert_dialog_update_user);
            dialogupdate.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogupdate.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT
                    , WindowManager.LayoutParams.WRAP_CONTENT);
            dialogupdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogupdate.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialogupdate.show();
            hasilkg = kglama + kgbaru;
            hasilharga = hargalama + hargabaru;
            sisasaldo = saldo - hasilharga;
            TextView tvkgupdate,tvhumiupdate,tvsaldobefore,tvsaldoafter,tvprice;
            Button btncancel,btnupdate,btnbayar;
            btncancel = dialogupdate.findViewById(R.id.cancelupdate);
            btnupdate= dialogupdate.findViewById(R.id.submitbasketupdate);
            btnbayar = dialogupdate.findViewById(R.id.submitpayupdate);
            tvkgupdate = dialogupdate.findViewById(R.id.beratafterupdate);
            tvhumiupdate = dialogupdate.findViewById(R.id.humidityafterupdate);
            tvsaldoafter = dialogupdate.findViewById(R.id.saldoafterupdate);
            tvsaldobefore = dialogupdate.findViewById(R.id.saldobeforeupdate);
            tvprice = dialogupdate.findViewById(R.id.totalpriceupdate);
            tvkgupdate.setText(hasilkg +" KG");
            if (humibaru == 0){
                tvhumiupdate.setText(humilama +" %");
            }
            else {
                tvhumiupdate.setText(humibaru +" %");
            }
            tvsaldobefore.setText(session.getSpSaldo());
            tvprice.setText(formatRupiah.format((double)hasilharga));
            if (sisasaldo<0){
                tvsaldoafter.setText(formatRupiah.format((double)sisasaldo));
                tvsaldoafter.setTextColor(getColor(R.color.red));
            }
            else {
                tvsaldoafter.setText(formatRupiah.format((double)sisasaldo));
                tvsaldoafter.setTextColor(getColor(R.color.green));
                btnbayar.setBackground(getDrawable(R.drawable.round_green));
                btnbayar.setEnabled(true);
            }

            btncancel.setOnClickListener(v1 -> {
                dialogupdate.dismiss();
            });

            btnupdate.setOnClickListener(v12 -> {
                int kondisi;
                if (humibaru == 0 ){
                    kondisi = humilama;
                } else {
                    kondisi = humibaru;
                }
                new AlertDialog.Builder(DetailUser.this)
                        .setTitle("Konfirmasi!")
                        .setIcon(R.mipmap.ic_apk)
                        .setMessage("Lakukan Update ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            alertDialog.show();
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                                        ds.getRef().child("berat").setValue(hasilkg);
                                        ds.getRef().child("harga").setValue(hasilharga);
                                        ds.getRef().child("kelembaban").setValue(kondisi);
                                        ds.getRef().child("tanggal").setValue(currentDate);

                                    }
                                    alertDialog.dismiss();
                                    dialogupdate.dismiss();
                                    Toasty.success(getApplicationContext(),"Update Keranjang Berhasil",Toasty.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MyBasketList.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    finish();
                                    CustomIntent.customType(DetailUser.this,"bottom-to-up");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    alertDialog.dismiss();
                                    Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            });

            btnbayar.setOnClickListener(v13 -> {
                String status = "Sudah Dibayar";
                int kondisibayar;
                if (humibaru==0){
                    kondisibayar = humilama;
                }
                else {
                    kondisibayar =  humibaru;
                }
                DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference(USERS);
                Query queryuser = dbuser.orderByChild("uid").equalTo(session.getSPUid());
                new AlertDialog.Builder(DetailUser.this)
                        .setTitle("Konfirmasi!")
                        .setIcon(R.mipmap.ic_apk)
                        .setMessage("Lakukan Pembayaran ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            alertDialog.show();
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot updatedata : dataSnapshot.getChildren()){
                                        updatedata.getRef().child("berat").setValue(hasilkg);
                                        updatedata.getRef().child("harga").setValue(hasilharga);
                                        updatedata.getRef().child("kelembaban").setValue(kondisibayar);
                                        updatedata.getRef().child("tanggal").setValue(currentDate);
                                        updatedata.getRef().child("status").setValue(status);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
                                }
                            });
                            queryuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds :dataSnapshot.getChildren()){
                                        //update Data
                                        ds.getRef().child("saldo").setValue(sisasaldo);
                                    }
                                    alertDialog.dismiss();
                                    dialogupdate.dismiss();
                                    Toasty.success(getApplicationContext(),"Update Keranjang Berhasil",Toasty.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MyBasketList.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    finish();
                                    CustomIntent.customType(DetailUser.this,"left-to-right");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    alertDialog.dismiss();
                                    Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
                                }
                            });

                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            });

        });

        stepview();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        session = new Session(this);
        tvkglama = findViewById(R.id.beratdetail);
        tvkgbaru = findViewById(R.id.beratdetail2);
        tvhumilama = findViewById(R.id.detailhumi);
        tvhargalama = findViewById(R.id.hargadetail);
        tvhargabaru = findViewById(R.id.hargadetail2);
        tvtanggal = findViewById(R.id.tanggaldetail);
        getdata = findViewById(R.id.refreshdetail);
        update = findViewById(R.id.updatedetail);
        linearLayout = findViewById(R.id.tomboldetial);
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        databaseReference = FirebaseDatabase.getInstance().getReference(Path.LAUNDRY).child(Path.NaiveBayes);
        databaseReference2 = FirebaseDatabase.getInstance().getReference(Path.LAUNDRY).child(Path.ByOrder);
        alertDialog = new SpotsDialog.Builder().setContext(DetailUser.this).build();
        alertDialog.setMessage("Mohon Tunggu");
        alertDialog.setIcon(R.mipmap.ic_apk);
        antaresAPIHTTP = new AntaresHTTPAPI();
        antaresAPIHTTP.addListener(this);
        saldo = Integer.parseInt(session.getSpSaldoHitung());
        horizontalStepView = findViewById(R.id.horidetail);
        Intent intent = getIntent();
        DataLaundry terimadata = intent.getParcelableExtra(Path.DATA);
        if (terimadata!=null){
            getkey = terimadata.getKey();
            step = terimadata.getStatus();
            kglama = terimadata.getBerat();
            humilama = terimadata.getKelembaban();
            hargalama = terimadata.getHarga();
            tvkglama.setText(kglama +" KG");
            tvtanggal.setText(terimadata.getTanggal());
            if (kglama>=9){
                update.setBackground(getDrawable(R.drawable.round_green));
                update.setEnabled(true);
            }
            tvhargalama.setText(formatRupiah.format((double)hargalama));
            tvhumilama.setText(humilama +" %");
            query = databaseReference.orderByChild("key").equalTo(getkey);
            if (step.equals("Belum Dibayar")){
                linearLayout.setVisibility(View.VISIBLE);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponse(AntaresResponse antaresResponse) {
        Log.d(TAG,Integer.toString(antaresResponse.getRequestCode()));
        if(antaresResponse.getRequestCode()==0){
            try {
                JSONObject body = new JSONObject(antaresResponse.getBody());
                dataDevice = body.getJSONObject("m2m:cin").getString("con");
                JSONObject data = new JSONObject(dataDevice);
                humibaru = Integer.parseInt(data.get("Kelembapan").toString());
                kgbaru = Integer.parseInt(data.get("Berat").toString());
                hargabaru = Integer.parseInt(data.get("TotalHarga").toString());
                runOnUiThread(() -> {
                    alertDialog.dismiss();
                    tvhargabaru.setText("+ "+formatRupiah.format((double)hargabaru));
                    tvkgbaru.setText("+ "+kgbaru+" KG");
                    tvhumilama.setText(humibaru+" %");
                    getdata.setText("Refresh");
                    update.setBackground(getDrawable(R.drawable.round_green));
                    update.setEnabled(true);
                });
                Log.d(TAG,dataDevice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
