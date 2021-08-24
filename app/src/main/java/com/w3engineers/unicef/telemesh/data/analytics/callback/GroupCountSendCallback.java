package com.w3engineers.unicef.telemesh.data.analytics.callback;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;

import java.util.ArrayList;

public interface GroupCountSendCallback {
    void onGetGroupCountSendResponse(boolean isSuccess, ArrayList<GroupCountParseModel> modelList);
}
