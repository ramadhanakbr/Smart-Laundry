package com.example.smartlaundry.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    public static final String SP_MAHASISWA_APP = "spMahasiswaApp";

    public static final String SP_NAMA_KARYAWAN = "spNamaKaryawan";
    public static final String SP_NAMA = "spNama";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_NOHP ="spNik";
    public static final String SP_UID ="spUid";
    public static final String SP_ALAMAT ="spLevel";
    public static final String SP_SUDAH_LOGIN = "spSudahLogin";
    public static final String SP_PHOTO ="spTema";
    public static final String SP_STATUS ="spStatus";
    public static final String SP_SALDO ="spSaldo";
    public static final String SP_SALDO_HITUNG = "spSaldoHitung";


    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @SuppressLint("CommitPrefEdits")
    public Session(Context context){
        sp = context.getSharedPreferences(SP_MAHASISWA_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPUid(){
        return sp.getString(SP_UID, "");
    }
    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }
    public String getSpNamaKaryawan(){
        return sp.getString(SP_NAMA_KARYAWAN, "");
    }
    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }
    public String getSpNohp(){ return sp.getString(SP_NOHP,"");}
    public String getSpPhoto(){ return sp.getString(SP_PHOTO,"");}
    public String getSpStatus(){ return sp.getString(SP_STATUS,"");}
    public String getSpAlamat(){ return sp.getString(SP_ALAMAT,"");}
    public String getSpSaldo(){return sp.getString(SP_SALDO,"");}
    public String getSpSaldoHitung(){return sp.getString(SP_SALDO_HITUNG,"");}
    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
