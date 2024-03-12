/*
 * Copyright (C) 2016-2023 crDroid Android Project
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
package com.sigma.settings.fragments;

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
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.internal.util.crdroid.CustomUtils;
import com.sigma.settings.fragments.ui.MonetSettings;

import java.util.List;

@SearchIndexable
public class UserInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    public static final String TAG = "UserInterface";

    private static final String KEY_DASHBOARD_STYLE = "settings_dashboard_style";
    private static final String KEY_SETTINGS_STORAGE_WIDGET = "settings_storage_widget";
    private static final String KEY_SETTINGS_BATTERY_WIDGET = "settings_battery_widget";

    private ListPreference mDashBoardStyle;
    private SwitchPreference mHomepageStorageWidgetToggle;
    private SwitchPreference mHomepageBatteryWidgetToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        addPreferencesFromResource(R.xml.sigma_settings_ui);

        mDashBoardStyle = (ListPreference) findPreference(KEY_DASHBOARD_STYLE);
        mDashBoardStyle.setOnPreferenceChangeListener(this);
        mHomepageStorageWidgetToggle = (SwitchPreference) findPreference(KEY_SETTINGS_STORAGE_WIDGET);
        mHomepageBatteryWidgetToggle = (SwitchPreference) findPreference(KEY_SETTINGS_BATTERY_WIDGET);

        mHomepageBatteryWidgetToggle.setChecked(Settings.System.getIntForUser(getActivity().getContentResolver(),
                "settings_battery_widget", 0, UserHandle.USER_CURRENT) != 0);
        mHomepageStorageWidgetToggle.setChecked(Settings.System.getIntForUser(getActivity().getContentResolver(),
                "settings_storage_widget", 0, UserHandle.USER_CURRENT) != 0);

        mHomepageStorageWidgetToggle.setOnPreferenceChangeListener(this);
        mHomepageBatteryWidgetToggle.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDashBoardStyle) {
            CustomUtils.showSettingsRestartDialog(getContext());
            return true;
        } 
        if (preference == mHomepageStorageWidgetToggle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(), "settings_storage_widget", value ? 1 : 0);
			CustomUtils.showSettingsRestartDialog(getContext());
        return true;
		} else if (preference == mHomepageBatteryWidgetToggle) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(), "settings_battery_widget", value ? 1 : 0);
			CustomUtils.showSettingsRestartDialog(getContext());
        return true;
		}
        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.ENABLE_FLOATING_ROTATION_BUTTON, 1, UserHandle.USER_CURRENT);

        //MonetSettings.reset(mContext);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SIGMA;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.sigma_settings_ui) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
            };
}
