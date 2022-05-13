package com.cansalman.smarttodolist42;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;
import android.widget.TextView;
import com.cansalman.smarttodolist42.Login.LoginScreen;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.model.UserWithContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.cansalman.smarttodolist42.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity  {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    SharedPreferences sharedPreferences;
    private  View decoreView;
    CompositeDisposable compositeDisposable;
    ToDoDataBase toDoDataBase;
    ToDoDao toDoDao;
    List<UserWithContent> userWithContentList;
    boolean isAllVisible;
    List<Drawable> header_background;





    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        compositeDisposable = new CompositeDisposable();
        toDoDataBase= ToDoDataBase.getInstance(this);
        toDoDao= toDoDataBase.toDoDao();
        header_background= new ArrayList<>();


        //navigation view icin tanimlama ve kullanilacak widgetlari ekleme
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.logout,R.id.dark_mode_switch)
                .setOpenableLayout(drawer)
                .build();

        //gecislerde kullanilacak widgetlari controller ile action vermek
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        BottomNavigationView  navView = binding.appBarMain.bottomNav;
        NavigationUI.setupWithNavController(navView, navController);


        //bottom navigationdaki menu bar ile navgation view'i acmak icin gerekli olan kod
        binding.appBarMain.bottomNav.getMenu().findItem(R.id.navigationMenu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) drawer.openDrawer(GravityCompat.START);
                else drawer.closeDrawer(GravityCompat.END);
                return false;
            }
        });



        //tam ekran
        decoreView  = getWindow().getDecorView();
        decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ( visibility==0){
                    decoreView.setSystemUiVisibility(hideStatusBar());
                }
            }
        });


        //scroll down yapildiginda bottom menu'nun kapanmasi scroll up yapildiginda ise gorunur olmasini saglayan kod
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.appBarMain.bottomNav.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //ilk girildiginde hangi sayfada olacak o ayarlaniyor
        binding.appBarMain.bottomNav.setSelectedItemId(R.id.nav_home);



        //sag kosede bulunan content ekleme butonunun animasyon tanimlari
        isAllVisible=false;
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllVisible){
                    binding.appBarMain.addFloatingButton.show();
                    binding.appBarMain.addTextView.setVisibility(View.VISIBLE);
                    binding.appBarMain.fab.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_open_anim));
                    binding.appBarMain.addFloatingButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.from_bottom_anim));
                    binding.appBarMain.addTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.from_bottom_anim));
                    isAllVisible=true;

                }else {


                    //hepsi acik ve main faba basilinca olucak animasyon
                    binding.appBarMain.addFloatingButton.setVisibility(View.GONE);
                    binding.appBarMain.addTextView.setVisibility(View.INVISIBLE);
                    binding.appBarMain.fab.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close_anim));
                    binding.appBarMain.addFloatingButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_anim));
                    binding.appBarMain.addTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_anim));
                    isAllVisible=false;
                }
            }
        });

        //add butonunun animasyon ve yapilacagi islemlerin tanimlanmasi
        binding.appBarMain.addFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bottom sheet dialogun acilmasi ve bottom Sheet fragment classini calistiran kod
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetFr();
                bottomSheetDialogFragment.show(getSupportFragmentManager(),"Tag");
                isAllVisible=false;


                //hepsi acik ve main faba basilinca olacak animasyon
                binding.appBarMain.fab.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close_anim));
                binding.appBarMain.addFloatingButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_anim));
                binding.appBarMain.addTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_anim));
                binding.appBarMain.addFloatingButton.hide();
                binding.appBarMain.addFloatingButton.setVisibility(View.GONE);
                binding.appBarMain.addTextView.setVisibility(View.INVISIBLE);
            }
        });



        //tekrar giris yapildiginda login ekranina atmamasi icin
        binding.navView.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("called");
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("currentuser",Context.MODE_PRIVATE).edit();
                editor.putBoolean("currentuser",false);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();


                return false;
            }
        });


        binding.navView.getMenu().findItem(R.id.dark_mode_switch).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

            //dark mode code will be here in this field

                return false;
            }
        });


            Resources res = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.header_back,null);
            Drawable drawable1= ResourcesCompat.getDrawable(res,R.drawable.header_back1,null);
            Drawable drawable2= ResourcesCompat.getDrawable(res,R.drawable.header_back2,null);
            Drawable drawable3= ResourcesCompat.getDrawable(res,R.drawable.header_back3,null);
            Drawable drawable4= ResourcesCompat.getDrawable(res,R.drawable.header_back4,null);
            Drawable drawable5= ResourcesCompat.getDrawable(res,R.drawable.header_back5,null);
            Drawable drawable6= ResourcesCompat.getDrawable(res,R.drawable.header_back6,null);
            Drawable drawable7= ResourcesCompat.getDrawable(res,R.drawable.header_back7,null);
            Drawable drawable8= ResourcesCompat.getDrawable(res,R.drawable.header_back8,null);
            Drawable drawable10= ResourcesCompat.getDrawable(res,R.drawable.header_back10,null);
            Drawable drawable11= ResourcesCompat.getDrawable(res,R.drawable.header_back11,null);
            final int random= new Random().nextInt(10);
            header_background.add(drawable);
            header_background.add(drawable1);
            header_background.add(drawable2);
            header_background.add(drawable3);
            header_background.add(drawable4);
            header_background.add(drawable5);
            header_background.add(drawable6);
            header_background.add(drawable7);
            header_background.add(drawable8);
            header_background.add(drawable10);
            header_background.add(drawable11);

            binding.navView.getHeaderView(0).findViewById(R.id.headerConstrain).setBackground(header_background.get(random));




        //header layout icin user name, user e mailinin databaseden alinip ilgili textlerde gosterme bolumu
        sharedPreferences = this.getSharedPreferences("userId",Context.MODE_PRIVATE);
        int userId= sharedPreferences.getInt("userId",0);

        userWithContentList = new ArrayList<>();
        userWithContentList=toDoDao.getAll(userId);
        handlerResponse(userWithContentList);

    }



    public void handlerResponse(List<UserWithContent> userList){
        TextView nameText=binding.navView.getHeaderView(0).findViewById(R.id.navName);
        TextView eMailText= binding.navView.getHeaderView(0).findViewById(R.id.navEmail);


        try {
            nameText.setText(userList.get(0).user.name);
            eMailText.setText(userList.get(0).user.userEmail);

        }catch (Exception e){
            e.printStackTrace();
        }
    }





    //Navigation view icin controllerin appBar ile navigate etme kismi
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




    //tam ekran icin
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decoreView.setSystemUiVisibility(hideStatusBar());
        }
    }

    //tam ekran  gorunum saglamak icin gerekli kod duzenlemesi
    private int hideStatusBar(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
