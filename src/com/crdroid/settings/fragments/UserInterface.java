/*
 * Copyright (C) 2016-2024 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crdroid.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.crdroid.CustomUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.crdroid.settings.fragments.ui.DozeSettings;
import com.crdroid.settings.fragments.ui.SmartPixels;
import com.crdroid.settings.fragments.ui.MonetSettings;

import java.util.List;

@SearchIndexable
public class UserInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    public static final String TAG = "UserInterface";

    private static final String KEY_FORCE_FULL_SCREEN = "display_cutout_force_fullscreen_settings";
    private static final String SMART_PIXELS = "smart_pixels";
    private static final String KEY_DASHBOARD_STYLE = "settings_dashboard_style";
    private static final String KEY_SETTINGS_STORAGE_WIDGET = "settings_storage_widget";
    private static final String KEY_SETTINGS_BATTERY_WIDGET = "settings_battery_widget";

    private Preference mShowCutoutForce;
    private Preference mSmartPixels;
    private ListPreference mDashBoardStyle;
    private SwitchPreference mHomepageStorageWidgetToggle;
    private SwitchPreference mHomepageBatteryWidgetToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.crdroid_settings_ui);

        Context mContext = getActivity().getApplicationContext();
        final PreferenceScreen prefScreen = getPreferenceScreen();
       // final Context mContext = getActivity().getApplicationContext();
       final ContentResolver resolver = mContext.getContentResolver();
	    final String displayCutout =
            mContext.getResources().getString(com.android.internal.R.string.config_mainBuiltInDisplayCutout);

        if (TextUtils.isEmpty(displayCutout)) {
            mShowCutoutForce = (Preference) findPreference(KEY_FORCE_FULL_SCREEN);
            prefScreen.removePreference(mShowCutoutForce);
        }

        mSmartPixels = (Preference) prefScreen.findPreference(SMART_PIXELS);
        boolean mSmartPixelsSupported = getResources().getBoolean(
                com.android.internal.R.bool.config_supportSmartPixels);
        if (!mSmartPixelsSupported)
            prefScreen.removePreference(mSmartPixels);

        mHomepageStorageWidgetToggle = (SwitchPreference) findPreference(KEY_SETTINGS_STORAGE_WIDGET);
        mHomepageBatteryWidgetToggle = (SwitchPreference) findPreference(KEY_SETTINGS_BATTERY_WIDGET);
        mDashBoardStyle = (ListPreference) findPreference(KEY_DASHBOARD_STYLE);
        mDashBoardStyle.setOnPreferenceChangeListener(this);
        int dashboardStyle = Settings.System.getIntForUser(resolver,
                Settings.System.SETTINGS_DASHBOARD_STYLE , 0, UserHandle.USER_CURRENT);
                updateSettingsWidgets(dashboardStyle );

        mHomepageBatteryWidgetToggle.setChecked(Settings.System.getIntForUser(getActivity().getContentResolver(),
                "settings_battery_widget", 0, UserHandle.USER_CURRENT) != 0);
        mHomepageStorageWidgetToggle.setChecked(Settings.System.getIntForUser(getActivity().getContentResolver(),
                "settings_storage_widget", 0, UserHandle.USER_CURRENT) != 0);

        mHomepageStorageWidgetToggle.setOnPreferenceChangeListener(this);
        mHomepageBatteryWidgetToggle.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mDashBoardStyle) {
            int value = Integer.parseInt((String) newValue);
            updateSettingsWidgets(value);
            CustomUtils.showSettingsRestartDialog(getContext());
            return true;
        } 
        if (preference == mHomepageStorageWidgetToggle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(), "settings_storage_widget", value ? 1 : 0);
			CustomUtils.showSettingsRestartDialog(getContext());
            return true;
		} 
        if (preference == mHomepageBatteryWidgetToggle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(), "settings_battery_widget", value ? 1 : 0);
			CustomUtils.showSettingsRestartDialog(getContext());
            return true;
		}
        return false;
    }

    private void updateSettingsWidgets(int dashboardStyle) {
        mHomepageBatteryWidgetToggle.setEnabled(dashboardStyle == 0);
        mHomepageStorageWidgetToggle.setEnabled(dashboardStyle == 0);
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.CHARGING_ANIMATION, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.ENABLE_ROTATION_BUTTON, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SETTINGS_DASHBOARD_STYLE , 0, UserHandle.USER_CURRENT);
        DozeSettings.reset(mContext);
        MonetSettings.reset(mContext);
        SmartPixels.reset(mContext);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SIGMA;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.crdroid_settings_ui) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);

	                final String displayCutout =
                        context.getResources().getString(com.android.internal.R.string.config_mainBuiltInDisplayCutout);

                    if (TextUtils.isEmpty(displayCutout)) {
                        keys.add(KEY_FORCE_FULL_SCREEN);
                    }

                    boolean mSmartPixelsSupported = context.getResources().getBoolean(
                            com.android.internal.R.bool.config_supportSmartPixels);
                    if (!mSmartPixelsSupported)
                        keys.add(SMART_PIXELS);

                    return keys;
                }
            };
}
