package com.example.smartlaundry.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DataLaundry implements Parcelable {

    private String uid;
    private String alamat;
    private String nama;
    private String nohp;
    private String status;
    private String tanggal;
    private String key;
    private int harga;
    private int berat;
    private int kelembaban;

    public DataLaundry(){}

    public DataLaundry(String uid, String alamat, String nama, String nohp, String status, String tanggal, String key, int harga, int berat, int kelembaban) {
        this.uid = uid;
        this.alamat = alamat;
        this.nama = nama;
        this.nohp = nohp;
        this.status = status;
        this.tanggal = tanggal;
        this.key = key;
        this.harga = harga;
        this.berat = berat;
        this.kelembaban = kelembaban;
    }

    protected DataLaundry(Parcel in) {
        uid = in.readString();
        alamat = in.readString();
        nama = in.readString();
        nohp = in.readString();
        status = in.readString();
        tanggal = in.readString();
        key = in.readString();
        harga = in.readInt();
        berat = in.readInt();
        kelembaban = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(alamat);
        dest.writeString(nama);
        dest.writeString(nohp);
        dest.writeString(status);
        dest.writeString(tanggal);
        dest.writeString(key);
        dest.writeInt(harga);
        dest.writeInt(berat);
        dest.writeInt(kelembaban);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DataLaundry> CREATOR = new Creator<DataLaundry>() {
        @Override
        public DataLaundry createFromParcel(Parcel in) {
            return new DataLaundry(in);
        }

        @Override
        public DataLaundry[] newArray(int size) {
            return new DataLaundry[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public int getKelembaban() {
        return kelembaban;
    }

    public void setKelembaban(int kelembaban) {
        this.kelembaban = kelembaban;
    }
}
