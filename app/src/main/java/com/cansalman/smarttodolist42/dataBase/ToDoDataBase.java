package com.cansalman.smarttodolist42.dataBase;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.model.User;


@Database(entities = {User.class, Content.class},version = 1)
public abstract class ToDoDataBase extends RoomDatabase {
        public abstract ToDoDao toDoDao();



        //relational database olusturuldugundan bu classtan instance olusturarak database erisim saglanir
        //gerekli getter ve sorgular olusturulur
        private static ToDoDataBase INSTANCE;
        public  static ToDoDataBase getInstance(Context context){
                if(INSTANCE ==null){
                        synchronized (ToDoDataBase.class){
                                if(INSTANCE==null){
                                        INSTANCE= Room.databaseBuilder(context,ToDoDataBase.class,"User")
                                                .allowMainThreadQueries()
                                                .build();

                                }
                        }
                }
                return INSTANCE;
        }

}
