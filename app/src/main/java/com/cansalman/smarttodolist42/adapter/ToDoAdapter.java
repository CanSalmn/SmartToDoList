package com.cansalman.smarttodolist42.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.cansalman.smarttodolist42.R;
import com.cansalman.smarttodolist42.dataBase.ToDoDao;
import com.cansalman.smarttodolist42.dataBase.ToDoDataBase;
import com.cansalman.smarttodolist42.databinding.MainRecyclerViewBinding;
import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.ui.home.HomeVeiwModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoHolder> {

    public List<Content> contentList ;

    CompositeDisposable compositeDisposable ;
    ToDoDataBase toDoDataBase ;
    HomeVeiwModel homeVeiwModel;
    ToDoDao toDoDao;
    List<Drawable> drawableList;
    Context context;



    public ToDoAdapter(List<Content> contentList, Context context) {
        this.contentList = contentList;
        this.context = context;
    }


    @NonNull
    @Override
    public ToDoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainRecyclerViewBinding binding = MainRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);


        compositeDisposable = new CompositeDisposable();
        toDoDataBase = ToDoDataBase.getInstance(parent.getContext());
        toDoDao = toDoDataBase.toDoDao();
        drawableList = new ArrayList<>();
        homeVeiwModel= new ViewModelProvider((FragmentActivity) context).get(HomeVeiwModel.class);




        //listelenecek todolarin background renkleri burada tanimlanir ve listeye ekleniyor
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable1=  context.getResources().getDrawable(R.drawable.home_gradient_one);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable2=  context.getResources().getDrawable(R.drawable.home_gradient_two);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable3=  context.getResources().getDrawable(R.drawable.home_gradient_three);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable4=  context.getResources().getDrawable(R.drawable.home_gradient_four);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable5=  context.getResources().getDrawable(R.drawable.home_gradient_five);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable6=  context.getResources().getDrawable(R.drawable.home_gradient_six);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable7=  context.getResources().getDrawable(R.drawable.home_gradient_seven);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable8=  context.getResources().getDrawable(R.drawable.home_gradient_eight);
        drawableList.add(drawable1);
        drawableList.add(drawable2);
        drawableList.add(drawable3);
        drawableList.add(drawable4);
        drawableList.add(drawable5);
        drawableList.add(drawable6);
        drawableList.add(drawable7);
        drawableList.add(drawable8);

        return new ToDoHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.trashImage.setVisibility(View.INVISIBLE);//cop kutusu gorunumu gizleniyor
        holder.binding.contentTextViewRecyc.setBackground(drawableList.get(position%7));//todolarin backgroundlari ayarlaniyor

        // todolar check edilmismi edilmemis mi kontrolu yapiliyor
        if(contentList.get(position).isChecked==0){
            //normal bir sekilde siralaniyor textleri veriliyor
            holder.binding.contentTextViewRecyc.setEnabled(true);
            holder.binding.contentTextViewRecyc.setChecked(false);
            holder.binding.contentTextViewRecyc.setPaintFlags(holder.binding.contentTextViewRecyc.getPaintFlags()
                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.contentTextViewRecyc.setText(contentList.get(position).content);

        }else{
            //check edilmis ise text'in uzeri isaretlenip check box checki isretleniyor
            holder.binding.contentTextViewRecyc.setText(contentList.get(position).content);
            holder.binding.contentTextViewRecyc.setPaintFlags(holder.binding.contentTextViewRecyc.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.contentTextViewRecyc.setEnabled(false);
            holder.binding.contentTextViewRecyc.setChecked(true);
            holder.binding.contentTextViewRecyc.setBackground(drawableList.get(7));

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todolarimiza tiklandiginda background, text cizilmesi ve check box isaretlenmesi bu kod blogunda
                //gerceklesiyor

                holder.binding.contentTextViewRecyc.toggle();
                //kullanici isaretleme yapilip yapilmadiginin kontrolu yapiliyor
                if(holder.binding.contentTextViewRecyc.isChecked()){
                        holder.binding.contentTextViewRecyc.setPaintFlags(holder.binding.contentTextViewRecyc.getPaintFlags() |
                                Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.binding.contentTextViewRecyc.setEnabled(false);
                        holder.binding.contentTextViewRecyc.setBackground(drawableList.get(7));
                    try{
                        //todo checked edildiyse database update'i yapiliyor
                        contentList.get(position).isChecked=1;
                        compositeDisposable.add(toDoDao.Update(1,contentList.get(position).contentId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe());


                    }catch (Exception e ){
                        e.printStackTrace();
                    }


                }else{
                    holder.binding.contentTextViewRecyc.setPaintFlags(holder.binding.contentTextViewRecyc.getPaintFlags()
                                & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.binding.contentTextViewRecyc.setEnabled(true);
                        holder.binding.contentTextViewRecyc.setBackground(drawableList.get(position%7));


                        try{
                            //todo checked durumundan not checked konumuna geldi ise tekrardan database update'i yapiliyor
                            contentList.get(position).isChecked=0;
                            compositeDisposable.add(toDoDao.Update(0,contentList.get(position).contentId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe()
                            );

                        }catch (Exception e     ){
                            e.printStackTrace();
                        }
                }
            }


        });

        //uzun tiklama yapidiginda delete buttonu,anismasyon ile birlikte gorunur hale asagidaki kod blogunda gerceklesiyor
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(holder.binding.trashImage.getVisibility()==View.INVISIBLE){
                    Animation animation =AnimationUtils.loadAnimation(context,R.anim.dragging_right_xml);
                    holder.binding.contentTextViewRecyc.startAnimation(animation);
                    holder.binding.trashImage.setVisibility(View.VISIBLE);
                    holder.binding.trashImage.setEnabled(true);
                    Animation animation2 =AnimationUtils.loadAnimation(context,R.anim.trash_anim);
                    holder.binding.trashImage.startAnimation(animation2);

                    //delete butonuna basildiginda recyclerviewdan ve databaseden silme islemi yapilan kod blogu
                    holder.binding.trashImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                int contentId= contentList.get(holder.getAdapterPosition()).contentId;
                                compositeDisposable.add(toDoDao.deleteByContentId(contentId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe());
                            }catch (Exception e ){
                                e.printStackTrace();
                            }

                            contentList.get(holder.getAdapterPosition()).isChecked=0;
                            contentList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());

                        }
                    });
                }else if (holder.binding.trashImage.getVisibility()==View.VISIBLE){
                    Animation animation =AnimationUtils.loadAnimation(context,R.anim.dragging_left);
                    holder.binding.contentTextViewRecyc.startAnimation(animation);
                    Animation animation2 =AnimationUtils.loadAnimation(context,R.anim.trash_back_anim);
                    holder.binding.trashImage.startAnimation(animation2);
                    holder.binding.trashImage.setEnabled(false);
                    holder.binding.trashImage.setVisibility(View.GONE);
                    holder.binding.trashImage.setVisibility(View.INVISIBLE);


                }






                return true;
            }
        });





    }

    //olusturdugumuz recycler view kac defa tekrarlayacak onun size'ini burada veriyoruz.
    @Override
    public int getItemCount() {
        return contentList.size();
    }










    //xml'deki gorunumleri tutan class ve yukarida erisim kolayligi olmasi icin
    //o xml classindan bir binding olusuturuyoruz
    protected class ToDoHolder extends RecyclerView.ViewHolder{
        private MainRecyclerViewBinding binding;


        public ToDoHolder(MainRecyclerViewBinding binding ) {
            super(binding.getRoot());
            this.binding= binding;
        }


    }






}

