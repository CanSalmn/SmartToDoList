package com.cansalman.smarttodolist42.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cansalman.smarttodolist42.model.Content;
import com.cansalman.smarttodolist42.model.UserWithContent;

public class HomeVeiwModel extends ViewModel {
    private final MutableLiveData<Content> contentMutableLiveData =new MutableLiveData<>();

    private final MutableLiveData<Boolean> viewModelBoolean =new MutableLiveData<>();



    public void setContent(Content addContent){

        contentMutableLiveData.setValue(addContent);

    }
    public LiveData<Content> getContent(){
        return contentMutableLiveData;
    }




    public void setViewMOdelBool(boolean viewMOdelBool){

        viewModelBoolean.setValue(viewMOdelBool);

    }
    public LiveData<Boolean> getViewModelBool(){
        return viewModelBoolean;
    }





}
