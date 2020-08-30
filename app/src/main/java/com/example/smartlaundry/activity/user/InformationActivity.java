package com.example.smartlaundry.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smartlaundry.R;
import com.example.smartlaundry.utils.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.USERS;

public class InformationActivity extends AppCompatActivity {

    private TextView tvowner,tvemail,tvalamat,tvtelp;
    private Button btnback;
    private String email,nama,alamat,nohp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        btnback.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finishAffinity();
            finish();
            CustomIntent.customType(this,"up-to-bottom");
        });

    }

    private void init() {
        tvowner = findViewById(R.id.infoowner);
        tvemail = findViewById(R.id.infomail);
        tvalamat = findViewById(R.id.infoalamat);
        tvtelp = findViewById(R.id.infotelp);
        btnback = findViewById(R.id.infoback);

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
                }
                tvowner.setText(nama);
                tvemail.setText(email);
                tvalamat.setText(alamat);
                tvtelp.setText(nohp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toasty.error(getApplicationContext(),databaseError.getMessage(),Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAffinity();
        finish();
        CustomIntent.customType(this,"up-to-bottom");
    }
}