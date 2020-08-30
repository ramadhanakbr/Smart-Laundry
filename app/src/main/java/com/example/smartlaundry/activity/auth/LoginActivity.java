package com.example.smartlaundry.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.admin.HomeAdmin;
import com.example.smartlaundry.activity.user.MainActivity;
import com.example.smartlaundry.utils.EmailValidator;
import com.example.smartlaundry.utils.Session;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    private TextView tvregis,tvlupa;
    private TextInputEditText tieloginemail,tieloginpswd;
    private Button buttonLogin;
    private FirebaseAuth auth;
    private Session session;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        //Cek Apakah Sudah ada user yang login
        ceklogin();

        login(); // memanggil method login
        tvlupa.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(),ForgotPassword.class);
            startActivity(intent);
            finish();
        });
        tvregis.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }
    private void init() {
        tvregis = findViewById(R.id.tvregis);
        tvlupa = findViewById(R.id.tvforgot);
        tieloginemail = findViewById(R.id.emailogin);
        tieloginpswd = findViewById(R.id.loginpsswd);
        buttonLogin = findViewById(R.id.btnsignin);
        auth = FirebaseAuth.getInstance();
        avLoadingIndicatorView = findViewById(R.id.loadinglogin);
        session = new Session(this);
    }
    private void login() {
        buttonLogin.setOnClickListener(v -> {
            //Validasi
            final String email = Objects.requireNonNull(tieloginemail.getText()).toString().trim();
            final String password = Objects.requireNonNull(tieloginpswd.getText()).toString().trim();
            session.saveSPString(Session.SP_EMAIL,email);
            if (TextUtils.isEmpty(email)) {
                Toasty.warning(LoginActivity.this,
                        "Anda Belum Mengisi Form Email !",
                        Toasty.LENGTH_SHORT).show();
                tieloginemail.setError("Email Tidak Boleh Kosong");

            }
           else if (!EmailValidator.validate(email)) {
                Toasty.warning(LoginActivity.this,
                        "Terjadi Kesalahan,Periksa Kembali Email Anda",
                        Toasty.LENGTH_SHORT).show();
                tieloginemail.setError("Email Tidak Valid");

            }
           else if (TextUtils.isEmpty(password)) {
                Toasty.warning(LoginActivity.this,
                        "Anda Belum Mengisi Form Password !", Toasty.LENGTH_SHORT).show();
                tieloginpswd.setError("Password Tidak Boleh Kosong");

            }
           else if (password.length() < 6) {
                Toasty.warning(LoginActivity.this,
                        "Password Terlalu Pendek !", Toasty.LENGTH_SHORT).show();
                tieloginpswd.setError("Masukan Password Minimal 6 Karakter");

            }

           else {
               avLoadingIndicatorView.smoothToShow();
               buttonLogin.setVisibility(View.GONE);
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {

                    //jika tugas berhasil
                    if (task.isSuccessful()) {
                        // menghilangkan loading
                        avLoadingIndicatorView.smoothToHide();
                        session.saveSPString(Session.SP_EMAIL,email);
                        Toasty.success(LoginActivity.this, "Login Berhasil", Toasty.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(Objects.requireNonNull(user));
                    } else { //jika tidak berhasil
                        avLoadingIndicatorView.smoothToHide();
                        buttonLogin.setVisibility(View.VISIBLE);
                        Toasty.error(LoginActivity.this,
                                "Login Gagal, Data Tidak Sesuai", Toasty.LENGTH_SHORT).show();

                    }
                });
            }

        });
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser.getUid().equals("nDb8qGUrSxZUPHWPbh4W9I7gVsH2")){
            startActivity(new Intent(LoginActivity.this, HomeAdmin.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            Log.v("DATA", currentUser.getUid());
            session.saveSPString(Session.SP_UID,currentUser.getUid());
            finish();
        }
        else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            Log.v("DATA", currentUser.getUid());

            finish();
        }

    }

    private void ceklogin() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "bottom-to-up");
    }
}
