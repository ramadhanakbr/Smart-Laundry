package com.example.smartlaundry.activity.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smartlaundry.R;
import com.example.smartlaundry.models.DataLaundry;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import id.co.telkom.iot.AntaresHTTPAPI;
import id.co.telkom.iot.AntaresResponse;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.ByOrder;
import static com.example.smartlaundry.utils.Path.LAUNDRY;
import static com.example.smartlaundry.utils.Path.NOTIF;
import static com.example.smartlaundry.utils.Path.NaiveBayes;
import static com.example.smartlaundry.utils.Path.USERS;

public class Payment extends AppCompatActivity implements AntaresHTTPAPI.OnResponseListener {

    private static final String TAG = "Respon Antares";
    private Session session;
    private TextView tvalamat,tvnama,tvtelp,tvberat,tvlembab,tvharga;
    private int responlembab,responharga,responberat;
    private String dataDevice;
    private Locale localeID;
    private Button refresh,submit;
    private NumberFormat formatRupiah;
    private AntaresHTTPAPI antaresAPIHTTP;
    private DatabaseReference dbRef,dbRef2,dbnotiforder,dbnotifkeranjang;
    private android.app.AlertDialog alertDialog;
    private CircleImageView circleImageView;
    private String currentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        // setinfo
        init();
        //getdata
        refresh.setOnClickListener(v ->{
            alertDialog.show();
            antaresAPIHTTP.getLatestDataofDevice(" ad45eb67934eb34f:92afc1fdefa0ff55","SmartLaundry","laundrybox");
        });

