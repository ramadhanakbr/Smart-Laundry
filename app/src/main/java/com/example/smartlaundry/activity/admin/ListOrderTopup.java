package com.example.smartlaundry.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.user.DetailUser;
import com.example.smartlaundry.activity.user.MainActivity;
import com.example.smartlaundry.adapter.TopUpAdapter;
import com.example.smartlaundry.models.TopUp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.DATA;
import static com.example.smartlaundry.utils.Path.TOPUP;
import static com.example.smartlaundry.utils.Path.USERS;

public class ListOrderTopup extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<TopUp> topUps;
    private TopUpAdapter topUpAdapter;
    private DatabaseReference rootRef;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order_topup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        avLoadingIndicatorView = findViewById(R.id.avilisttopupadmin);
        rootRef = FirebaseDatabase.getInstance().getReference(USERS).child(TOPUP);
        rootRef.keepSynced(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView = findViewById(R.id.rcvtopupadmin);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
    }
    @Override
    protected void onStart() {
        super.onStart();
        avLoadingIndicatorView.smoothToShow();
        if (rootRef!=null){
            rootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        topUps = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            topUps.add(ds.getValue(TopUp.class));
                        }
                        topUpAdapter = new TopUpAdapter(ListOrderTopup.this,topUps);
                        recyclerView.setAdapter(topUpAdapter);
                        topUpAdapter.notifyDataSetChanged();
                        avLoadingIndicatorView.smoothToHide();
                        topUpAdapter.setOnItemClickListener(position -> {
                            Intent intent = new Intent(getApplicationContext(), DetailTopUp.class);
                            intent.putExtra(DATA,topUps.get(position));
                            startActivity(intent);
                        });
                    } else {
                        avLoadingIndicatorView.smoothToHide();
                        Toasty.info(getApplicationContext(),"Data Tidak Ditemukan",Toasty.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toasty.error(getApplicationContext(),""+databaseError.getMessage(),Toasty.LENGTH_LONG).show();
                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ListOrderTopup.this, HomeAdmin.class);
        startActivity(intentHome);
        finishAffinity();
        finish();
        CustomIntent.customType(this,"bottom-to-up");
    }
}
