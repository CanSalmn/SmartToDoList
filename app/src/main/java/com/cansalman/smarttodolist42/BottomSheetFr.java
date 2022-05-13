package com.cansalman.smarttodolist42;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.FragmentBottomSheetBinding;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.ui.home.HomeVeiwModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class BottomSheetFr extends BottomSheetDialogFragment {

    private FragmentBottomSheetBinding binding;
    ToDoDataBase toDoDataBase ;
    ToDoDao toDoDao ;
    CompositeDisposable compositeDisposable;
    public int userId;
    HomeVeiwModel homeVeiwModel;
    SharedPreferences sharedPreferences;

    public BottomSheetFr() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentBottomSheetBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        homeVeiwModel = new ViewModelProvider(requireActivity()).get(HomeVeiwModel.class);

        //eklenecek contentin hangi kullaciya ait oldugunu saptamak ve
        //uzerine yazmak icin user id'i aldigimiz kod blogu
        sharedPreferences = requireActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
        userId= sharedPreferences.getInt("userId",0);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toDoDataBase = ToDoDataBase.getInstance(requireContext());
        toDoDao = toDoDataBase.toDoDao();
        compositeDisposable = new CompositeDisposable();

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(userId);
            }
        });
    }


    public void save(int userId){
        //eklenen contentin database'e kaydi yapiliyor
        String inputContent = binding.contentEditText.getText().toString();
        Content content = new Content(inputContent,userId);

        compositeDisposable.add(toDoDao.insertContent(content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );

        homeVeiwModel.setViewMOdelBool(true);//eklenen todonun app lifecycle'i surecinde tekrar olusmasini onlemek icin
        //duzenlenen bool ifade


        homeVeiwModel.setContent(content);//contentin recycler viewda  gorunmesi icin View model yardimiyla
        //home Fragmenta gonderiliyor

        this.dismiss();//save buttonuna basıldıgında fragmentı kapatmak icin
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}