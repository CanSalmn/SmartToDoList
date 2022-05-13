package com.cansalman.smarttodolist42.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int userId ;

    public String userEmail;
    public String password;
    public String name;
    public byte[] image;
    public String mobileNumber;



    public User(String userEmail,String password,String name,byte[] image,String mobileNumber) {
        this.userEmail = userEmail;
        this.password = password;
        this.name=name;
        this.image = image;
        this.mobileNumber= mobileNumber;

    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
