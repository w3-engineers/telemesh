package com.w3engineers.unicef.telemesh.data.provider;

import android.app.Application;
import android.support.annotation.NonNull;

import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsViewModel;
import com.w3engineers.unicef.telemesh.ui.buydata.BuyDataViewModel;
import com.w3engineers.unicef.telemesh.ui.chat.ChatViewModel;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserViewModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivityViewModel;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.telemesh.ui.mywallet.MyWalletViewModel;
import com.w3engineers.unicef.telemesh.ui.selldata.SellDataViewModel;
import com.w3engineers.unicef.telemesh.ui.setorganization.SetOrganizationViewModel;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashViewModel;
import com.w3engineers.unicef.telemesh.ui.survey.SurveyViewModel;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileViewModel;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class ServiceLocator extends BaseServiceLocator {

    private static ServiceLocator serviceLocator;

    @NonNull
    public static ServiceLocator getInstance() {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        return serviceLocator;
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
    public SetOrganizationViewModel getSetOrganizationViewModel(@NonNull Application application) {
        return new SetOrganizationViewModel(application);
    }

    @NonNull
    public MainActivityViewModel getMainActivityViewModel(){
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
        return new MessageFeedViewModel();
    }

    @NonNull
    public SurveyViewModel getSurveyViewModel() {
        return new SurveyViewModel();
    }

    @NonNull
    public ChatViewModel getChatViewModel(@NonNull Application application){
        return new ChatViewModel(application);
    }

    @NonNull
    @Override
    public BaseRmDataSource getRmDataSource() {
        return RmDataHelper.getInstance().initRM(Source.getDbSource());
    }

    @NonNull
    public MyWalletViewModel getMyWalletViewModel(@NonNull Application application) {
        return new MyWalletViewModel(application);
    }

    @NonNull
    public BuyDataViewModel getBuyDataViewModel(@NonNull Application application) {
        return new BuyDataViewModel(application);
    }

    @NonNull
    public SellDataViewModel getSellDataViewModel(@NonNull Application application) {
        return new SellDataViewModel(application);
    }

    /*@NonNull
    public InAppShareViewModel getInAppShareViewModel(@NonNull Application application) {
        return new InAppShareViewModel(application);
    }*/

    public void resetRmDataSourceInstance() {
        RmDataHelper.getInstance().resetRmDataSourceInstance();
    }

    public void restartRmService() {
        RmDataHelper.getInstance().initRM(Source.getDbSource());
    }
}
