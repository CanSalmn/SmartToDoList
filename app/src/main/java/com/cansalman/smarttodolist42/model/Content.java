package com.cansalman.smarttodolist42.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Content {

    @PrimaryKey(autoGenerate = true)
    public int contentId ;


    public String content;
    public int userId;
    public int isChecked ;


    public Content(String content, int userId) {
        this.content = content;
        this.userId = userId;

    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }
}
