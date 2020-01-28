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

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.FragmentAppBlockerBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

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

       /* Dialog dialog = onCreateDialog(savedInstanceState);

        if (dialog != null) {
            Log.d("AppBlocker", "Dialog not null");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        } else {
            Log.d("AppBlocker", "Dialog null");
        }*/

        if (this.getDialog() != null) {
            this.getDialog().setCanceledOnTouchOutside(false);
        } else {
            Log.d("AppBlocker", "Dialog 2 null");
        }

        mBinding.buttonUpdate.setOnClickListener(this);
        mBinding.buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_update) {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().checkPlayStoreAppUpdate();
            }
        } else if (v.getId() == R.id.button_cancel) {
            android.os.Process.killProcess(Process.myPid());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("AppBlocker", "onCreateDialog call");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
