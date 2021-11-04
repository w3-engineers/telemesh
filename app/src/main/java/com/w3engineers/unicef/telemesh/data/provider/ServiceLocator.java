package com.w3engineers.unicef.telemesh.data.provider;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsViewModel;
import com.w3engineers.unicef.telemesh.ui.addnewmember.AddNewMemberViewModel;
import com.w3engineers.unicef.telemesh.ui.bulletindetails.BulletinViewModel;
import com.w3engineers.unicef.telemesh.ui.chat.ChatViewModel;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserViewModel;
import com.w3engineers.unicef.telemesh.ui.editprofile.EditProfileViewModel;
import com.w3engineers.unicef.telemesh.ui.feedback.FeedbackViewModel;
import com.w3engineers.unicef.telemesh.ui.groupcreate.GroupCreateViewModel;
import com.w3engineers.unicef.telemesh.ui.groupdetails.GroupDetailsViewModel;
import com.w3engineers.unicef.telemesh.ui.groupnameedit.GroupNameEditViewModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivityViewModel;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;
import com.w3engineers.unicef.telemesh.ui.meshdiscovered.DiscoverViewModel;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedViewModel;
import com.w3engineers.unicef.telemesh.ui.selectaccount.SelectAccountViewModel;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashViewModel;
import com.w3engineers.unicef.telemesh.ui.termofuse.TermsOfUseViewModel;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileViewModel;
import com.w3engineers.unicef.util.base.ui.BaseServiceLocator;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class ServiceLocator extends BaseServiceLocator {

    // SingleTon
    protected ServiceLocator() {

    }

    // to manage proper singleton that will remained singleton during multi-thread instance
    public static class ServiceLocatorHolder {
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
    public MainActivityViewModel getMainActivityViewModel() {
        restartRmService();
        return new MainActivityViewModel();
    }

    @NonNull
    public SettingsViewModel getSettingsViewModel(@NonNull Application application) {
        return new SettingsViewModel(application);
    }

    @NonNull
    public MeshContactViewModel getMeshContactViewModel(@NonNull Application application) {
        return new MeshContactViewModel(application);
    }

    @NonNull
    public DiscoverViewModel getDiscoveViewModel(@NonNull Application application) {
        return new DiscoverViewModel(application);
    }

    @NonNull
    public MessageFeedViewModel getMessageFeedViewModel() {
        return new MessageFeedViewModel(FeedDataSource.getInstance());
    }

    @NonNull
    public ChatViewModel getChatViewModel(@NonNull Application application) {
        return new ChatViewModel(application);
    }

    @NonNull
    public TermsOfUseViewModel getTermsOfViewModel() {
        return new TermsOfUseViewModel();
    }


    public SelectAccountViewModel getSelectAccountViewModel() {
        return new SelectAccountViewModel();
    }

    /*@NonNull
    public ConversationViewModel getConversationViewModel(@NonNull Application application) {
        return new ConversationViewModel(application);
    }*/

    /*@NonNull
    public ProfileChoiceViewModel getProfileChoiceViewModel(@NonNull Application application) {
        return new ProfileChoiceViewModel(application);
    }

    @NonNull
    public ImportProfileViewModel getImportProfileViewModel(@NonNull Application application) {
        return new ImportProfileViewModel(application);
    }

    @NonNull
    public SecurityViewModel getSecurityViewModel(@NonNull Application application) {
        return new SecurityViewModel(application);
    }

    @NonNull
    public ImportWalletViewModel getImportWalletViewModel(@NonNull Application application) {
        return new ImportWalletViewModel(application);
    }*/

    @NonNull
    public EditProfileViewModel getEditProfileViewModel(@NonNull Application application) {
        return new EditProfileViewModel(application);
    }

    @NonNull
    public FeedbackViewModel getFeedbackViewModel(@NonNull Application application) {
        return new FeedbackViewModel(application);
    }

    @NonNull
    public GroupCreateViewModel getGroupCreateViewModel(@NonNull Application application) {
        return new GroupCreateViewModel(application);
    }

    @NonNull
    public GroupDetailsViewModel getGroupDetailsViewModel(@NonNull Application application) {
        return new GroupDetailsViewModel(application);
    }

    @NonNull
    public GroupNameEditViewModel getGroupNameEditViewModel(@NonNull Application application) {
        return new GroupNameEditViewModel(application);
    }

    @NonNull
    public AddNewMemberViewModel getAddNewMemberViewModel(@NonNull Application application) {
        return new AddNewMemberViewModel(application);
    }

    @NonNull
    public BulletinViewModel getBulletinViewModel() {
        return new BulletinViewModel(FeedDataSource.getInstance());
    }


    /*@NonNull
    @Override
    public BaseMeshDataSource getRmDataSource() {
        return RmDataHelper.getInstance().initRM(Source.getDbSource());
    }*/

    @Override
    public void initViper() {
        RmDataHelper.getInstance().initRM(Source.getDbSource());
    }

    //Call it only from policy page and should not call it
    // from any other place in the project.
    @Deprecated
    public void startTelemeshService() {
        RmDataHelper.getInstance().startServiceFromPolicyPage(Source.getDbSource());
    }

    public void launchActivity(int activityType) {
        RmDataHelper.getInstance().launchActivity(activityType);
    }

    public void getLocation() {
        Location location = RmDataHelper.getInstance().getLocationFromServiceApp();
        if (location == null) {
            Log.e("Location", "user location is null");
        } else {
            Log.e("Location", "user location is : " + location.getLatitude() + " " + location.getLongitude());
        }
    }

    public void resetMesh() {
        RmDataHelper.getInstance().restartMesh();
    }

    public void restartRmService() {
        RmDataHelper.getInstance().initRM(Source.getDbSource());
    }
}
