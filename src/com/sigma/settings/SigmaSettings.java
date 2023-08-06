/*
 * Copyright (C) 2017-2022 crDroid Android Project
 * Copyright (C) 2023 SigmaDroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sigma.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;

import com.sigma.settings.fragments.Buttons;
import com.sigma.settings.fragments.LockScreen;
import com.sigma.settings.fragments.QuickSettings;
import com.sigma.settings.fragments.Miscellaneous;
import com.sigma.settings.fragments.Navigation;
import com.sigma.settings.fragments.Notifications;
import com.sigma.settings.fragments.Sound;
import com.sigma.settings.fragments.StatusBar;
import com.sigma.settings.fragments.UserInterface;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

@SearchIndexable
public class SigmaSettings extends DashboardFragment {

    private static final String TAG = "SigmaSettings";

    protected Context mContext;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private static final int MENU_RESET = Menu.FIRST;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = getActivity().getApplicationContext();
        hideToolbar();
        setSigmaDashboardStyle();
    }

    private void hideToolbar() {
        if (mCollapsingToolbarLayout == null) {
            mCollapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_toolbar);
        }
        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setVisibility(View.GONE);
        }
    }

    public void onResume() {
        super.onResume();
        hideToolbar();
        setSigmaDashboardStyle();
    }

    private void setSigmaDashboardStyle() {
        int mDashBoardStyle = geSettingstDashboardStyle();
        final PreferenceScreen mScreen = getPreferenceScreen();
        final int mCount = mScreen.getPreferenceCount();
        for (int i = 0; i < mCount; i++) {
            final Preference mPreference = mScreen.getPreference(i);

            String mKey = mPreference.getKey();

            if (mKey == null) continue;

            if (mKey.equals("sigma_header")) {
                mPreference.setLayoutResource(R.layout.settings_sigma_toolbox_header);
                continue;
            }

            if (mDashBoardStyle == 2) { // 0 = stock , 1 = Dot, 2 = Nad, 3 = Sigma
                mPreference.setLayoutResource(R.layout.sigma_dashboard_preference_full);
            } else if (mDashBoardStyle == 1 || mDashBoardStyle == 3){
               if (mKey.equals("ui_settings_category")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_top);
                } else if (mKey.equals("misc_settings_category")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_bottom);
                } else {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_middle); 
                }  
            }
        }
    }

    private int geSettingstDashboardStyle() {
        return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 2, UserHandle.USER_CURRENT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset_settings_title)
                .setIcon(R.drawable.ic_reset)
                .setAlphabeticShortcut('r')
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                        MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    public void resetAll(Context context) {
        new ResetAllTask(context).execute();
    }

    public void showResetAllDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.reset_settings_title)
                .setMessage(R.string.reset_settings_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetAll(context);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private class ResetAllTask extends AsyncTask<Void, Void, Void> {
        private Context rContext;

        public ResetAllTask(Context context) {
            super();
            rContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Buttons.reset(rContext);
            LockScreen.reset(rContext);
            QuickSettings.reset(rContext);
            Miscellaneous.reset(rContext);
            Navigation.reset(rContext);
            Notifications.reset(rContext);
            Sound.reset(rContext);
            StatusBar.reset(rContext);
            UserInterface.reset(rContext);
            finish();
            startActivity(getIntent());
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                 showResetAllDialog(getActivity());
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SIGMA;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_display;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.layout.sigma_settings;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.layout.sigma_settings);
}
