package com.w3engineers.unicef.util.helper;

import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MeshDataSourceTest {

    MeshDataSource meshDataSource;

    @Before
    public void setUp() throws Exception {
        meshDataSource = MeshDataSource.getInstance();
    }

}