        submit.setOnClickListener(v->{
            //memanggil dialog
            Dialog dialogconfirmpayment = new Dialog(this);
            dialogconfirmpayment.setContentView(R.layout.alert_dialog_order);
            dialogconfirmpayment.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogconfirmpayment.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT
                    , WindowManager.LayoutParams.WRAP_CONTENT);
            dialogconfirmpayment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogconfirmpayment.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialogconfirmpayment.show();
            TextView saldobefor,saldoafter,price;
            int before = Integer.parseInt(session.getSpSaldoHitung());
            int hitung = before - responharga;

            Button btnconfirm,btncancel,btnbasket;
            saldoafter = dialogconfirmpayment.findViewById(R.id.saldoafter);
            saldobefor = dialogconfirmpayment.findViewById(R.id.saldobefore);
            price = dialogconfirmpayment.findViewById(R.id.totalprice);
            btncancel = dialogconfirmpayment.findViewById(R.id.cancelpay);
            btnconfirm = dialogconfirmpayment.findViewById(R.id.submitpay);
            btnbasket = dialogconfirmpayment.findViewById(R.id.submitbasket);
            //cancel
            btncancel.setOnClickListener(v1 -> dialogconfirmpayment.dismiss());

            //basket
            btnbasket.setOnClickListener(v13 -> {
                String status = "Belum Dibayar";
                new AlertDialog.Builder(Payment.this)
                        .setTitle("Konfirmasi!")
                        .setIcon(R.mipmap.ic_apk)
                        .setMessage("Masukkan dalam keranjang cucian ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            DataLaundry postdata = new DataLaundry();
                            postdata.setAlamat(session.getSpAlamat());
                            postdata.setBerat(responberat);
                            postdata.setHarga(responharga);
                            String key = dbRef2.push().getKey();
                            postdata.setKey(key);
                            postdata.setKelembaban(responlembab);
                            postdata.setNohp(session.getSpNohp());
                            postdata.setStatus(status);
                            postdata.setUid(session.getSPUid());
                            postdata.setNama(session.getSPNama());
                            postdata.setTanggal(currentDate);
                            alertDialog.show();
                            dbnotifkeranjang.setValue(postdata);
                            dbRef2.push().setValue(postdata).addOnSuccessListener(aVoid -> {
                                alertDialog.dismiss();
                                dialogconfirmpayment.dismiss();
                                Toasty.success(getApplicationContext(),"Simpan Basket Berhasil",Toasty.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                finish();
                                CustomIntent.customType(Payment.this,"left-to-right");
                            })
                                    .addOnFailureListener(aVoid -> {
                                        alertDialog.dismiss();
                                        Toasty.error(getApplicationContext(), Objects.requireNonNull(aVoid.getMessage()), Toasty.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Tidak", null)
                        .show();

            });
            //Perhitungan
            saldobefor.setText(session.getSpSaldo());
            price.setText(tvharga.getText().toString());
            if (hitung<0){
                saldoafter.setText(formatRupiah.format((double)hitung));
                saldoafter.setTextColor(getColor(R.color.red));

            }else {
                saldoafter.setText(formatRupiah.format((double)hitung));
                saldoafter.setTextColor(getColor(R.color.green));
                btnconfirm.setBackground(getDrawable(R.drawable.round_green));
                btnconfirm.setEnabled(true);
            }

            //tombolconfirm
            btnconfirm.setOnClickListener(v12 -> {
                String status = "Sudah Dibayar";
                DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference(USERS);
                Query query = dbuser.orderByChild("uid").equalTo(session.getSPUid());
                DataLaundry postdata = new DataLaundry();
                postdata.setAlamat(session.getSpAlamat());
                postdata.setBerat(responberat);
                String key = dbRef.push().getKey();
                postdata.setKey(key);
                postdata.setHarga(responharga);
                postdata.setKelembaban(responlembab);
                postdata.setNohp(session.getSpNohp());
                postdata.setStatus(status);
                postdata.setUid(session.getSPUid());
                postdata.setNama(session.getSPNama());
                postdata.setTanggal(currentDate);
                new AlertDialog.Builder(Payment.this)
                        .setTitle("Konfirmasi!")
                        .setIcon(R.mipmap.ic_apk)
                        .setMessage("Lakukan Pembayaran ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            alertDialog.show();
                            dbnotiforder.setValue(postdata);
                            dbRef.push().setValue(postdata).addOnSuccessListener(aVoid ->
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds :dataSnapshot.getChildren()){
                                                //update Data
                                                ds.getRef().child("saldo").setValue(hitung);
                                            }
                                            alertDialog.dismiss();
                                            dialogconfirmpayment.dismiss();
                                            Toasty.success(getApplicationContext(),"Pemesanan Berhasil",Toasty.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                            finish();
                                            CustomIntent.customType(Payment.this,"left-to-right");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            alertDialog.dismiss();
                                            Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
                                        }
                                    }))
                                    .addOnFailureListener(aVoid->{
                                        alertDialog.dismiss();
                                        Toasty.error(getApplicationContext(), Objects.requireNonNull(aVoid.getMessage()),Toasty.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            });
        });

    }

    private void init() {
        tvalamat = findViewById(R.id.alamatlaundry);
        tvnama = findViewById(R.id.namalaundry);
        tvtelp = findViewById(R.id.telplaundry);
        tvberat = findViewById(R.id.beratlaundry);
        tvlembab = findViewById(R.id.humilaundry);
        tvharga = findViewById(R.id.hargalaundry);
        circleImageView = findViewById(R.id.laundryprofile);
        localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        session = new Session(this);
        refresh = findViewById(R.id.refreshlaundry);
        submit = findViewById(R.id.submitlaundry);
        antaresAPIHTTP = new AntaresHTTPAPI();
        antaresAPIHTTP.addListener(this);
        alertDialog =
                new SpotsDialog.Builder().setContext(Payment.this).build();
        alertDialog.setMessage("Get Data");
        alertDialog.setIcon(R.mipmap.ic_apk);

        tvalamat.setText(session.getSpAlamat());
        tvnama.setText(session.getSPNama());
        tvtelp.setText(session.getSpNohp());
        dbRef = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(ByOrder);
        dbRef2 = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(NaiveBayes);
        dbnotiforder = FirebaseDatabase.getInstance().getReference(NOTIF).child(ByOrder);
        dbnotifkeranjang = FirebaseDatabase.getInstance().getReference(NOTIF).child(NaiveBayes);

        Glide.with(getApplicationContext()).load(session.getSpPhoto()).into(circleImageView);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finishAffinity();
        finish();
        CustomIntent.customType(this,"left-to-right");

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
                responlembab = Integer.parseInt(data.get("Kelembapan").toString());
                responberat = Integer.parseInt(data.get("Berat").toString());
                responharga = Integer.parseInt(data.get("TotalHarga").toString());
                runOnUiThread(() -> {
                    alertDialog.dismiss();
                    tvharga.setText(formatRupiah.format((double)responharga));
                    tvberat.setText(responberat+" Gram");
                    tvlembab.setText(responlembab+" %");
                    refresh.setText("Refresh");
                    submit.setBackground(getDrawable(R.drawable.round_green));
                    submit.setEnabled(true);
                });
                Log.d(TAG,dataDevice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
