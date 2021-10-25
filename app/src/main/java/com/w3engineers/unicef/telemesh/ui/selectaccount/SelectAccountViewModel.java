package com.w3engineers.unicef.telemesh.ui.selectaccount;

import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.base.ui.BaseRxViewModel;
import com.w3engineers.unicef.util.helper.ViperUtil;

/**
 * Created by Azizul Islam on 10/22/21.
 */
public class SelectAccountViewModel extends BaseRxViewModel {
    public void launchImportWalletActivity() {
        ServiceLocator.getInstance().launchActivity(ViperUtil.WALLET_IMPORT_ACTIVITY);
    }
}
