package com.w3engineers.unicef.telemesh.ui.conversations;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;

import java.util.List;

public class ConversationViewModel extends BaseRxAndroidViewModel {

    public ConversationViewModel(@NonNull Application application) {
        super(application);
    }

    public void openMessage(GroupEntity groupEntity) {

    }

    public LiveData<List<GroupEntity>> getGroupConversation() {
        return null;
    }
}
