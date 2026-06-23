package com.clemm.notepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private void applySelectedTheme() {
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_use_black_theme", false) ? R.style.DarkTheme : R.style.LightTheme);
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        applySelectedTheme();
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals("pref_key_use_black_theme")) {
            finish();
            startActivity(new Intent(this, (Class<?>) SettingsActivity.class));
            setResult(-1, new Intent());
        }
    }
}




