package com.w3engineers.unicef.telemesh.data.di;

import android.content.Context;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {

    @Provides
    public AppDatabase provideAppDatabase(){
        return AppDatabase.createDbWithMigration();
    }
}
