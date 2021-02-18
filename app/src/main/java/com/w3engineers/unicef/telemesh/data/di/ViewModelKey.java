package com.w3engineers.unicef.telemesh.data.di;



import android.arch.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

/**5.
 * Create the key of the Map
 *
 * Multi Binding working in that way you create a key and you map certain dependencies to that key.
 * You can use these dependencies and use/inject them in different classes.
 *
 * Create an annotation class that Dagger will use to create the key of the map.
 * For example, take a look inside ViewModelKey.java which is used to tell Dagger that the key of
 * the map will be a ViewModel Class instance.
 *
 * Next Bind the key into Map ViewModelModule.java
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
