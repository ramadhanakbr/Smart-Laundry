package com.example.smartlaundry.activity.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.smartlaundry.R;
import com.example.smartlaundry.models.TopUp;
import com.example.smartlaundry.utils.Path;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.TOPUP;
import static com.example.smartlaundry.utils.Path.USERS;

public class DetailTopUp extends AppCompatActivity {

    private TextView textViewnama,textViewsaldo;
    private int saldo =0;
    private int saldolama = 0;
    private int hasil;
    private ImageView imgbukti;
    private String Saldobaru;
    private CircleImageView imageprofiletopup;
    private Locale localeID;
    private NumberFormat formatRupiah;
    private Button btnconfirm;
    private RequestOptions options ;
    private DatabaseReference databaseReference,dbuser;
    private Query query,queryuser,query2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_top_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();

    }

    private void init() {
        textViewnama = findViewById(R.id.detailnamatopup);
        textViewsaldo = findViewById(R.id.detailsaldo);
        imgbukti = findViewById(R.id.buktigambar);
        imageprofiletopup = findViewById(R.id.adminimgdetailtopup);
        databaseReference =  FirebaseDatabase.getInstance().getReference(USERS).child(TOPUP);
        dbuser =  FirebaseDatabase.getInstance().getReference(USERS);
        localeID = new Locale("in", "ID");
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading);
        btnconfirm = findViewById(R.id.confirmtopup);
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        Intent intent = getIntent();
        TopUp data = intent.getParcelableExtra(Path.DATA);
        if (data!=null){
            textViewnama.setText(data.getNama());
            saldo = data.getSaldo();
            saldolama = data.getSaldolama();
            query = databaseReference.orderByChild("key").equalTo(data.getKey());
            query2 = databaseReference.orderByChild("uid").equalTo(data.getKey());
            queryuser = dbuser.orderByChild("nama").equalTo(data.getNama());
            textViewsaldo.setText(formatRupiah.format((double)saldo));
            hasil = saldolama+saldo;
            Glide.with(getApplicationContext()).load(data.getFoto()).into(imageprofiletopup);
            Glide.with(getApplicationContext()).load(data.getFotobukti()).apply(options).into(imgbukti);
        }
        btnconfirm.setOnClickListener(v->{

            new AlertDialog.Builder(DetailTopUp.this)
                    .setTitle("Konfirmasi!")
                    .setIcon(R.mipmap.ic_apk)
                    .setMessage("Update Prosess ?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, which) -> {
                        queryuser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    ds.getRef().child("saldo").setValue(hasil);

                                }
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                                            ds.getRef().child("saldoakhir").setValue(hasil);

                                        }
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                    ds.getRef().removeValue();
                                                }
                                                Toasty.success(getApplicationContext(),"Pengisian Saldo Berhasil",Toasty.LENGTH_SHORT).show();
                                                Intent intent1 = new Intent(getApplicationContext(),ListOrderTopup.class);
                                                startActivity(intent1);
                                                finishAffinity();
                                                finish();
                                                CustomIntent.customType(DetailTopUp.this,"bottom-to-up");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    })
                    .setNegativeButton("Tidak", null)
                    .show();

        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(),ListOrderTopup.class);
        startActivity(intent1);
        finishAffinity();
        finish();
        CustomIntent.customType(DetailTopUp.this,"bottom-to-up");
    }
}
