package com.w3engineers.unicef.telemesh.data.di;

import android.arch.lifecycle.ViewModel;

import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * 6.
 * Create an abstract @Module class and write an abstract function inside it to bind your ViewModel
 * into the map using @ViewModelKey. The return type of the function tells Dagger that the value of
 * the map will be a Provider<ViewModel> instance and the key will be the Class instance of your ViewModel.
 */

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    public abstract ViewModel bindsSplashViewModel(SplashViewModel viewModel);

    //You can add any number of ViewModels here.
}
