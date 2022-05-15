package com.cansalman.smarttodolist42.ui.Profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ActivityInfoCompat;
import androidx.fragment.app.Fragment;

import com.cansalman.smarttodolist42.BottomNavigationBehavior;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.FragmentProfileBinding;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.model.UserWithContent;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.eazegraph.lib.models.PieModel;

import java.io.ByteArrayOutputStream;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    CompositeDisposable compositeDisposable ;
    ToDoDataBase toDoDataBase;
    ToDoDao toDoDao ;
    SharedPreferences sharedPreferences;
    List<UserWithContent> userWithContentList;
    int checkedTodo=0;
    int unCheckedTodo=0;
    int userId;
    Bitmap convertedImage;

    byte[] imageByte;

    ActivityResultLauncher<Intent>activityResultLauncher;
    ActivityResultLauncher<String > permissionLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        registerLauncher();

        compositeDisposable = new CompositeDisposable();
        toDoDataBase= ToDoDataBase.getInstance(requireContext());
        toDoDao= toDoDataBase.toDoDao();
        return root;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("userId", Context.MODE_PRIVATE);
         userId= sharedPreferences.getInt("userId",0);



            userWithContentList = new ArrayList<>();
            userWithContentList=toDoDao.getAll(userId);




            //total todoları ekranda gostermek icin kullanılan kod
        String numberString = String.valueOf(userWithContentList.get(0).contentList.size());
        binding.detailsInclude.allToDoNumbers.setText(numberString);



        //total todoların hangiler isaretli hangileri degil kontrolu yapılıyor
        for(Content content : userWithContentList.get(0).contentList){
            if(content.isChecked==1){
                checkedTodo++;

            }else{
                unCheckedTodo++;
            }
        }
        binding.detailsInclude.activeTodoNumbers.setText(String.valueOf(unCheckedTodo));
        binding.detailsInclude.finishedToDoNumbers.setText(String.valueOf(checkedTodo));



        binding.detailsInclude.pieChart.addPieSlice(
                new PieModel("Active",unCheckedTodo, Color.parseColor("#FF416C")));
        binding.detailsInclude.pieChart.addPieSlice(
                new PieModel("Finished",checkedTodo,Color.parseColor("#FFA72F")));

        binding.detailsInclude.pieChart.startAnimation();






//*****************************************//

        //daha oncesinde resim yuklenmis ise burda gosteriyoruz
        if(  userWithContentList.get(0).user.image !=null){
                Bitmap imageToBitmap = BitmapFactory.decodeByteArray( userWithContentList.get(0).user.image,0, userWithContentList.get(0).user.image.length);

                binding.profilImageView.setImageBitmap(imageToBitmap);

        }

        binding.profilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });


    }



private void selectImage(View view){
        //resim icin izim isteme
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE )){
                //permission
                Snackbar snackbar =  Snackbar.make(view,"Permission Needed for Gallery ",Snackbar.LENGTH_LONG);
                snackbar.setDuration(10000);
                //snackbar.setAnchorView(binding.detailsInclude.pieChart);
                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                View snackbarLayout = snackbar.getView();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(250, 1450, 150, 0);
                snackbarLayout.setLayoutParams(lp);
                snackbar.setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                });
                snackbar.show();

            }else{
                //permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{

            //izin verilmiş ise galeriye gonderme
            //go to gallery
            Intent intentToGallery= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }


}


    private  void registerLauncher(){

        permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(getContext() , "Permission Needed For Gallery", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //galeriye yonlendirir ve geri donuşlerin kontrolü yapılır
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK){
                    Intent intentFromResponse= result.getData();
                    if(intentFromResponse != null){
                        Uri imageUri= intentFromResponse.getData();



                        //image viewda burada gosteriyoruz
                        try {
                            if(Build.VERSION.SDK_INT >=28 ){
                                ImageDecoder.Source source =ImageDecoder.createSource(requireActivity().getContentResolver(),imageUri);
                                convertedImage =ImageDecoder.decodeBitmap(source);
                                binding.profilImageView.setImageBitmap(convertedImage);

                            }else{
                                convertedImage= MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),imageUri);
                                binding.profilImageView.setImageBitmap(convertedImage);
                            }


                            //data base yuksek boyutlu kayıt yapmamamk icin resimde ölceklendirme yaparak kuculturuyoruz
                            Bitmap smallerImage =makeSmallerImage(convertedImage,300);
                            ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
                            smallerImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
                            byte[] byteArray =byteArrayOutputStream.toByteArray();

                            compositeDisposable.add(toDoDao.uploadImage(byteArray,userId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe());

                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });







    }


    //ölceklendirilmiş bir sekilde kücültme işlemi yapılır
    public Bitmap makeSmallerImage(Bitmap image, int maximumSize)
    {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        compositeDisposable.clear();
    }
}