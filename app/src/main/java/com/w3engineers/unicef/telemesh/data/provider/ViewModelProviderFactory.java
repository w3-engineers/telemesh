package com.w3engineers.unicef.telemesh.data.provider;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * 3.
 * What’s the point of creating ViewModelProviderFactory class?
 *
 * it's because by default classes that extend ViewModel can only have an empty constructor.
 * You can’t pass any parameter through the constructor. So if you want to use constructor injection
 * you can’t pass parameters through a constructor.
 * So to solve the issue we have to create custom ViewModelProviderFactory.
 * If we create the factory then we can effectively do the constructor injection.
 * In the constructor of the class, you can see the @Inject annotation.
 * That’s a particular thing that has to do with the Multibinding.
 *
 * What is Multibinding?
 *
 * custom ViewModelProviderFactory implement a ViewModelProvider.Factory,
 * which can be used to instantiate ViewModels in a lifecycle-aware way.
 * This implementation of ViewModelProvider.Factory needs a Map dependency that Dagger has to satisfy.
 * The way we achieve this resolution is by using multibindings.
 *
 * We have to bind all the ViewModels into a map using the @IntoMap annotation.
 *
 * Multibinding is a way of binding several objects of some given type into a collection
 * that application code can inject without depending on the individual bindings directly.
 *
 * Next create ViewModelFactoryModule.java
 */
@Singleton
public class ViewModelProviderFactory implements ViewModelProvider.Factory {


    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    //Add a Map<Class<? extends ViewModel>, Provider<ViewModel>> parameter to its constructor as the dependency.
    public ViewModelProviderFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators){
          this.creators = creators;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
