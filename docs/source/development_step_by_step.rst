.. development_step_by_step:

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