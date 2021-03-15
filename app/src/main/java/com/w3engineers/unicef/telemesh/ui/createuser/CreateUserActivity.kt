package com.w3engineers.unicef.telemesh.ui.createuser

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.w3engineers.ext.strom.application.ui.base.BaseActivity
import com.w3engineers.unicef.telemesh.R
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator
import com.w3engineers.unicef.telemesh.ui.chooseprofileimage.ProfileImageActivity
import com.w3engineers.unicef.telemesh.ui.importwallet.ImportWalletActivity
import com.w3engineers.unicef.telemesh.ui.main.MainActivity
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity
import com.w3engineers.unicef.util.helper.CommonUtil
import com.w3engineers.unicef.util.helper.WalletAddressHelper
import com.w3engineers.unicef.util.helper.uiutil.UIHelper
import com.w3engineers.walleter.wallet.WalletService
import kotlinx.android.synthetic.main.activity_create_user.*
/*
* ============================================================================
* Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
* Unauthorized copying of this file, via any medium is strictly prohibited
* Proprietary and confidential
* ============================================================================
*/
class CreateUserActivity : BaseActivity(), View.OnClickListener {

    // private var mViewModel: CreateUserViewModel? = null
    // private var mBinding: ActivityCreateUserBinding? = null
    // private var mPassword: String? = null

    // lateinit: Use this keyword when you know the value of a property will not be null once it is initialized.
    //private lateinit var mBinding: ActivityCreateUserBinding
    private lateinit var mViewModel: CreateUserViewModel
    private lateinit var mPassword: String

    private val PROFILE_IMAGE_REQUEST = 1

    // Kotlin can infer the type from the assignment, so no boolean type is required to indicate
    private var isLoadAccount = false


    // to expose the field to Java class @JvmField annotation is used
    companion object {
        @JvmField var INITIAL_IMAGE_INDEX = -1
        @JvmField var sInstance: CreateUserActivity? = null
        @JvmField var IMAGE_POSITION = "image_position"
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_create_user
    }

    override fun statusBarColor(): Int {
        return R.color.colorPrimaryDark
    }

    @SuppressLint("CheckResult")
    override fun startUI() {
        //mBinding = viewDataBinding as ActivityCreateUserBinding
        setTitle(getString(R.string.create_user))
        mViewModel = createUserViewModel

        setClickListener(imageViewBack)
        parseIntent()

        UIHelper.hideKeyboardFrom(this, editTextName)
        imageViewCamera.setOnClickListener(this)
        buttonSignup.setOnClickListener(this)
        imageProfile.setOnClickListener(this)

        editTextName.maxCharacters = Constants.DefaultValue.MAXIMUM_TEXT_LIMIT
        editTextName.minCharacters = Constants.DefaultValue.MINIMUM_TEXT_LIMIT

        mViewModel.textChangeLiveData.observe(this, { nameText: String -> nextButtonControl(nameText) })

        mViewModel.textEditControl(editTextName)
        sInstance = this

        editTextName.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_NEXT) {
                nextAction()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun nextButtonControl(nameText: String) {

        if (!TextUtils.isEmpty(nameText) &&
                nameText.length >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT) {
            buttonSignup.setBackgroundResource(R.drawable.ractangular_gradient)
            buttonSignup.setTextColor(resources.getColor(R.color.white))
            //mBinding.buttonSignup.setClickable(true);
        } else {
            buttonSignup.setBackgroundResource(R.drawable.ractangular_white)
            buttonSignup.setTextColor(resources.getColor(R.color.new_user_button_color))
            //mBinding.buttonSignup.setClickable(false);
        }
    }

    private val createUserViewModel: CreateUserViewModel
         get() = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ServiceLocator.getInstance().getCreateUserViewModel(application) as T
            }
        }).get(CreateUserViewModel::class.java)

    protected fun requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            goNext()
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            CommonUtil.showPermissionPopUp(this@CreateUserActivity)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error: DexterError? -> requestMultiplePermissions() }.onSameThread().check()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        when (id) {
            R.id.buttonSignup -> nextAction()
            R.id.imageProfile, R.id.imageViewCamera -> {
                val intent = Intent(this, ProfileImageActivity::class.java)
                intent.putExtra(IMAGE_POSITION, mViewModel.imageIndex)
                startActivityForResult(intent, PROFILE_IMAGE_REQUEST)
            }
            R.id.imageViewBack -> finish()
        }
    }

    private fun nextAction() {
        if (isLoadAccount) {
            saveData()
        } else {
            goToPasswordPage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mViewModel.imageIndex = data.getIntExtra(IMAGE_POSITION, INITIAL_IMAGE_INDEX)
            val id = resources.getIdentifier(Constants.drawables.AVATAR_IMAGE + mViewModel.imageIndex, Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, packageName)
            imageProfile.setImageResource(id)


            nextButtonControl(editTextName.text.toString())

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sInstance = null
    }

    private fun saveData() {

        // Create an immutable variable that stores a lambda expression called notEmpty.
        // The lambda expression takes a TextView as a parameter (EditText inherits from TextView)
        // and returns a Boolean:
        // If a lambda expression only has a single parameter,
        // it can be omitted and replaced with the it keyword.

        // The pattern is input -> output, however, if the code returns no value we use the type Unit:
        val notEmpty: (TextView) -> Boolean = {textView ->  textView.text.isEmpty()}
        if(notEmpty(editTextName)){
            if (CommonUtil.isValidName(editTextName.text.toString(), this)) {
                requestMultiplePermissions()
            }
        }

    }

    private fun parseIntent() {
        val intent = intent
        if (intent.hasExtra(Constants.IntentKeys.PASSWORD)) {
            isLoadAccount = true
            mPassword = intent.getStringExtra(Constants.IntentKeys.PASSWORD)
        } else {
            if (WalletService.getInstance(this).isWalletExists) {
                showWarningDialog()
            }
        }
    }

    protected fun goNext() {
        if (mViewModel.storeData(editTextName.text.toString() + "", mPassword)) {
            val intent = Intent(this@CreateUserActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun goToPasswordPage() {
        if (CommonUtil.isValidName(editTextName.text.toString(), this)) {
            val intent = Intent(this@CreateUserActivity, SecurityActivity::class.java)
            intent.putExtra(Constants.IntentKeys.USER_NAME, editTextName.text.toString() + "")
            intent.putExtra(Constants.IntentKeys.AVATAR_INDEX, mViewModel.imageIndex)
            startActivity(intent)
        }
    }

    private fun showWarningDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(Html.fromHtml("<b>" + getString(R.string.alert_title_text) + "</b>"))
        builder.setMessage(WalletAddressHelper.getWalletSpannableString(this).toString())
        builder.setPositiveButton(Html.fromHtml("<b>" + getString(R.string.button_postivive) + "<b>")) { dialog, arg1 ->
            startActivity(Intent(this@CreateUserActivity, ImportWalletActivity::class.java))
            finish()
        }
        builder.setNegativeButton(Html.fromHtml("<b>" + getString(R.string.negative_button) + "<b>")) { dialog, arg1 -> }
        builder.create()
        builder.show()
    }


}