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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cansalman.smarttodolist42.MainActivity;
import com.cansalman.smarttodolist42.R;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.FragmentLoginBinding;
import com.cansalman.smarttodolist42.databinding.FragmentRegisterBinding;
import com.cansalman.smarttodolist42.databinding.LayoutLoginBinding;
import com.cansalman.smarttodolist42.model.User;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    CompositeDisposable compositeDisposable;
    ToDoDataBase toDoDataBase;
    ToDoDao toDoDao;
    String password;
    static SharedPreferences sharedPreferences;
    private String inputEmail;
    private String inputPassword;


    public LoginFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        compositeDisposable = new CompositeDisposable();
        toDoDataBase = ToDoDataBase.getInstance(requireContext());
        toDoDao = toDoDataBase.toDoDao();


        //kullanicin main activitede  onceden giris yapip yapmadıgını tutuat kod
        sharedPreferences = requireContext().getSharedPreferences("currentuser", Context.MODE_PRIVATE);
        boolean currentUser = sharedPreferences.getBoolean("currentuser", false);

        //login olan kullanicinin tekrar giris yapmamasini saglayan kod
        if (currentUser) {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();



        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        binding.registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }


    public void signIn() {

        //user kayitli mi degil mi konstrolu yapiliyor
        inputEmail = binding.editTextEmail.getText().toString();
        inputPassword = binding.editTextPassword.getText().toString();


        //kullanıcının daha once kayıtlı olup olmadıgnını sorgusu yapılıyor
        compositeDisposable.add(toDoDao.getCurrentUser(inputEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerResponse)
        );

    }


    public void handlerResponse(List<User> userList) {


        if (userList.size() != 0 && userList.get(0).password.equals(inputPassword)) {
            SharedPreferences.Editor editor = requireContext().getSharedPreferences("currentuser", Context.MODE_PRIVATE).edit();
            editor.putBoolean("currentuser", true);
            editor.apply();
            // user mevcut ise user id ve passwordu shared preferences'a kaydediliyor
            //valid user
            int userId = userList.get(0).userId;
            editor.putInt("userId", userId);
            editor.putString("password", password);
            editor.apply();

            compositeDisposable.add(toDoDao.getLastAdded(inputEmail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handlerGetRecAdded)
            );


        } else {
            //invalid user pop-up gosterme
            LayoutInflater inflater= getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_message,null);
            layout.findViewById(R.id.toastConstrain);
            Toast toast = new Toast(getContext());
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void handlerGetRecAdded(List<User> userList){

        String inputPassword= binding.editTextPassword.getText().toString();
        int userId = userList.get(0).userId;

        //yeni todolar eklemede hangi usera eklenecegini saptamak icin userId ve password sharedPreference'a kaydediliyor
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("userId", Context.MODE_PRIVATE).edit();
        editor.putInt("userId",userId);
        editor.putString("password",inputPassword);
        editor.apply();

        Intent intent = new Intent(requireContext(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();


    }


    //register fragmenta gecis icin Navigation tanimlanmasi yapiliyor
    private void register(View view){
        NavDirections navDirections =LoginFragmentDirections.actionLoginToRegister();
        Navigation.findNavController(view).navigate(navDirections);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}