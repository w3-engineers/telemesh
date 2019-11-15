.. viper:

=====
Viper
=====

Viper is the android library that acts as a communication bridge between Telemesh Android Application
and Telemesh Service.
It is responsible for coordinating the actions to the Telemesh Service.
It can also perform some mappings to prepare the objects coming from the Android Application.

Dependency
----------

Include the library in app level build.gradle

    dependencies{
        implementation 'com.github.w3-engineers:viper:<version_number>'
    }


Usage
-----

It contains few tools to ease developers daily development. Support provides:

- Custom components (BaseActivity, BaseFragment, BaseAdapter etc.)
- Custom Widgets (BaseButton, BaseRecyclerView, BaseEditText etc.)
- Close Coupled Behavior with Widget and Components
- Few configurable options (debugDatabase, Toasty etc.Still we are improving here)
- Enhanced support for Room (migration, creation of database, columns etc.)
- Necessary library added such a way so that developers can use without including in their gradle file (Timber, Multidex, Crashlytics, Debug Database etc.)
- BaseSplashViewModel provides time calculation facility and enforce ViewModel LiveData communication
