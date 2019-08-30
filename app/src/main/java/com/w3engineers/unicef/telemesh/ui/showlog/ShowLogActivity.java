package com.w3engineers.unicef.telemesh.ui.showlog;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityShowLogBinding;
import com.w3engineers.unicef.util.helper.LogProcessUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class ShowLogActivity extends BaseActivity {

    private ActivityShowLogBinding mShowLogBinding;
    @Nullable
    public ShowLogViewModel showLogViewModel;
    private ShowLogAdapter showLogAdapter;

    private String[] logArray = {"All", "Special", "Warning", "Info", "Error"};
    private final int ALL = 0, SPECIAL = 1, WARNING = 2, INFO = 3, ERROR = 4, DATE = 5;

    private int type = ALL;
    private boolean textChanged = true;

    private List<MeshLogModel> logList = new ArrayList<>();

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_log;
    }

    @Override
    protected void startUI() {
        mShowLogBinding = (ActivityShowLogBinding) getViewDataBinding();

        init();

        setTitle(getString(R.string.settings_open_log));

        uiUpdate();
        initSearch();

        showAllLogs();
        showLatestLogs();

        searchlogOperation();
    }

    private void showAllLogs() {
        if (showLogViewModel != null) {

            showLogViewModel.startAllLogObserver().observe(this, (List<MeshLogModel> meshLogModels) -> {
                if (meshLogModels != null && meshLogModels.size() > 0) {

                    logList.clear();

                    logList = meshLogModels;
                    showLogAdapter.resetWithList(logList);
                }
                mShowLogBinding.pbLoading.setVisibility(View.GONE);
            });

            LogProcessUtil.getInstance().readLog();
        }
    }

    private void showLatestLogs() {
        if (showLogViewModel != null) {
            showLogViewModel.startLogObserver().observe(this, (MeshLogModel meshLogModel) -> {

                if (meshLogModel != null) {
                    logList.add(meshLogModel);

                    if (type == meshLogModel.getType()) {
                        mShowLogBinding.recyclerViewLog.setVisibility(View.VISIBLE);
                        showLogAdapter.addItem(meshLogModel);
                    }
                    mShowLogBinding.pbLoading.setVisibility(View.GONE);
                }
            });

        }
    }

    private void uiUpdate() {
        mShowLogBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String logName = logArray[position];
                textChanged = false;
                mShowLogBinding.editTextSearch.setText("");
                showItem(logName);
                mShowLogBinding.pbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mShowLogBinding.textViewClear.setOnClickListener(v -> {
            showLogAdapter.clear();
            mShowLogBinding.recyclerViewLog.setVisibility(View.GONE);
            logList.clear();
        });

        mShowLogBinding.textViewToBottom.setOnClickListener(v -> ShowLogActivity.this.scrollToLast());
    }

    private void initSearch() {
        mShowLogBinding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (showLogViewModel != null && textChanged) {
                    showLogAdapter.clear();
                    showLogViewModel.startSearch(String.valueOf(s), logList);
                    mShowLogBinding.pbLoading.setVisibility(View.VISIBLE);
                }
                textChanged = true;
            }
        });
    }

    private void showItem(String logItem) {

        if (logItem.contains(logArray[WARNING])) {
            type = WARNING;
        } else if (logItem.equalsIgnoreCase(logArray[SPECIAL])) {
            type = SPECIAL;
        } else if (logItem.equalsIgnoreCase(logArray[INFO])) {
            type = INFO;
        } else if (logItem.equalsIgnoreCase(logArray[ERROR])) {
            type = ERROR;
        } else if (logItem.equalsIgnoreCase("Date")) {
            type = DATE;
        } else {
            type = ALL;
        }

        if (showLogViewModel != null) {

            showLogViewModel.getFilteredListWithTag().observe(this, (List<MeshLogModel> showLogEntities) -> {
                if (showLogEntities != null && showLogEntities.size() > 0) {
                    mShowLogBinding.recyclerViewLog.setVisibility(View.VISIBLE);
                    showLogAdapter.resetWithList(showLogEntities);
                } else {
                    showLogAdapter.clear();
                }
                mShowLogBinding.pbLoading.setVisibility(View.GONE);
            });
            showLogViewModel.filterListWithTag(type, logList);
        }
    }

    private void searchlogOperation() {
        if (showLogViewModel != null) {
            showLogViewModel.getFilteredList().observe(this, (List<MeshLogModel> showLogEntities) -> {
                if (showLogEntities != null && showLogEntities.size() > 0) {
                    mShowLogBinding.recyclerViewLog.setVisibility(View.VISIBLE);
                    showLogAdapter.resetWithList(showLogEntities);
                }
                mShowLogBinding.pbLoading.setVisibility(View.GONE);
            });
        }
    }

    private void init() {
        showLogViewModel = getViewModel();
        mShowLogBinding.recyclerViewLog.setItemAnimator(null);
        mShowLogBinding.recyclerViewLog.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mShowLogBinding.recyclerViewLog.setLayoutManager(layoutManager);

        showLogAdapter = new ShowLogAdapter(this);
        mShowLogBinding.recyclerViewLog.setAdapter(showLogAdapter);
    }

    private void scrollToLast() {
        int index = showLogAdapter.getItemCount() - 1;
        if (index > 0) {
            mShowLogBinding.recyclerViewLog.scrollToPosition(index);
        }
    }

    private ShowLogViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getShowUserViewModel(getApplication());
            }
        }).get(ShowLogViewModel.class);
    }
}
