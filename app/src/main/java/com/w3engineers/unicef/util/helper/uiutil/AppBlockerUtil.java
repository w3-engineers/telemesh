package com.w3engineers.unicef.util.helper.uiutil;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.os.Process;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.w3engineers.mesh.util.lib.mesh.DataManager;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.databinding.DialogAppBlockerBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.LanguageUtil;

public class AppBlockerUtil {

    public static void openAppBlockerDialog(Activity activity, String versionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(activity);
        DialogAppBlockerBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_app_blocker, null, false);
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();

        String versionText = "Update are Available: " + versionName;
        binding.textViewVersion.setText(versionText);

        binding.textViewUpdate.setOnClickListener(v -> {

            if (DataManager.on().isNetworkOnline()) {
                Toast.makeText(activity, LanguageUtil.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            if (MainActivity.getInstance() != null) {
                dialog.dismiss();
                // TODO: 2/14/2020 need to optimize that for app blocker it is need to check play store or not
                MainActivity.getInstance().checkPlayStoreAppUpdate(Constants.AppUpdateType.BLOCKER, "");
            }
        });

        binding.textViewCancel.setOnClickListener(v -> Process.killProcess(Process.myPid()));

        dialog.show();
    }
}
