package com.w3engineers.unicef.telemesh.data.di;


import com.w3engineers.unicef.telemesh.ui.main.MainActivityViewModel;

import dagger.Component;

/**
 * Note:
 * In Android, you usually create a Dagger graph that lives in your application class because
 * you want an instance of the graph to be in memory as long as the app is running.
 * In this way, the graph is attached to the app lifecycle.
 * In some cases, you might also want to have the application context available in the graph.
 * For that, you would also need the graph to be in the application class.
 * One advantage of this approach is that the graph is available to other Android framework classes.
 * Additionally, it simplifies testing by allowing you to use a custom application class in tests.
 */

@Component(modules = StorageModule.class)
public interface ApplicationComponent {

    // With the inject(MainActivityViewModel mainActivityViewModel) method in the @Component interface,
    // we're telling Dagger that MainActivityViewModel requests injection and that it has to provide
    // the dependencies which are annotated with @Inject .
    void inject(MainActivityViewModel mainActivityViewModel);
}
