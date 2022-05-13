package com.cansalman.smarttodolist42.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cansalman.smarttodolist42.R;
import com.cansalman.smarttodolist42.adapter.ToDoAdapter;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.FragmentHomeBinding;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.model.UserWithContent;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public int  userId;
    public String password;
    HomeVeiwModel homeVeiwModel;
    ToDoDataBase toDoDataBase;
    ToDoDao toDoDao;
    CompositeDisposable compositeDisposable ;
    ToDoAdapter toDoAdapter ;
    SharedPreferences sharedPreferences;
    List<UserWithContent> userWithContentList;
    Bundle savedInstanceState;
    Boolean calledBoolean;




    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("home fragment called");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeVeiwModel= new ViewModelProvider(requireActivity()).get(HomeVeiwModel.class);
        toDoDataBase = ToDoDataBase.getInstance(requireActivity());
        toDoDao=toDoDataBase.toDoDao();
        compositeDisposable = new CompositeDisposable();
        this.savedInstanceState = savedInstanceState;



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //database'e call atmak icin userId sharedPreferences araciligiyla aliniyor
        sharedPreferences = requireActivity().getSharedPreferences("userId",Context.MODE_PRIVATE);
        int sharedUserId= sharedPreferences.getInt("userId",0);
        String sharedPassword = sharedPreferences.getString("password","test1");

        userId=sharedUserId;
        password=sharedPassword;

        getData(userId);
    }

    public void getData(int userId){

        //Data Access Objects interface'i sayesinde database'den verilen kullaniciya ait butun verileri aliyoruz
        //bu call ise bize  response olarak liste UserWithContent classina ait objelerin oldugu bir liste donduruyor
        userWithContentList = new ArrayList<>();
        userWithContentList=toDoDao.getAll(userId);

        handlerResponse(userWithContentList);
    }

    public void handlerResponse(List<UserWithContent> userWithContentList) {
        this.userWithContentList = userWithContentList;
        int size = userWithContentList.size();
        if(size!=0){

            //recycler view'in layout'u olusuturuluyor ve recyclerView'a tanimlaniyor
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
            binding.mainREcyclerView.setLayoutManager(layoutManager);


            //kullanicagimiz adapter olusturuluyor ve listenecek todolarimizin oldugu liste data olarak veriliyor
            toDoAdapter = new ToDoAdapter(userWithContentList.get(0).contentList,requireContext());
            binding.mainREcyclerView.setAdapter(toDoAdapter);




                // uygulama olusturulduktan sonra veri girisi yapildiginda recycler view'da goruntuleyen kod blogu
                homeVeiwModel.getContent().observe(requireActivity(), new Observer<Content>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(Content content) {
                        try {
                            if (homeVeiwModel.getViewModelBool().getValue()) {

                                UserWithContent addContent = new UserWithContent();
                                addContent.user = userWithContentList.get(0).user;
                                addContent.contentList = userWithContentList.get(0).contentList;
                                addContent.contentList.add(content);
                                int index = userWithContentList.get(0).contentList.size();
                                userWithContentList.add(addContent);
                                toDoAdapter.notifyItemInserted(index);
                                toDoAdapter.notifyDataSetChanged();
                                toDoAdapter = new ToDoAdapter(userWithContentList.get(0).contentList,requireContext());
                                binding.mainREcyclerView.setAdapter(toDoAdapter);

                                homeVeiwModel.setViewMOdelBool(false);

                                 }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                }
                });



        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        compositeDisposable.clear();
    }


}