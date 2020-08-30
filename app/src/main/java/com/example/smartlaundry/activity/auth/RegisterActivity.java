package com.example.smartlaundry.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.user.MainActivity;
import com.example.smartlaundry.models.User;
import com.example.smartlaundry.utils.EmailValidator;
import com.example.smartlaundry.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

import static com.example.smartlaundry.utils.Path.USERS;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG ="RgisterActivity" ;
    private Session session;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private TextInputEditText tieRegNama, tieRegNohp,tieRegAlamat, tieRegEmail, tieRegPassword;
    private TextView tvback;
    private Button tombolsubmit;
    private CircleImageView circleImageView;
    private String nama,nohp,alamat,email,foto,password;
    private int saldo = 0;

    private Uri resultUri;
    private StorageReference mStorageReference;
    private AVLoadingIndicatorView loadingsignin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //memberikan identitas
        init();
        // tombol kembali
        tvback.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finishAffinity();
            finish();
        });
        tombolsubmit.setOnClickListener(v->{
            signup();
        });
        mStorageReference = FirebaseStorage.getInstance().getReference();


    }

//    private void ambilgambar() {
//        circleImageView.setOnClickListener(v -> Dexter.withActivity(RegisterActivity.this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        CropImage.activity()
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .start(RegisterActivity.this);
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        if (response.isPermanentlyDenied()) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//                            builder.setTitle("Perizinan Diperlukan")
//                                    .setMessage("Aktifkan Perizinan untuk Mengupload Gambar")
//                                    .setPositiveButton("OK", (dialog, which) -> {
//                                        Intent intent = new Intent();
//                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
//                                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//                                    }).setNegativeButton("Batal", null).show();
//                        }
//
//                    }
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//                }).check());
//    }

    private void init() {
        session = new Session(this);
        mAuth  = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS);
        tieRegNama = findViewById(R.id.nameregis);
        tieRegNohp = findViewById(R.id.nohpregis);
        tieRegAlamat = findViewById(R.id.alamatregis);
        tieRegEmail = findViewById(R.id.emairegis);
        tieRegPassword = findViewById(R.id.pswdregis);
        tvback = findViewById(R.id.tvlogin);
        tombolsubmit = findViewById(R.id.btnsignup);
        loadingsignin = findViewById(R.id.loading);
    }
    private void signup() {
//        if (resultUri!=null){
//            StorageReference storageReference2nd = mStorageReference
//                    .child(STORAGE+System.currentTimeMillis()+".jpg");
            nama = Objects.requireNonNull(tieRegNama.getText()).toString();
            nohp = Objects.requireNonNull(tieRegNohp.getText()).toString();
            alamat = Objects.requireNonNull(tieRegAlamat.getText()).toString();
            email = Objects.requireNonNull(tieRegEmail.getText()).toString();
            password = Objects.requireNonNull(tieRegPassword.getText()).toString();
            foto = "https://firebasestorage.googleapis.com/v0/b/smartlaundry-c169d.appspot.com/o/Image%2Fimages.jpeg?alt=media&token=6375c0c2-5caf-45da-99c2-cb64b1c8bf27";
            if (nama.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Nama Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegNama.setError("Nama Tidak Boleh Kosong!");
            }
            else if (nohp.isEmpty()) {
                Toasty.warning(getApplicationContext(), "No HP Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegNohp.setError("No Hp Tidak Boleh Kosong !");
            }
            else if (alamat.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Alamat Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegAlamat.setError("Alamat Tidak Boleh Kosong !");
            }
            else if (email.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Email Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegEmail.setError("Email Harus Di Isi !");

            }
            else if (!EmailValidator.validate(email)) {
                Toasty.warning(getApplicationContext(), "Email Tidak Valid!", Toasty.LENGTH_SHORT).show();
                tieRegEmail.setError("Format Email Salah !");

            }
            else if (password.isEmpty()) {
                Toasty.warning(getApplicationContext(), "Password Belum Di Isi !", Toasty.LENGTH_SHORT).show();
                tieRegPassword.setError("Password Tidak Boleh Kosong");

            }
            else if (password.length() <= 6) {
                Toasty.warning(getApplicationContext(), "Password Terlalu Pendek !", Toasty.LENGTH_SHORT).show();
                tieRegPassword.setError("Masukan Minimal 6 Karakter");

            }
            else {
                loadingsignin.smoothToShow();
                tombolsubmit.setVisibility(View.GONE);
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(RegisterActivity.this,task -> {
                            if (task.isSuccessful()){
                                loadingsignin.smoothToHide();
                                session.saveSPString(Session.SP_EMAIL,email);
                                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                                String uid = Objects.requireNonNull(user).getUid();
                                User informasi = new User(uid, nama, email, saldo,nohp,alamat,foto);
                                databaseReference.push().setValue(informasi).addOnCompleteListener(task1 -> {
                                    Toasty.success(getApplicationContext(),"Registrasi Berhasil",Toasty.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    finish();
                                });
                            }
                        }).addOnFailureListener(e -> {
                    loadingsignin.smoothToHide();
                    Toasty.error(getApplicationContext(), Objects.requireNonNull(e.getMessage()),Toasty.LENGTH_SHORT).show();
                    tombolsubmit.setVisibility(View.VISIBLE);
                });


            }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "up-to-bottom");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finishAffinity();
        finish();
    }
}
