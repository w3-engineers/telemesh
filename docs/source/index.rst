.. telemesh documentation master file, created by
   sphinx-quickstart on Tue Oct 29 12:17:57 2019.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

.. _app_architecture:

Project Overview
----------------

Background
~~~~~~~~~~

Globally, 68.5 million people are forcibly displaced at the time of
writing this readme, and over 25.4 million are refugees. In Bangladesh,
there are over 650,000 Rohingya refugees who have fled violence, mass
killings and sexual abuse from neighboring Myanmar.2 Of those, nearly
60% are children, many of whom are orphaned Distributing information
about humanitarian services to large numbers of refugees poses
significant challenges for NGOs like UNICEF. While 40% of rural refugee
households have smartphones, many are unconnected due to a lack of or
poor telecommunications infrastructure or unaffordable cellular costs.
The UNHCR believes that connecting refugees would ultimately transform
humanitarian operations.

Project’s Goal
~~~~~~~~~~~~~~

We intend to make use of mesh network. It allows for multi-hop,
peer-to-peer connectivity directly between smartphones, instead of
relying on internet and cell networks. Blockchain is used in the network
to uniquely identify each node (smartphone) providing a trust layer to
users without centralized signup. It also provides the infrastructure
for users to connect multiple separate meshes by sharing an internet
connection in exchange for ERC20 tokens. This offers an entirely new and
unique method of information distribution not possible with existing
technology.

For UNICEF, we plan to develop an open source messaging app to be tested
in refugee camps, specifically, Cox’s Bazar, Bangladesh. A broadcast
channel would allow UNICEF to push vital information to smartphone users
about services like vaccination clinics, maternity clinics and schools.
The app would also allow refugees to message one another even if they do
not have a SIM card or cellular data.

Feature list
------------

-  One to one messaging
-  Message broadcast
-  App sharing
-  forthcoming

Project’s Structure
-------------------

::

       .
       |-- app
       |-- src
       |-- main
           |-- com.w3engineers.unicef
               |-- telemesh
                   |-- data #local database, file, shared preferences etc.
                   |-- ui #ui components
               |-- util
                   |-- helper #Generic tasks like TimeUtil, NetworkUtil etc.
                   |-- lib #third party library, component etc.
               |-- Application.java #Android Application class
       |-- viper #W3Engineers wrapper module
       |-- build.gradle
       |-- settings.gradle
       |-- versions.gradle
       |-- gradle.properties

-  **Alias** N/A

-  **Commands** N/A

Prerequisites
-------------

-  Mesh networking technology.
-  Android device with Wifi, WifiDirect, Bluetooth or Bluetooth Low
   Energy (BLE) support.

Project Dependencies
--------------------

-  **Viper**: This is a wrapper on native android to reduce some
   repeated works and of a mesh library and has been used as a
   dependency into this project.

Local development environments
------------------------------

You will be glad to know that you can start Telemesh Android application
development on either of the following operating systems −

-  Microsoft Windows XP or later version.

-  Mac OS X 10.5.8 or later version with Intel chip.

-  Linux including GNU C Library 2.7 or later.

Second point is that all the required tools to develop Android
applications are freely available and can be downloaded from the Web.

Following is the list of software’s you will need before you start your
Android application programming.

-  Java JDK 8 or later version

-  Android Studio 3.3 or later version

How to get started
------------------

**Step 1: Clone repository:** Navigate to directory where you want to
keep source code. Open command prompt. Execute below command: > git
clone https://github.com/w3-engineers/telemesh.git

**Step 2: Prepare gradle.properties file:** You need to request for mesh
technology support and some credentials to the Telemesh team to build
the project successfully.

Also you can use the following communication methods

-  The ``#get-help`` channel on our `Discord chat`_

-  The mailing list [media@telemesh.net] for long term discussion.

**Step 3: Sync and build:** If everything is ok then sync and build
should work as it should be. If not please recheck step 1 and 2.

**Step 4: Test on device:**

Minimum API: 19 (KitKat - 4.4.x)

.. _Discord chat: https://discord.gg/SHG4qrH

Development Guideline
---------------------


Hey, read the :ref:`app_architecture:TeleMesh App Architecture` section.


