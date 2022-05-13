package com.cansalman.smarttodolist42.Login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.cansalman.smarttodolist42.MainActivity;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.ActivityLoginScreenBinding;
import com.cansalman.smarttodolist42.model.User;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.jvm.internal.Intrinsics;

public class LoginScreen extends AppCompatActivity {
    private ActivityLoginScreenBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }


}