.. viper_intro:


Viper Introduction
------------------

``Viper`` is the android library that acts as a communication bridge between
Telemesh Android Application and Telemesh Service.
It is responsible for coordinating the actions to the Telemesh Service.
It can also perform some mappings to prepare the objects coming from the Android Application.

It provides few supports on ``ui/ux`` to ease developers daily development.
Also it provides set of api for ``mesh`` support and ``wallet`` support.
Currently ``Telemesh`` ui design use those supports from Viper.


Dependency
----------

Include the library in app level build.gradle of Telemesh

 ::

       dependencies{
            implementation 'com.github.w3-engineers:viper:<version_number>'
       }