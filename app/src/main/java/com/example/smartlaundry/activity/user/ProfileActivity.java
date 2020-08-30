package com.example.smartlaundry.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.admin.HomeAdmin;
import com.example.smartlaundry.activity.auth.LoginActivity;
import com.example.smartlaundry.models.TopUp;
import com.example.smartlaundry.utils.Session;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.STORAGE;
import static com.example.smartlaundry.utils.Path.TOPUP;
import static com.example.smartlaundry.utils.Path.USERS;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String getemail,getsaldo,getnama,getalamat,getnohp,getfoto;
    private TextView tvnama,tvalamat,tvsaldo,tvhp,tvemail;
    private Button btnSigout,btnEdit,btntopup;
    private CircleImageView circleImageView,editfoto;
    private ImageView bukti;
    private Uri resultUri,resultbukti;
    private Session session;
    private int topup=0;
    private Query query;
    private StorageReference mStorageReference;
    private DatabaseReference databaseReference,databaseReference2;
    private final int PICK_IMAGE_REQUEST = 5;
    private String uid;
    private LinearLayout linearLayout,linearLayoutbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        uid = session.getSPUid();
        if (uid.equals("nDb8qGUrSxZUPHWPbh4W9I7gVsH2")){
            linearLayout.setVisibility(View.GONE);
            linearLayoutbutton.setWeightSum(2);
            btntopup.setVisibility(View.GONE);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS);
        databaseReference2 = FirebaseDatabase.getInstance().getReference(USERS).child(TOPUP);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        query = databaseReference.orderByChild("uid").equalTo(session.getSPUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot newsnapshot : dataSnapshot.getChildren()){
                    getalamat = newsnapshot.child("alamat").getValue(String.class);
                    getnama = newsnapshot.child("nama").getValue(String.class);
                    getnohp = newsnapshot.child("nohp").getValue(String.class);
                    getfoto = newsnapshot.child("photo").getValue(String.class);
                }
                tvnama.setText(getnama);
                tvalamat.setText(getalamat);
                tvhp.setText(getnohp);
                Glide.with(getApplicationContext()).load(session.getSpPhoto()).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSigout.setOnClickListener(v -> new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("SignOut Akun!")
                .setIcon(R.mipmap.ic_apk)
                .setMessage("Apakah Anda Yakin Ingin Keluar Akun ?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> ProfileActivity.this.auth.signOut())
                .setNegativeButton("Tidak", null)
                .show());

        btnEdit.setOnClickListener(v -> {
            final Dialog dialoEdit = new Dialog(this);
            dialoEdit.setContentView(R.layout.alert_dialog_edit);
            dialoEdit.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialoEdit.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT
                    , WindowManager.LayoutParams.WRAP_CONTENT);
            dialoEdit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialoEdit.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialoEdit.show();
            EditText editTextNama,editAlamat,editTelop;

            editTextNama = dialoEdit.findViewById(R.id.edtnama);
            editfoto = dialoEdit.findViewById(R.id.gantigambar);
            Glide.with(ProfileActivity.this).load(session.getSpPhoto()).into(editfoto);
            editAlamat = dialoEdit.findViewById(R.id.edtalamat);
            editTelop = dialoEdit.findViewById(R.id.edttelp);
            editTextNama.setText(tvnama.getText().toString());
            editAlamat.setText(tvalamat.getText().toString());
            editTelop.setText(tvhp.getText().toString());
            Button ambil = dialoEdit.findViewById(R.id.ambilgambar);
            ambil.setOnClickListener(v3 -> Dexter.withActivity(ProfileActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(ProfileActivity.this);
                        }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setTitle("Perizinan Diperlukan")
                                        .setMessage("Aktifkan Perizinan untuk Mengupload Gambar")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                        }).setNegativeButton("Batal", null).show();
                            }

                        }
                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check());

            Button buttonBack = dialoEdit.findViewById(R.id.canceledit);
            buttonBack.setOnClickListener(v1 -> dialoEdit.dismiss());

            Button buttonSubmit = dialoEdit.findViewById(R.id.submitedit);
            buttonSubmit.setOnClickListener(v12 -> {
                getnama = editTextNama.getText().toString();
                getnohp = editTelop.getText().toString();
                getalamat = editAlamat.getText().toString();
                if (resultUri!=null){
                    if (getnama == null){
                        Toasty.warning(getApplicationContext(),"Nama Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    }
                    else if (getnohp == null){
                        Toasty.warning(getApplicationContext(),"NoHP Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    }
                    else if (getalamat.equals("")){
                        Toasty.warning(getApplicationContext(),"Alamat Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    } else {
                        String imageName = System.currentTimeMillis()+".jpeg";
                        //refrensi penyimpanan
                        android.app.AlertDialog alertDialog =
                                new SpotsDialog.Builder().setContext(ProfileActivity.this).build();
                        alertDialog.setMessage("Update Data");
                        alertDialog.setIcon(R.mipmap.ic_apk);
                        alertDialog.show();

                        StorageReference storageReference2 = mStorageReference.child(STORAGE+imageName);
                        UploadTask uploadTask = storageReference2.putFile(resultUri);
                        uploadTask.continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return storageReference2.getDownloadUrl();
                        }).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Uri downloaduri = task.getResult();
                                String upload = Objects.requireNonNull(downloaduri).toString();
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds :dataSnapshot.getChildren()){
                                            //update Data
                                            ds.getRef().child("nama").setValue(getnama);
                                            ds.getRef().child("nohp").setValue(getnohp);
                                            ds.getRef().child("alamat").setValue(getalamat);
                                            ds.getRef().child("photo").setValue(upload);
                                        }
                                        alertDialog.dismiss();
                                        Toasty.success(ProfileActivity.this,"Data Berhasil di Update",Toasty.LENGTH_LONG).show();
                                        dialoEdit.dismiss();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        alertDialog.dismiss();
                                        Toasty.error(ProfileActivity.this,""+databaseError.getMessage(),Toasty.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
                else {
                    if (getnama == null){
                        Toasty.warning(getApplicationContext(),"Nama Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    }
                    else if (getnohp == null){
                        Toasty.warning(getApplicationContext(),"NoHP Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    }
                    else if (getalamat.equals("")){
                        Toasty.warning(getApplicationContext(),"Alamat Tidak Boleh Kosong",Toasty.LENGTH_SHORT).show();
                    }
                    else {
                        android.app.AlertDialog alertDialog =
                                new SpotsDialog.Builder().setContext(ProfileActivity.this).build();
                        alertDialog.setMessage("Update Data");
                        alertDialog.setIcon(R.mipmap.ic_apk);
                        alertDialog.show();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds :dataSnapshot.getChildren()){
                                    //update Data
                                    ds.getRef().child("nama").setValue(getnama);
                                    ds.getRef().child("nohp").setValue(getnohp);
                                    ds.getRef().child("alamat").setValue(getalamat);
                                }
                                alertDialog.dismiss();
                                Toasty.success(ProfileActivity.this,"Data Berhasil di Update",Toasty.LENGTH_LONG).show();
                                dialoEdit.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                alertDialog.dismiss();
                                Toasty.error(ProfileActivity.this,""+databaseError.getMessage(),Toasty.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            });
        });
        btntopup.setOnClickListener(v->{
            Dialog dialogTopup = new Dialog(this);
            dialogTopup.setContentView(R.layout.alert_dialog_topup);
            dialogTopup.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialogTopup.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT
                    , WindowManager.LayoutParams.WRAP_CONTENT);
            dialogTopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogTopup.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialogTopup.show();
            Button btncancel,btnsubmit,btnupload;
            RadioButton radio25,radio50,raido75,radio100;
            radio25 = dialogTopup.findViewById(R.id.dualima);
            radio50 = dialogTopup.findViewById(R.id.gocap);
            raido75 = dialogTopup.findViewById(R.id.tujuhlima);
            radio100 = dialogTopup.findViewById(R.id.cepe);

            bukti = dialogTopup.findViewById(R.id.gambarbukti);
            btncancel = dialogTopup.findViewById(R.id.canceltopup);
            btnsubmit = dialogTopup.findViewById(R.id.submittopup);
            btnupload = dialogTopup.findViewById(R.id.uploadbukti);

            //cancel
            btncancel.setOnClickListener(v13 -> {
                dialogTopup.dismiss();
            });

            btnupload.setOnClickListener(v3 -> Dexter.withActivity(ProfileActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            ImagePicker.Companion.with(ProfileActivity.this)
                                    .crop()	    			//Crop image(Optional), Check Customization for more option
                                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                                    .start();
                        }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                builder.setTitle("Perizinan Diperlukan")
                                        .setMessage("Aktifkan Perizinan untuk Mengupload Gambar")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                        }).setNegativeButton("Batal", null).show();
                            }

                        }
                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check());

            btnsubmit.setOnClickListener(v15->{
                if (resultbukti!=null){
                    if (radio25.isChecked()){
                        topup = 25000;
                    }
                    if (radio50.isChecked()){
                        topup = 50000;
                    }
                    if (raido75.isChecked()){
                        topup = 75000;
                    }
                    if (radio100.isChecked()){
                        topup = 100000;
                    }
                    android.app.AlertDialog alertDialog =
                            new SpotsDialog.Builder().setContext(ProfileActivity.this).build();
                    alertDialog.setMessage("Mohon Tunggu");
                    alertDialog.setIcon(R.mipmap.ic_apk);
                    alertDialog.show();
                    String imageName = System.currentTimeMillis()+".jpeg";
                    StorageReference storageReference2 = mStorageReference.child(STORAGE+imageName);
                    UploadTask uploadTask = storageReference2.putFile(resultbukti);
                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return storageReference2.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            String key =  databaseReference2.push().getKey();
                            String nominal = session.getSpSaldoHitung();
                            int saldo = Integer.parseInt(nominal);
                            Uri downloaduri = task.getResult();
                            String upload = Objects.requireNonNull(downloaduri).toString();
                            TopUp topUp = new TopUp();
                            topUp.setFotobukti(upload);
                            topUp.setFoto(session.getSpPhoto());
                            topUp.setUid(session.getSPUid());
                            topUp.setNama(session.getSPNama());
                            topUp.setSaldo(topup);
                            topUp.setKey(key);
                            topUp.setSaldolama(saldo);
                            databaseReference2.push().setValue(topUp).addOnSuccessListener(aVoid -> {
                                alertDialog.dismiss();
                                Toasty.success(getApplicationContext(),"Top Up Berhasil,Menunggu Konfirmasi Admin",Toasty.LENGTH_SHORT).show();
                                dialogTopup.dismiss();

                            }).addOnFailureListener(e -> {
                                Toasty.error(getApplicationContext(), Objects.requireNonNull(e.getMessage()),Toasty.LENGTH_SHORT).show();
                            });


                        }
                    });
                }
                else {
                    Toasty.warning(getApplicationContext(),"GAMBAR BELUM DI LAMPIRKAN",Toasty.LENGTH_SHORT).show();
                }
            });
        });
        // session login
        auth = FirebaseAuth.getInstance();
        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finishAffinity();
                finish();
                CustomIntent.customType(this,"fadein-to-fadeout");
            }
        };
    }

    private void init() {
        tvnama = findViewById(R.id.profilenama);
        tvalamat = findViewById(R.id.alamatprofile);
        tvsaldo = findViewById(R.id.profilesaldo);
        btntopup = findViewById(R.id.btntopup);
        tvhp = findViewById(R.id.profilehp);
        circleImageView = findViewById(R.id.gambardetail);
        tvemail = findViewById(R.id.profileemail);
        btnEdit = findViewById(R.id.edtprofile);
        linearLayoutbutton = findViewById(R.id.llbutton);
        btnSigout = findViewById(R.id.signout);
        session = new Session(this);
        tvnama.setText(session.getSPNama());
        tvsaldo.setText(session.getSpSaldo());
        linearLayout = findViewById(R.id.llsaldo);
        tvhp.setText(session.getSpNohp());
        tvalamat.setText(session.getSpAlamat());
        tvemail.setText(session.getSPEmail());
        Glide.with(getApplicationContext()).load(session.getSpPhoto()).into(circleImageView);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    protected void onStop() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                editfoto.setImageURI(resultUri);
            }
        }
        else if (resultCode == Activity.RESULT_OK){
            resultbukti = data.getData();
            bukti.setImageURI(resultbukti);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (uid.equals("nDb8qGUrSxZUPHWPbh4W9I7gVsH2")){
            Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
            startActivity(intent);
            finishAffinity();
            finish();
            CustomIntent.customType(this,"right-to-left");
        }else {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finishAffinity();
            finish();
            CustomIntent.customType(this,"right-to-left");
        }

    }
}
