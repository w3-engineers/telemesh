package com.w3engineers.unicef.telemesh.ui.chooseprofileimage;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.databinding.ActivityProfileImageBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.util.helper.ImageUtil;

public class ProfileImageActivity extends BaseActivity implements ItemClickListener<Integer> {

    private ActivityProfileImageBinding mProfileImageBinding;
    private ProfileImageAdapter mAdapter;
    private int selectedItem = -1;
    private int ITEM_IN_ROW = 3;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_image;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }


    @Override
    protected void startUI() {

        mProfileImageBinding = (ActivityProfileImageBinding) getViewDataBinding();
        setTitle(getString(R.string.select_photo));
        selectedItem = getIntent().getIntExtra(CreateUserActivity.IMAGE_POSITION, CreateUserActivity.INITIAL_IMAGE_INDEX);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new ProfileImageAdapter(this, selectedItem);
        mAdapter.setItemClickListener(this);
        mProfileImageBinding.recyclerView.setAdapter(mAdapter);
        mProfileImageBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, ITEM_IN_ROW));
        mAdapter.addItem(ImageUtil.getAllImages());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_done:
                Intent intent = getIntent();
                intent.putExtra(CreateUserActivity.IMAGE_POSITION, selectedItem);
                setResult(RESULT_OK, intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, Integer item) {
        selectedItem = item;
    }
}
