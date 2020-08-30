package com.example.smartlaundry.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartlaundry.R;
import com.example.smartlaundry.utils.EmailValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ForgotPassword extends AppCompatActivity {
    //membuat variable
    private TextView textViewBack;
    private TextInputEditText tieResetEmail;
    private Button buttonSubmit;
    private FirebaseAuth auth;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //membuat layar menjadi full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //deklarasi
        auth = FirebaseAuth.getInstance();
        tieResetEmail = findViewById(R.id.emailforgot);
        textViewBack = findViewById(R.id.tvback);
        buttonSubmit = findViewById(R.id.btnreset);
        avLoadingIndicatorView = findViewById(R.id.loadingforgot);

        // function kembali
        textViewBack.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
        resetPassword();
    }

    private void resetPassword() {
        buttonSubmit.setOnClickListener(v -> {
            String email = Objects.requireNonNull(tieResetEmail.getText()).toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toasty.warning(getApplication(), "Masukan Email Anda !", Toasty.LENGTH_SHORT).show();
                tieResetEmail.setError("Email Tidak Boleh Kosong");
            }
            else if (!EmailValidator.validate(email)){
                Toasty.warning(getApplicationContext(),
                        "Format Email Salah", Toasty.LENGTH_SHORT).show();
                tieResetEmail.setError("Hindari Karakter $%@+_,");
            }
            else {
                avLoadingIndicatorView.smoothToShow();
                buttonSubmit.setVisibility(View.GONE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                avLoadingIndicatorView.smoothToHide();
                                Toasty.success(getApplicationContext(), "Kami telah mengirimkan instruksi kepada email anda !", Toasty.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }else{
                                avLoadingIndicatorView.smoothToHide();
                                buttonSubmit.setVisibility(View.VISIBLE);
                                Toasty.error(getApplicationContext(), "Email tidak Terdaftar", Toasty.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                    avLoadingIndicatorView.smoothToHide();
                    buttonSubmit.setVisibility(View.VISIBLE);
                    Toasty.error(getApplicationContext(), "Email tidak Valid", Toasty.LENGTH_SHORT).show();
                });
            }

        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "up-to-bottom");
    }
}
