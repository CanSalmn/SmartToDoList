package com.cansalman.smarttodolist42.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cansalman.smarttodolist42.MainActivity;
import com.cansalman.smarttodolist42.R;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.FragmentRegisterBinding;
import com.cansalman.smarttodolist42.model.User;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Register extends Fragment {

    private FragmentRegisterBinding binding;
    CompositeDisposable compositeDisposable ;
    ToDoDataBase toDoDataBase;
    ToDoDao toDoDao ;

    public Register() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentRegisterBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        compositeDisposable= new CompositeDisposable();
        toDoDataBase= ToDoDataBase.getInstance(requireContext());
        toDoDao= toDoDataBase.toDoDao() ;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existsAccount(v);
            }
        });




        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }




private  void register(){
     String inputUserEmail=binding.inputEmail.getText().toString();
     String inputPassword= binding.inputPassword.getText().toString();
     String  inputMobilPhone= binding.inputMobileNumber.getText().toString();
     String inputName= binding.nameEditText.getText().toString();
    //invalid user

    //yeni user kaydi yapiliyor
    User user    = new User(inputUserEmail,inputPassword,inputName,null,inputMobilPhone);


    //user database kaydediliyor
    compositeDisposable.add(toDoDao.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe());


    //eklenen user'in userId'isini almak icin databaseden veriler cekiliyor
    compositeDisposable.add(toDoDao.getLastAdded(user.userEmail)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handlerGetRecAdded)
    );



}


public void handlerGetRecAdded(List<User> userList){

        String inputPassword= binding.inputPassword.getText().toString();
        int userId = userList.get(0).userId;

        //alinan response'dan userId ve password sharedPreference'a kaydediliyor
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE).edit();
        editor.putInt("userId",userId);
        editor.putString("password",inputPassword);
        editor.apply();
        Intent intent = new Intent(requireContext(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();


        }




    //hesabi varsa eger login'e geri donmek icin kullanilan kod
    private  void existsAccount(View view){
        NavDirections navDirections = RegisterDirections.actionRegisterToLogin();
        Navigation.findNavController(view).navigate(navDirections);

    }
}