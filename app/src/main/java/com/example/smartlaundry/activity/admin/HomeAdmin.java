package com.example.smartlaundry.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.user.ProfileActivity;
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
import static com.example.smartlaundry.utils.Path.NOTIF;
import static com.example.smartlaundry.utils.Path.NaiveBayes;
import static com.example.smartlaundry.utils.Path.TOPUP;
import static com.example.smartlaundry.utils.Path.USERS;

public class HomeAdmin extends AppCompatActivity {
    private static final String TAG = "count data ";
    private AVLoadingIndicatorView avi;
    private Session session;
    private CircleImageView circleImageView;
    private RequestOptions options ;
    private LinearLayout linearLayout;
    private CardView cvprofile,cvpayment,cvlistorder,cvlistbasket;
    private String email,nama,alamat,nohp,photo,uid;
    private  String notifnama,notifbasketpayment,notiftanggal;
    private int notifberat,notifhumidity;
    private int countchild= 0;
    private NotificationManagerCompat notificationManager;
    private TextView tvnama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        //data profile
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.keepSynced(true);
        DatabaseReference userRef = rootRef.child(USERS);
        Query query = userRef.orderByChild("uid").equalTo("nDb8qGUrSxZUPHWPbh4W9I7gVsH2");
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
                }

                session.saveSPString(Session.SP_NAMA,nama);
                session.saveSPString(Session.SP_UID,uid);
                session.saveSPString(Session.SP_ALAMAT,alamat);
                session.saveSPString(Session.SP_NOHP,nohp);
                session.saveSPString(Session.SP_PHOTO,photo);
                session.saveSPString(Session.SP_EMAIL,email);

                avi.smoothToHide();
                linearLayout.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(photo).apply(options).into(circleImageView);
                tvnama.setText(nama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                avi.smoothToHide();
                Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
            }
        });

        //notif data order
        DatabaseReference ready = FirebaseDatabase.getInstance().getReference(NOTIF).child(ByOrder);
        ready.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,String s) {
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.logo2)
                        .setContentTitle("Ada Order Baru ")
                        .setContentText("Periksa Daftar Orderan")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(1, notification);
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
        DatabaseReference basket = FirebaseDatabase.getInstance().getReference(NOTIF).child(NaiveBayes);
        basket.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                            .setSmallIcon(R.drawable.logo2)
                            .setContentTitle("Keranjang "+notiftanggal)
                            .setContentText("Atas Nama " + notifnama+" Siap Dijemput")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();
                    notificationManager.notify(2, notification);

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

        //notif data topup
        DatabaseReference topup = FirebaseDatabase.getInstance().getReference(USERS).child(TOPUP);
        topup.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                notifnama= dataSnapshot.child("nama").getValue(String.class);
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                        .setSmallIcon(R.drawable.logo2)
                        .setContentTitle("Ada Request TopUp ")
                        .setContentText("Atas Nama " + notifnama)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(3, notification);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

        cvprofile.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            CustomIntent.customType(this,"fadein-to-fadeout");
        });
        cvlistbasket.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ListBasketAdmin.class);
            startActivity(intent);
            CustomIntent.customType(this,"left-to-right");
        });

        cvlistorder.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ListOrderAdmin.class);
            startActivity(intent);
            CustomIntent.customType(this,"right-to-left");
        });
        cvpayment.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ListOrderTopup.class);
            startActivity(intent);
            CustomIntent.customType(this,"up-to-bottom");
        });
    }
    private void init() {
        avi = findViewById(R.id.aviadmin);
        session = new Session(this);
        circleImageView = findViewById(R.id.imgprofileadmin);
        linearLayout = findViewById(R.id.llmainadmin);
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.user)
                .error(R.drawable.user);
        cvprofile = findViewById(R.id.goprofileamdin);
        tvnama = findViewById(R.id.tvnameadmin);
        cvpayment = findViewById(R.id.reqtopup);
        cvlistorder = findViewById(R.id.listorderadmin);
        cvlistbasket = findViewById(R.id.listbasketadmin);
        notificationManager = NotificationManagerCompat.from(this);
        avi.smoothToShow();

    }
}