Developers Guideline step by step
---------------------------------

1. Find the ``versions.gradle`` in the root directory of the repo and
   any new support library reference should be added here.

2. Any support library on app level ``build.gradle`` should be added in
   this way

   ::

           implementation deps.support.app_compat
           implementation deps.support.design
           implementation deps.constraint_layout
           implementation deps.support.recyclerview
           implementation deps.support.cardview

3. Below you will get the reference of how to use custom ui components
   of **Viper** to reduce some repeated works



**BaseRecyclerView**

BaseRecyclerView is a wrapper class of android RecyclerView

::

   <RelativeLayout
           android:layout_width="match_parent"
           android:layout_height="match_parent">

           <TextView
               android:id="@+id/empty_layout"
               android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:layout_centerInParent="true"
               android:text="No data found"
               android:visibility="gone" />

           <com.w3engineers.ext.strom.application.ui.widget.BaseRecyclerView
               android:id="@+id/rv"
               android:layout_width="match_parent"
               android:layout_height="match_parent"
               app:brv_defaultAnimation="false"
               app:brv_emptyLayoutId="@id/empty_layout" // Empty View id. This is mandatory field
               app:brv_viewMode="vertical" />

       </RelativeLayout>

-  ``app:brv_emptyLayoutId="@id/empty_layout"`` This is compulsory filed
   if it does’t set then you will get Runtime exception
-  ``app:brv_viewMode="vertical"`` indicate how the RecyclerView scroll
   horizontally or vertically
-  ``app:brv_defaultAnimation="false"`` Mark default animation enable or
   disable

**BaseAdapter**

BaseAdapter is a generic RecyclerView adapter which is capable to work
with all types of data model.

**Example**

::

   public class ExampleAdapter extends BaseAdapter<User> {
       @Override
       public boolean isEqual(User left, User right) {
           return false;
       }

       @Override
       public BaseAdapterViewHolder newViewHolder(ViewGroup parent, int viewType) {
           return null;
       }
   }

Child class needs to implement *isEqual() and newViewHolder()* methods.
No needs to override **onBindViewHolder()**

**BaseToolBar**

``activity_home.xml``

::

   <com.w3engineers.ext.strom.application.ui.base.BaseToolBar
     android:id="@+id/home_toolbar"
     ...
     app:showHomeButton="true"            // this will show toolbar home button
     app:customTitle="@string/app_name"  // this will show toolbar title
     >
   </com.w3engineers.ext.strom.application.ui.base.BaseToolBar>

``HomeActivity.java``

::

    @Override
        protected int getToolbarId() {
            return R.id.home_toolbar;
        }


**BaseButton:**

BaseButton is a custom View class. You can design any types of Button
with and without image, round corner and there are various properties
with it.

-``app:bb_drawable="@drawable/button_gradient_blue"`` is a mandatory
field. If developer does not set this property it may causes Runtime
exception

::

   <com.w3engineers.ext.strom.application.ui.widget.BaseCompositeButton
                       android:id="@+id/btn_facebook_like"
                       android:layout_width="wrap_content"
                       android:layout_height="wrap_content"
                       android:layout_marginBottom="10dp"
                       android:padding="10dp"
                       android:textStyle="italic"
                       app:btn_borderColor="#FFFFFF"
                       app:btn_borderWidth="1dp"                       // Button border width
                       app:btn_defaultColor="#3b5998"
                       app:btn_focusColor="#5577bd"                    // When click show this focus color
                       app:btn_fontIconSize="15sp"
                       app:btn_iconPosition="right"                    // Icon position (left, right, top, bottom)
                       app:btn_iconResource="@drawable/facebook"
                       app:btn_radius="30dp"                           // Button corner radious
                       app:btn_text="Like my facebook page"
                       app:btn_disabledBorderColor="@color/colorAccent"
                       app:btn_disabledTextColor="@color/colorAccent"
                       app:btn_disabledColor="@color/colorAccent"
                       app:btn_textGravity="start"
                       app:btn_iconColor="@color/colorAccent"
                       app:btn_textColor="#FFFFFF" />

Till now nothing is mandatory, there are so many options here. This
custom class will support for all types of button.

