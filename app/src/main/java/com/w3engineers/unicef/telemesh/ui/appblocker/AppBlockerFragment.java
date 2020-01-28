package com.w3engineers.unicef.telemesh.ui.appblocker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.databinding.FragmentAppBlockerBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class AppBlockerFragment extends DialogFragment implements View.OnClickListener {

    private FragmentAppBlockerBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_blocker, container, false);

        setCancelable(false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.getDialog() != null) {
            this.getDialog().setCanceledOnTouchOutside(false);
        }

        mBinding.buttonUpdate.setOnClickListener(this);
        mBinding.buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_update) {
            if (!Constants.IS_DATA_ON) {
                Toaster.showShort(LanguageUtil.getString(R.string.no_internet_connection));
                return;
            }
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().checkPlayStoreAppUpdate(Constants.AppUpdateType.BLOCKER, "");
            }
        } else if (v.getId() == R.id.button_cancel) {
            android.os.Process.killProcess(Process.myPid());
        }
    }

}
