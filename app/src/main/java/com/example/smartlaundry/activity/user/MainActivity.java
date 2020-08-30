package com.example.smartlaundry.activity.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.smartlaundry.R;
import com.example.smartlaundry.utils.Session;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.App.CHANNEL_1_ID;
import static com.example.smartlaundry.utils.App.CHANNEL_2_ID;
import static com.example.smartlaundry.utils.App.CHANNEL_3_ID;
import static com.example.smartlaundry.utils.Path.ByOrder;
import static com.example.smartlaundry.utils.Path.LAUNDRY;
import static com.example.smartlaundry.utils.Path.NaiveBayes;
import static com.example.smartlaundry.utils.Path.USERS;

public class MainActivity extends AppCompatActivity {
    private TextView tvname,tvsaldo;
    private CardView cvprofile,cvpayment,cvlist,cvlistpro,cvgoin;
    private String email,nama,alamat,nohp,photo,uid,stringsaldo;
    private int saldo;
    private AVLoadingIndicatorView avi;
    private Session session;
    private NotificationManagerCompat notificationManager;
    private NumberFormat formatRupiah;
    private Locale localeID;
    private CircleImageView circleImageView;
    private RequestOptions options ;
    private LinearLayout linearLayout;
    private Query query,querybasket,queryorder,querysaldo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        email = session.getSPEmail();
        localeID = new Locale("in", "ID");
        avi.smoothToShow();
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        //Firebase
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        DatabaseReference userRef = rootRef.child(USERS);
        query = userRef.orderByChild("email").equalTo(email);
        querysaldo = userRef.orderByChild("uid").equalTo(session.getSPUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()){
                    email = key.child("email").getValue(String.class);
                    nama = key.child("nama").getValue(String.class);
                    alamat = key.child("alamat").getValue(String.class);
                    nohp = key.child("nohp").getValue(String.class);
                    photo = key.child("photo").getValue(String.class);
                    uid = key.child("uid").getValue(String.class);
                    saldo = Integer.parseInt(Objects.requireNonNull(key.child("saldo").getValue()).toString());
                }
                stringsaldo = String.valueOf(saldo);
                session.saveSPString(Session.SP_NAMA,nama);
                session.saveSPString(Session.SP_UID,uid);
                session.saveSPString(Session.SP_ALAMAT,alamat);
                session.saveSPString(Session.SP_NOHP,nohp);
                session.saveSPString(Session.SP_PHOTO,photo);
                String saldohitung = String.valueOf(saldo);
                session.saveSPString(Session.SP_SALDO_HITUNG,saldohitung);

                avi.smoothToHide();
                linearLayout.setVisibility(View.VISIBLE);
                tvname.setText(nama);
                tvsaldo.setText(formatRupiah.format((double)saldo));
                Glide.with(getApplicationContext()).load(photo).apply(options).into(circleImageView);
                session.saveSPString(Session.SP_SALDO,tvsaldo.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                avi.smoothToHide();
                Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
            }
        });
        

        cvprofile.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
            CustomIntent.customType(this,"left-to-right");
        });
        cvpayment.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), Payment.class);
            startActivity(intent);
            CustomIntent.customType(this,"right-to-left");
        });
        cvlistpro.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MyOrderList.class);
            startActivity(intent);
            CustomIntent.customType(this,"fadein-to-fadeout");
        });
        cvlist.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MyBasketList.class);
            startActivity(intent);
            CustomIntent.customType(this,"fadein-to-fadeout");
        });
        cvgoin.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), InformationActivity.class);
            startActivity(intent);
            CustomIntent.customType(this,"bottom-to-up");
        });

        //notif data order
        DatabaseReference ready = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(ByOrder);
        queryorder = ready.orderByChild("uid").equalTo(session.getSPUid());
        queryorder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String notiftanggal= dataSnapshot.child("tanggal").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                if (Objects.equals(status, "Jemput Pakaian")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Orderan "+notiftanggal)
                            .setContentText("Dalam Tahap Penjemputan")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(1, notification);
                }
                if (Objects.equals(status, "Pencucian")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Orderan "+notiftanggal)
                            .setContentText("Dalam Tahap " +status)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(1, notification);
                }
                if (Objects.equals(status, "Selesai")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Orderan "+notiftanggal)
                            .setContentText(status +" Dilaundry")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(1, notification);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //notif data keranjang
        DatabaseReference basket = FirebaseDatabase.getInstance().getReference(LAUNDRY).child(NaiveBayes);
        querybasket = basket.orderByChild("uid").equalTo(session.getSPUid());
        querybasket.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String notiftanggal= dataSnapshot.child("tanggal").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                if (Objects.equals(status, "Jemput Pakaian")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Keranjang "+notiftanggal)
                            .setContentText("Dalam Tahap Penjemputan")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(2, notification);
                }
                if (Objects.equals(status, "Pencucian")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Keranjang "+notiftanggal)
                            .setContentText("Dalam Tahap " +status)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(2, notification);
                }
                if (Objects.equals(status, "Selesai")){
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Keranjang "+notiftanggal)
                            .setContentText(status +" Dilaundry")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(2, notification);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        tvname = findViewById(R.id.tvname);
        tvsaldo = findViewById(R.id.tvsaldo);
        session = new Session(this);
        linearLayout = findViewById(R.id.llmain);
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.user)
                .error(R.drawable.user);
        circleImageView = findViewById(R.id.imgprofile);
        avi = findViewById(R.id.avi);
        cvprofile = findViewById(R.id.goprofile);
        cvpayment = findViewById(R.id.golaundry);
        cvlist =findViewById(R.id.gopament);
        cvgoin = findViewById(R.id.goin);
        cvlistpro = findViewById(R.id.golaundrylist);
        notificationManager = NotificationManagerCompat.from(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
