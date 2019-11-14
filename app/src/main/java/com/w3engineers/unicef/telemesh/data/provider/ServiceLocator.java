package com.w3engineers.unicef.telemesh.data.provider;

import android.app.Application;
import android.support.annotation.NonNull;

import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.data.local.BaseMeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsViewModel;
import com.w3engineers.unicef.telemesh.ui.chat.ChatViewModel;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserViewModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivityViewModel;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.telemesh.ui.profilechoice.ProfileChoiceViewModel;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashViewModel;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileViewModel;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class ServiceLocator extends BaseServiceLocator {

    // SingleTon
    protected ServiceLocator(){

    }

    // to manage proper singleton that will remained singleton during multi-thread instance
    public static class ServiceLocatorHolder{
        @NonNull
        public static ServiceLocator serviceLocator = new ServiceLocator();
    }

    @NonNull
    public static ServiceLocator getInstance() {

        return ServiceLocatorHolder.serviceLocator;
    }

    @NonNull
    public SplashViewModel getSplashViewModel(@NonNull Application application) {
        return new SplashViewModel(application);
    }

    @NonNull
    public AboutUsViewModel getAboutUsViewModel(@NonNull Application application) {
        return new AboutUsViewModel(application);
    }

    @NonNull
    public UserProfileViewModel getUserProfileViewModel(@NonNull Application application) {
        return new UserProfileViewModel(application);
    }

    @NonNull
    public CreateUserViewModel getCreateUserViewModel(@NonNull Application application) {
        return new CreateUserViewModel(application);
    }

    @NonNull
    public MainActivityViewModel getMainActivityViewModel(){
        restartRmService();
        return new MainActivityViewModel();
    }

    @NonNull
    public SettingsViewModel getSettingsViewModel(@NonNull Application application){
        return new SettingsViewModel(application);
    }

    @NonNull
    public MeshContactViewModel getMeshContactViewModel() {
        return new MeshContactViewModel(UserDataSource.getInstance());
    }

    @NonNull
    public MessageFeedViewModel getMessageFeedViewModel() {
        return new MessageFeedViewModel(FeedDataSource.getInstance());
    }

    @NonNull
    public ChatViewModel getChatViewModel(@NonNull Application application){
        return new ChatViewModel(application);
    }

    @NonNull
    public ProfileChoiceViewModel getProfileChoiceViewModel(@NonNull Application application) {
        return new ProfileChoiceViewModel(application);
    }


    @NonNull
    @Override
    public BaseMeshDataSource getRmDataSource() {
        return RmDataHelper.getInstance().initRM(Source.getDbSource());
    }

    public void resetMesh() {
        RmDataHelper.getInstance().restartMesh();
//        restartRmService();
    }

    public void restartRmService() {
        RmDataHelper.getInstance().initRM(Source.getDbSource());
    }
}
