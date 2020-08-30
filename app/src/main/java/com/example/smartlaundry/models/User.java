package com.example.smartlaundry.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String Uid;
    private String Nama;
    private String Email;
    private int Saldo;
    private String Nohp;
    private String Alamat;
    private String Photo;

    public User(){}

    public User(String uid, String nama, String email, int saldo, String nohp, String alamat, String photo) {
        Uid = uid;
        Nama = nama;
        Email = email;
        Saldo = saldo;
        Nohp = nohp;
        Alamat = alamat;
        Photo = photo;
    }


    private User(Parcel in) {
        Uid = in.readString();
        Nama = in.readString();
        Email = in.readString();
        Saldo = in.readInt();
        Nohp = in.readString();
        Alamat = in.readString();
        Photo = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getSaldo() {
        return Saldo;
    }

    public void setSaldo(int saldo) {
        Saldo = saldo;
    }

    public String getNohp() {
        return Nohp;
    }

    public void setNohp(String nohp) {
        Nohp = nohp;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Uid);
        dest.writeString(Nama);
        dest.writeString(Email);
        dest.writeInt(Saldo);
        dest.writeString(Nohp);
        dest.writeString(Alamat);
        dest.writeString(Photo);
    }
}
