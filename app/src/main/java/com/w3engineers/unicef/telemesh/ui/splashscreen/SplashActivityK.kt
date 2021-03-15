package com.w3engineers.unicef.telemesh.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.w3engineers.unicef.telemesh.R
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator
import com.w3engineers.unicef.telemesh.databinding.ActivitySplashBinding
import com.w3engineers.unicef.telemesh.ui.main.MainActivity
import com.w3engineers.unicef.telemesh.ui.profilechoice.ProfileChoiceActivity
import com.w3engineers.unicef.util.helper.CommonUtil
import com.w3engineers.walleter.wallet.WalletService
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

class SplashActivityK: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashViewModel = getViewModel()
        val activitySplashBinding: ActivitySplashBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_splash)

        activitySplashBinding.setSplashViewModel(splashViewModel)

        MeshDataSource.isPrepared = false

        splashViewModel.getUserRegistrationStatus()

        //val shimmerFrameLayout = findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout)
        // no findViewById required now
        shimmerFrameLayout.startShimmer()


        if (CommonUtil.isEmulator()) {
            WalletService.getInstance(this).deleteExistsWallet()
        }


        splashViewModel.isUserRegistered.observe(this, { aBoolean: Boolean? ->
            val intent: Intent
            intent = if (aBoolean != null && aBoolean) {
                // Go to contact page
                Timber.d("User already created. Go next page")
                Intent(this, MainActivity::class.java)
            } else {
                Timber.e("User not created. Go User create page")
                Intent(this, ProfileChoiceActivity::class.java)
            }
            startActivity(intent)
            finish()
        })
    }



    fun getViewModel(): SplashViewModel{
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ServiceLocator.getInstance().getSplashViewModel(application) as T
            }
        }).get(SplashViewModel::class.java)
    }
}