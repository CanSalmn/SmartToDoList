package com.cansalman.smarttodolist42.dataBase;





import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.model.User;
import com.cansalman.smarttodolist42.model.UserWithContent;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ToDoDao {

    @Insert
    Completable insertContent(Content content);

    @Insert
    Completable insertUser(User user);



    @Query("DELETE FROM Content WHERE contentId = :contentId")
     Completable deleteByContentId(int contentId);




    //Login  fragment'ta user'dan alinan user email ve passwordun gecerli olup olmadiginin sorgusu
    @Query("SELECT * FROM User WHERE userEmail= :userEmail")
    Single<List<User>> getCurrentUser(String userEmail);



    //Home Fragmentta verilen kullaniciya ait butun verileri almamizi saglayan query kismi
    @Transaction
    @Query("SELECT * FROM User WHERE userId =:userId")
    List<UserWithContent> getAll(int userId);






    //Login fragment'ta gecerli kullanicinin userId'sini almak icin kullanilan query
    @Query("SELECT * FROM User WHERE userEmail= :userEmail")
    Flowable<List<User>> getLastAdded(String userEmail);




    //todolarin check veya not chect islemlerini database'te update eden query
    @Query("UPDATE Content SET isChecked= :setCheck WHERE contentId= :contentId ")
    Completable Update(int setCheck, int contentId);



    @Query("UPDATE User SET image= :image WHERE userId= :userId ")
    Completable uploadImage(byte[] image, int userId);
}
