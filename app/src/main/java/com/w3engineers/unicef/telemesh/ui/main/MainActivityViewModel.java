package com.w3engineers.unicef.telemesh.ui.main;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends BaseRxViewModel {

    private MessageSourceData messageSourceData;

    public MainActivityViewModel() {
        messageSourceData = MessageSourceData.getInstance();
    }

    public void userOfflineProcess() {
        getCompositeDisposable().add(updateUserToOffline()
                .subscribeOn(Schedulers.io()).subscribe(integer -> {}, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    /*public void makeSendingMessageAsFailed() {

        getCompositeDisposable().add(updateMessageStatus()
                .subscribeOn(Schedulers.io()).subscribe(aLong -> {}, Throwable::printStackTrace));
    }

    private Single<Long> updateMessageStatus() {
        return Single.fromCallable(() -> messageSourceData
                .changeMessageStatusFrom(Constants.MessageStatus.STATUS_SENDING,
                        Constants.MessageStatus.STATUS_FAILED));
    }*/
}
