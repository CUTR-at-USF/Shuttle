package edu.usf.sas.pal.muser.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityManager;

import com.google.android.gms.common.GoogleApiAvailability;
import com.simplecity.amp_library.ShuttleApplication;

import edu.usf.sas.pal.muser.constants.EventConstants;
import edu.usf.sas.pal.muser.model.DeviceInfo;
import edu.usf.sas.pal.muser.model.Event;
import edu.usf.sas.pal.muser.util.FirebaseIOUtils;
import edu.usf.sas.pal.muser.util.PreferenceUtils;

import static android.content.Context.ACCESSIBILITY_SERVICE;

public class DeviceInformationManager {

    private Context mApplicationContext;

    public DeviceInformationManager(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;
    }
    public void saveDeviceInformation() {
        PackageManager pm = mApplicationContext.getPackageManager();
        PackageInfo appInfoMuser;
        PackageInfo appInfoGps;
        String muserVersion = "";
        String googlePlayServicesAppVersion = "";
        try {
            appInfoMuser = pm.getPackageInfo(mApplicationContext.getPackageName(),
                    PackageManager.GET_META_DATA);
            muserVersion = appInfoMuser.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Leave version as empty string
        }
        try {
            appInfoGps = pm.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0);
            googlePlayServicesAppVersion = appInfoGps.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Leave version as empty string
        }

        AccessibilityManager am = (AccessibilityManager) mApplicationContext.getSystemService(ACCESSIBILITY_SERVICE);
        Boolean isTalkBackEnabled = am.isTouchExplorationEnabled();

        PowerManager powerManager = (PowerManager) mApplicationContext.getSystemService(Context.POWER_SERVICE);
        Boolean isPowerSaveModeActive = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isPowerSaveModeActive = powerManager.isPowerSaveMode();
        }
        Boolean isIgnoringBatteryOptimizations = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isIgnoringBatteryOptimizations = ShuttleApplication.isIgnoringBatteryOptimizations(mApplicationContext);
        }

        String recordId = Long.toString(System.currentTimeMillis());

        DeviceInfo di = new DeviceInfo(muserVersion, Build.MODEL, Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT, googlePlayServicesAppVersion,
                GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE, isTalkBackEnabled, recordId,
                isPowerSaveModeActive, isIgnoringBatteryOptimizations);
        int hashCode = di.hashCode();
        int mostRecentDeviceHash = PreferenceUtils.getInt(EventConstants.DEVICE_INFO_HASH,
                -1);

        String uid = PreferenceUtils.getString(EventConstants.USER_ID);

        // Update if the device info changed
        if (hashCode != mostRecentDeviceHash && uid != null) {
            FirebaseIOUtils.saveDeviceInfo(di, uid, recordId, hashCode);
        }
    }
}