**BaseEditText:**

BaseEditText is a custom EditText wrapper, using this class it is
possible to design EditText with and without label max, min char length
and there are various options with it.

::

   <com.w3engineers.ext.strom.application.ui.widget.BaseEditText
                   android:layout_width="match_parent"
                   android:layout_height="wrap_content"
                   android:layout_marginLeft="10dp"
                   android:hint="Floating Label"
                   app:bet_floatingLabel="highlight"
                   app:bet_maxCharacters="10"                 // Max character size
                   app:bet_minCharacters="2"                  // Min character size
                   app:bet_autoValidate="true"
                   app:bet_floatingLabelAlwaysShown="false"
                   app:bet_checkCharactersCountAtBeginning="true"
                   app:bet_baseColor="@color/colorAccent"
                   app:bet_floatingLabelTextSize="20sp"
                   app:bet_hideUnderline="true"
                   app:bet_helperText="Helper"               // If it needs to help user provide some example
                   app:bet_helperTextAlwaysShown="true"
                   app:bet_helperTextColor="@color/colorAccent"
                   app:bet_primaryColor="@color/accent"/>

Use this class and its necessary properties.

**BaseButton**

::


   <com.w3engineers.ext.strom.application.ui.widget.BaseButton
               android:id="@+id/btn_show_items"
               android:layout_width="match_parent"
               android:layout_height="wrap_content"
               android:layout_marginTop="10dp"
               android:text="@string/show_data"
               android:padding="10dp"
               app:layout_constraintTop_toBottomOf="@+id/btn_add_item"
               app:layout_constraintLeft_toLeftOf="parent"
               app:layout_constraintRight_toRightOf="parent"
               app:bb_drawable="@drawable/button_gradient_blue"/>


**BaseDialog**

Base dialog is a custom dialog class, which force developer to set a
layout file for custom design

::

       protected abstract int getLayoutId();
       protected abstract void startUi();

Are the two methods needs to child class implement.

**DialogUtil**

There are three overloading static methods here

::

   public static void showDialog(Context context, String message, DialogListener listener)
   public static void showDialog(Context context, String title, String message, DialogListener listener)
   public static void showDialog(Context context, String title, String message, String positiveText, String negativeText, final DialogListener listener)

Developer can call any one as his/her needs. It will show a default dialog


**ItemClickListener:**

::

   public interface ItemClickListener<T> {
       /**
        * Called when a item has been clicked.
        *
        * @param view The view that was clicked.
        * @param item The T type object that was clicked.
        */
       void onItemClick(View view, T item);
   }

Implement this interface in UI (Activity or Fragment) pass its reference
to the Adapter

**ItemLongClickListener**

::

   public interface ItemLongClickListener<T> {
       /**
        * Called when a item has been long clicked.
        *
        * @param view The view that was clicked.
        * @param item The T type object that was clicked.
        */
       void onItemLongClick(View view, T item);
   }

For item long click listener implement this interface in UI (Activity or
Fragment) and pass its reference to adapter

Test Coverage
-------------

-  This repo is configured with [Travis][travis] and
   [coverall][coverall]. Every merge with *master* produced a test
   coverage report. Latest coverage report is available `here`_. `This`_
   badge here shows coverage status.

-  To generate report locally you should go to project’s root directory,
   then execute below command: > gradlew coveralls

**NOTE:** You must have a connected device or emulator as it runs
instrumentation tests. You will find the coverage report at
*telemesh/app/build/reports/coverage*

.. _here: https://coveralls.io/github/w3-engineers/telemesh?branch=master
.. _This: #Telemesh

License
-------

::

      Copyright 2019 W3 Engineers

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.

Contributing Guideline
----------------------

Please find here `Contributing Guideline`_

Reporting Issues
----------------

If you face any bug or have any particular feature request please go
`here`_

Community manager
-----------------

If you have any suggestions or feedback, you are always welcome to reach
our community manager through [media@telemesh.net] & [info@telemesh.net]

.. _Contributing Guideline: https://github.com/w3-engineers/telemesh/blob/master/CONTRIBUTING.md
.. _here: https://github.com/w3-engineers/telemesh/blob/master/CONTRIBUTING.md#reportissue


