package com.w3engineers.unicef.telemesh.data.di;

import android.app.Application;

import com.w3engineers.unicef.TeleMeshApplication;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

// 1. Definition of the Application graph
// Next create the reference of the Graph to the custom application class TelemeshApplication.java
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ViewModelFactoryModule.class,
                ViewModelModule.class
        }
)
public interface AppComponent extends AndroidInjector<TeleMeshApplication> {

        @Component.Builder
        interface Builder {

                @BindsInstance
                Builder application(Application application);

                AppComponent build();
        }

}
