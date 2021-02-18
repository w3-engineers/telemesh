package com.w3engineers.unicef.telemesh.data.di;

import android.arch.lifecycle.ViewModelProvider;

import com.w3engineers.unicef.telemesh.data.provider.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

/** 4.
 * This module is responsible for generating the dependency injection for the factory class.
 * @Module is given when we need to provide dependency of any android components or 3rd party sdk like Retrofit.
 *
 * Next create the Map key ViewModelKey.java
 */
@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);

//    Both are same (above is same as below using @Provides annotation).
//    @Provides
//    static ViewModelProvider.Factory bindViewModelFactory2(ViewModelProviderFactory factory) {
//        return factory;
//    }
}
