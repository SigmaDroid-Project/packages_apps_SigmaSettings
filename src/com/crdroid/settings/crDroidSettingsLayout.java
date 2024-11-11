/*
 * Copyright (C) 2017-2022 crDroid Android Project
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

package com.crdroid.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.crdroid.settings.fragments.About;
import com.crdroid.settings.fragments.Buttons;
import com.crdroid.settings.fragments.LockScreen;
import com.crdroid.settings.fragments.Miscellaneous;
import com.crdroid.settings.fragments.Navigation;
import com.crdroid.settings.fragments.Notifications;
import com.crdroid.settings.fragments.QuickSettings;
import com.crdroid.settings.fragments.Sound;
import com.crdroid.settings.fragments.StatusBar;
import com.crdroid.settings.fragments.UserInterface;

import com.android.internal.logging.nano.MetricsProto
;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import android.os.UserHandle;
import android.provider.Settings;

public class crDroidSettingsLayout extends DashboardFragment {

    private static final String TAG = "crDroidSettingsLayout";

    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private static final int MENU_RESET = Menu.FIRST;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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
        //mScreen.setTitle(" ");
        if (mScreen == null) return;
        final int mCount = mScreen.getPreferenceCount();
        for (int i = 0; i < mCount; i++) {
            final Preference mPreference = mScreen.getPreference(i);

            String mKey = mPreference.getKey();

            if (mKey == null) continue;

            if (mKey.equals("sigma_header")) {
                mPreference.setLayoutResource(R.layout.settings_sigma_toolbox_header);
                continue;
            }

            if (mDashBoardStyle == 1 ){
                if (mKey.equals("ui_settings_category")) {
                     mPreference.setLayoutResource(R.layout.dot_dashboard_preference_top);
                } else if (mKey.equals("about_sigmadroid")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_bottom);
                } else {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_middle); 
                }  
            } else if (mDashBoardStyle == 2) {
                mPreference.setLayoutResource(R.layout.nad_dashboard_preference);
            } else if (mDashBoardStyle == 3) {
                            if (mKey.equals("ui_settings_category")) {
                    mPreference.setLayoutResource(R.layout.sigma_toolbox_preference_top);
                } else if (mKey.equals("about_sigmadroid")) {
                    mPreference.setLayoutResource(R.layout.sigma_toolbox_preference_bottom);
                } else {
                    mPreference.setLayoutResource(R.layout.sigma_toolbox_preference_middle); 
                } 
            } 
        }
    }


    private int geSettingstDashboardStyle() {
        return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 2, UserHandle.USER_CURRENT);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
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
            Miscellaneous.reset(rContext);
            Navigation.reset(rContext);
            Notifications.reset(rContext);
            QuickSettings.reset(rContext);
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
