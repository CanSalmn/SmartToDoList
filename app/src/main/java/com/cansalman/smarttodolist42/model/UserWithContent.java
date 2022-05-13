package com.cansalman.smarttodolist42.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;
@Entity
public class UserWithContent {


    @Embedded
    public  User user;
    @Relation(
            parentColumn ="userId",
            entityColumn = "userId"
    )
    public List<Content> contentList;

    //user ve content classlarini mergeleme isleminin yapildigi class
    //user ile content relation'i bu classta yapiliyor

}
