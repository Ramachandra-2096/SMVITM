package com.example.smvitm;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private CheckBox enableNotificationsCheckBox;
    private CheckBox showTutorialCheckBox;
    private Switch darkModeSwitch;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Find views by their IDs
        enableNotificationsCheckBox = findViewById(R.id.enableNotificationsCheckBox);
        showTutorialCheckBox = findViewById(R.id.showTutorialCheckBox);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        saveButton = findViewById(R.id.saveButton);

        // Load settings and update UI
        loadSettings();

        // Handle "Save" button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        // Handle dark mode switch changes
       /* darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyDarkMode(isChecked);
            }
        });
*/
        // Optional: Handle any other settings related functionality as needed
    }

    private void loadSettings() {
        boolean enableNotifications = sharedPreferences.getBoolean("EnableNotifications", false);
        boolean showTutorial = sharedPreferences.getBoolean("ShowTutorial", true);
        boolean darkMode = sharedPreferences.getBoolean("DarkMode", false);

        enableNotificationsCheckBox.setChecked(enableNotifications);
       // showTutorialCheckBox.setChecked(showTutorial);
       // darkModeSwitch.setChecked(darkMode);
      //  applyDarkMode(darkMode); // Apply dark mode theme on activity create
    }

    private void saveSettings() {
        boolean enableNotifications = enableNotificationsCheckBox.isChecked();
        boolean showTutorial = showTutorialCheckBox.isChecked();
        boolean darkMode = darkModeSwitch.isChecked();

        editor.putBoolean("EnableNotifications", enableNotifications);
        editor.putBoolean("ShowTutorial", showTutorial);
        //editor.putBoolean("DarkMode", darkMode);
        editor.apply();

        // Optional: Perform any other actions on saving settings (e.g., update UI)
    }

    private void applyDarkMode(boolean darkMode) {
        /*if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
*/
        // Recreate the activity to apply the new theme
        recreate();
    }
}
