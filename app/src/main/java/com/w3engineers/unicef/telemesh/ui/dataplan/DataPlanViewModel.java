package com.w3engineers.unicef.telemesh.ui.dataplan;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.arch.lifecycle.MutableLiveData;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.dataplan.model.BuyerUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataPlanViewModel extends BaseRxViewModel {

    public MutableLiveData<List<BuyerUser>> getBuyerUsers = new MutableLiveData<>();

    private List<BuyerUser> prepareBuyerList() {
        List<BuyerUser> buyerUsers = new ArrayList<>();
        Random random = new Random();
        String[] names = {"Danial Alvez", "Andre Russle", "Alvie D Costa", "Devid Warner", "Moin Aly"};
        int[] status = {Constants.BuyerStatus.DEFAULT, Constants.BuyerStatus.ACTIVE, Constants.BuyerStatus.IN_USE};

        for (String name : names) {
            int getStatusIndex = random.nextInt(3);
            BuyerUser buyerUser = new BuyerUser().setUserName(name)
                    .setActiveMode(status[getStatusIndex])
                    .setUsageData(getStatusIndex == 0 ? "0" : "" + random.nextInt(100));
            buyerUsers.add(buyerUser);
        }

        return buyerUsers;
    }

    public void getBuyerList() {
        getBuyerUsers.postValue(prepareBuyerList());
    }
}
