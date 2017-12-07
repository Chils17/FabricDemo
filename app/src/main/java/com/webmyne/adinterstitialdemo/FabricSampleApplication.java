package com.webmyne.adinterstitialdemo;

import android.app.Application;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

/**
 * Created by chiragpatel on 28-09-2017.
 */

public class FabricSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Appsee.start(getString(R.string.com_appsee_apikey));
        // Initialize Branch automatic session tracking
        Branch.getAutoInstance(this);

    }
}
