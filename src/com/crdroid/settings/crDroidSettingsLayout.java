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
    // ViewPager mViewPager;
    // ViewGroup mContainer;
    // PagerSlidingTabStrip mTabs;
    // SectionsPagerAdapter mSectionsPagerAdapter;
    // protected Context mContext;

    private static final int MENU_RESET = Menu.FIRST;

    @Override
    public void onCreate(Bundle icicle) {
        // super.onCreate(savedInstanceState);
        super.onCreate(icicle);
        // getActivity().setTitle(R.string.crdroid_settings_title);
        setSigmaDashboardStyle();
    }

    public void onResume() {
        super.onResume();
        setSigmaDashboardStyle();
    }

    private void setSigmaDashboardStyle() {
        int mDashBoardStyle = geSettingstDashboardStyle();
        final PreferenceScreen mScreen = getPreferenceScreen();
        if (mScreen == null) return;
        final int mCount = mScreen.getPreferenceCount();
        for (int i = 0; i < mCount; i++) {
            final Preference mPreference = mScreen.getPreference(i);

            String mKey = mPreference.getKey();

            if (mKey == null) continue;

            if (mDashBoardStyle == 1 ){
               if (mKey.equals("ui_settings_category")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_top);
                } else if (mKey.equals("about_crdroid")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_bottom);
                } else {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_middle); 
                }  
            } else if (mDashBoardStyle == 2) {
                mPreference.setLayoutResource(R.layout.nad_dashboard_preference);
            } 
        }
    }

    private int geSettingstDashboardStyle() {
        return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 0, UserHandle.USER_CURRENT);
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //     mContainer = container;
    //     View view = inflater.inflate(R.layout.crdroid_settings, container, false);
    //     mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
    //     mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
    //     mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
    //     mViewPager.setAdapter(mSectionsPagerAdapter);
    //     mViewPager.setClipChildren(true);
    //     mViewPager.setClipToPadding(true);
    //     mTabs.setViewPager(mViewPager);
    //     mContext = getActivity().getApplicationContext();

    //     return view;
    // }

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

    // class SectionsPagerAdapter extends FragmentPagerAdapter {

    //     String titles[] = getTitles();
    //     private Fragment frags[] = new Fragment[titles.length];

    //     public SectionsPagerAdapter(FragmentManager fm) {
    //         super(fm);
    //         frags[0] = new StatusBar();
    //         frags[1] = new QuickSettings();
    //         frags[2] = new LockScreen();
    //         frags[3] = new Navigation();
    //         frags[4] = new Buttons();
    //         frags[5] = new UserInterface();
    //         frags[6] = new Notifications();
    //         frags[7] = new Sound();
    //         frags[8] = new Miscellaneous();
    //         frags[9] = new About();
    //     }

    //     @Override
    //     public Fragment getItem(int position) {
    //         return frags[position];
    //     }

    //     @Override
    //     public int getCount() {
    //         return frags.length;
    //     }

    //     @Override
    //     public CharSequence getPageTitle(int position) {
    //         return titles[position];
    //     }
    // }

    // private String[] getTitles() {
    //     String titleString[];
    //     titleString = new String[] {
    //         getString(R.string.statusbar_title),
    //         getString(R.string.quicksettings_title),
    //         getString(R.string.lockscreen_title),
    //         getString(R.string.navigation_title),
    //         getString(R.string.button_title),
    //         getString(R.string.ui_title),
    //         getString(R.string.notifications_title),
    //         getString(R.string.sound_title),
    //         getString(R.string.misc_title),
    //         getString(R.string.about_crdroid)
    //     };
    //     return titleString;
    // }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
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
        return R.layout.alpha_settings;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.layout.alpha_settings);

}
