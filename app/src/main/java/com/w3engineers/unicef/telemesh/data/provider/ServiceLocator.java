package com.w3engineers.unicef.telemesh.data.provider;

import android.app.Application;

import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
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
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashViewModel;
import com.w3engineers.unicef.telemesh.ui.survey.SurveyViewModel;
import com.w3engineers.unicef.telemesh.ui.test.TestViewModel;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileViewModel;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [17-Sep-2018 at 4:54 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [17-Sep-2018 at 4:54 PM].
 * * --> <Second Editor> on [17-Sep-2018 at 4:54 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [17-Sep-2018 at 4:54 PM].
 * * --> <Second Reviewer> on [17-Sep-2018 at 4:54 PM].
 * * ============================================================================
 **/
public class ServiceLocator extends BaseServiceLocator {

    private static ServiceLocator serviceLocator;

    public static ServiceLocator getInstance() {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        return serviceLocator;
    }

    public TestViewModel getTestViewModel() {
        return new TestViewModel(UserDataSource.getInstance());
    }


    public SplashViewModel getSplashViewModel(Application application) {
        return new SplashViewModel(application);
    }

    public AboutUsViewModel getAboutUsViewModel(Application application) {
        return new AboutUsViewModel(application);
    }

    public UserProfileViewModel getUserProfileViewModel(Application application) {
        return new UserProfileViewModel(application);
    }

    public CreateUserViewModel getCreateUserViewModel(Application application) {
        return new CreateUserViewModel(application);
    }

    public MainActivityViewModel getMainActivityViewModel(){
        return new MainActivityViewModel();
    }

    public SettingsViewModel getSettingsViewModel(Application application){
        return new SettingsViewModel(application);
    }


    public MeshContactViewModel getMeshContactViewModel() {
        return new MeshContactViewModel(UserDataSource.getInstance());
    }

    public MessageFeedViewModel getMessageFeedViewModel() {
        return new MessageFeedViewModel();
    }

    public SurveyViewModel getSurveyViewModel() {
        return new SurveyViewModel();
    }

    public ChatViewModel getChatViewModel(Application application){
        return new ChatViewModel(application);
    }

    @Override
    public BaseRmDataSource getRmDataSource() {
        return RmDataHelper.getInstance().initRM(Source.getDbSource());
    }

    public MyWalletViewModel getMyWalletViewModel(Application application) {
        return new MyWalletViewModel(application);
    }

    public BuyDataViewModel getBuyDataViewModel(Application application) {
        return new BuyDataViewModel(application);
    }

    public SellDataViewModel getSellDataViewModel(Application application) {
        return new SellDataViewModel(application);
    }
}
