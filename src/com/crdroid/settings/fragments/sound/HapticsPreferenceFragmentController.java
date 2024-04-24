/*
 * Copyright (C) 2023 the risingOS android project
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
package com.crdroid.settings.fragments.sound;

import android.os.AsyncTask;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;

import com.android.settingslib.core.AbstractPreferenceController;

import com.crdroid.settings.preferences.CustomSeekBarPreference;

import com.android.internal.util.android.VibrationUtils;

public class HapticsPreferenceFragmentController extends AbstractPreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY = "haptics_settings";
    private static final String KEY_BACK_GESTURE_HAPTIC_INTENSITY = "back_gesture_haptic_intensity";
    private static final String KEY_BRIGHTNESS_SLIDER_HAPTICS_INTENSITY = "qs_brightness_slider_haptic";
    private static final String KEY_EDGE_SCROLLING_HAPTICS_INTENSITY = "edge_scrolling_haptics_intensity";
    private static final String KEY_QS_TILE_HAPTICS_INTENSITY = "qs_panel_tile_haptic";
    private static final String KEY_VOLUME_SLIDER_HAPTICS_INTENSITY = "volume_slider_haptics_intensity";

    private CustomSeekBarPreference mBackIntensity;
    private CustomSeekBarPreference mBrightnessIntensity;
    private CustomSeekBarPreference mEdgeScrollingIntensity;
    private CustomSeekBarPreference mQsTileIntensity;
    private CustomSeekBarPreference mVolumeSliderIntensity;
    
    private Context mContext;

    public HapticsPreferenceFragmentController(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }
    
    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mBackIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_BACK_GESTURE_HAPTIC_INTENSITY);
        mBrightnessIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_BRIGHTNESS_SLIDER_HAPTICS_INTENSITY);
        mEdgeScrollingIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_EDGE_SCROLLING_HAPTICS_INTENSITY);
        mQsTileIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_QS_TILE_HAPTICS_INTENSITY);
        mVolumeSliderIntensity = (CustomSeekBarPreference) screen.findPreference(KEY_VOLUME_SLIDER_HAPTICS_INTENSITY);
        updateSettings();
    }

   private void updateSettings() {
        int backIntensity = Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.BACK_GESTURE_HAPTIC_INTENSITY, 3);
        mBackIntensity.setValue(backIntensity);

        int brightnessIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_BRIGHTNESS_SLIDER_HAPTIC, 1);
        mBrightnessIntensity.setValue(brightnessIntensity);

        int edgeScrollingIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.EDGE_SCROLLING_HAPTICS_INTENSITY, 3);
        mEdgeScrollingIntensity.setValue(edgeScrollingIntensity);

        int volumeSliderIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.VOLUME_SLIDER_HAPTICS_INTENSITY, 1);
        mVolumeSliderIntensity.setValue(volumeSliderIntensity);
        
        int qsTileHapticsIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_PANEL_TILE_HAPTIC, 1);
        mQsTileIntensity.setValue(qsTileHapticsIntensity);

        mBackIntensity.setOnPreferenceChangeListener(this);
        mBrightnessIntensity.setOnPreferenceChangeListener(this);
        mEdgeScrollingIntensity.setOnPreferenceChangeListener(this);
        mQsTileIntensity.setOnPreferenceChangeListener(this);
        mVolumeSliderIntensity.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intensity = (Integer) newValue;
        boolean isChanged = false;
        if (preference == mBackIntensity) {
            Settings.Secure.putInt(mContext.getContentResolver(),
                    Settings.Secure.BACK_GESTURE_HAPTIC_INTENSITY, intensity);
            mBackIntensity.setValue(intensity);
            isChanged = true;
        } else if (preference == mBrightnessIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_BRIGHTNESS_SLIDER_HAPTIC, intensity);
            mBrightnessIntensity.setValue(intensity);
            isChanged = true;
        } else if (preference == mEdgeScrollingIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.EDGE_SCROLLING_HAPTICS_INTENSITY, intensity);
            mEdgeScrollingIntensity.setValue(intensity);
            isChanged = true;
        } else if (preference == mQsTileIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_PANEL_TILE_HAPTIC, intensity);
            mQsTileIntensity.setValue(intensity);
            isChanged = true;
        } else if (preference == mVolumeSliderIntensity) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.VOLUME_SLIDER_HAPTICS_INTENSITY, intensity);
            mVolumeSliderIntensity.setValue(intensity);
            isChanged = true;
        }
        if (isChanged) {
            VibrationUtils.triggerVibration(mContext, intensity);
            return true;
        }

        return false;
    }

}
