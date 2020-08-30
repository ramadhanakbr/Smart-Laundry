package com.example.smartlaundry.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TopUp implements Parcelable {
    private String uid;
    private String nama;
    private String foto;
    private String fotobukti;
    private String key;
    private int saldo;
    private int saldolama;

    public TopUp(){}

    public TopUp(String uid, String nama, String foto, String fotobukti, String key, int saldo, int saldolama) {
        this.uid = uid;
        this.nama = nama;
        this.foto = foto;
        this.fotobukti = fotobukti;
        this.key = key;
        this.saldo = saldo;
        this.saldolama = saldolama;
    }

    protected TopUp(Parcel in) {
        uid = in.readString();
        nama = in.readString();
        foto = in.readString();
        fotobukti = in.readString();
        key = in.readString();
        saldo = in.readInt();
        saldolama = in.readInt();
    }

    public static final Creator<TopUp> CREATOR = new Creator<TopUp>() {
        @Override
        public TopUp createFromParcel(Parcel in) {
            return new TopUp(in);
        }

        @Override
        public TopUp[] newArray(int size) {
            return new TopUp[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFotobukti() {
        return fotobukti;
    }

    public void setFotobukti(String fotobukti) {
        this.fotobukti = fotobukti;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getSaldolama() {
        return saldolama;
    }

    public void setSaldolama(int saldolama) {
        this.saldolama = saldolama;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nama);
        dest.writeString(foto);
        dest.writeString(fotobukti);
        dest.writeString(key);
        dest.writeInt(saldo);
        dest.writeInt(saldolama);
    }
}
