package com.sigma.settings.fragments.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.Settings;
import java.util.Arrays;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String initialAnimStyle = "udfps_op_mclaren_recognizing_anim";
            setInitialAnimationStyle(initialAnimStyle, context);

            boolean initialActivatedStatus = true;
            setInitialActivatedStatus(initialActivatedStatus, context);
        }
    }

    private void setInitialAnimationStyle(String styleName, Context context) {
        String mPkg = "com.crdroid.udfps.animations";
        Resources udfpsRes;
        String[] mAnims;
        try {
            PackageManager pm = context.getPackageManager();
            udfpsRes = pm.getResourcesForApplication(mPkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        mAnims = udfpsRes.getStringArray(udfpsRes.getIdentifier("udfps_animation_styles",
                "array", mPkg));
        int index = Arrays.asList(mAnims).indexOf(styleName);
        if (index != -1) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.UDFPS_ANIM_STYLE, index);
        }
    }

    private void setInitialActivatedStatus(boolean activated, Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.UDFPS_ANIM, activated ? 1 : 0);
    }
}