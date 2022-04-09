package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseSwitch;
    private Switch fatherSideSwitch;
    private Switch motherSideSwitch;
    private Switch maleSwitch;
    private Switch femaleSwitch;
    private ConstraintLayout logoutButton;
    public static final String CHANGED_SETTINGS_KEY = "changedSettings";
    public static final String LOGOUT_KEY = "logoutBool";
    public static final String TAG = "SettingsActivity";
    private boolean changedSettings;

    private boolean originalLifeStoryVal;
    private boolean originalFamilyTreeVal;
    private boolean originalSpouseVal;
    private boolean originalFatherVal;
    private boolean originalMotherVal;
    private boolean originalMaleVal;
    private boolean originalFemaleVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_settings);
        lifeStorySwitch = findViewById(R.id.switch1);
        familyTreeSwitch = findViewById(R.id.switch2);
        spouseSwitch = findViewById(R.id.switch3);
        fatherSideSwitch = findViewById(R.id.switch4);
        motherSideSwitch = findViewById(R.id.switch5);
        maleSwitch = findViewById(R.id.switch6);
        femaleSwitch = findViewById(R.id.switch7);
        logoutButton = findViewById(R.id.LogoutWidget);

        setSwitches();

        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowLifeEventLines();
            }
        });

        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowFamilyLines();
            }
        });

        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowSpouseLine();
            }
        });

        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowFatherSide();
            }
        });

        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowMotherSide();
            }
        });

        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowMale();
            }
        });

        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataCache.getInstance().settings.toggleShowFemale();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResponseResult(true);
            }
        });

    }

    private void setSwitches() {
        originalLifeStoryVal = DataCache.getInstance().settings.isShowLifeEventLines();
        lifeStorySwitch.setChecked(originalLifeStoryVal);
        originalFamilyTreeVal = DataCache.getInstance().settings.isShowFamilyLines();
        familyTreeSwitch.setChecked(originalFamilyTreeVal);
        originalSpouseVal = DataCache.getInstance().settings.isShowSpouseLine();
        spouseSwitch.setChecked(originalSpouseVal);
        originalFatherVal = DataCache.getInstance().settings.isShowFatherSide();
        fatherSideSwitch.setChecked(originalFatherVal);
        originalMotherVal = DataCache.getInstance().settings.isShowMotherSide();
        motherSideSwitch.setChecked(originalMotherVal);
        originalMaleVal = DataCache.getInstance().settings.isShowMale();
        maleSwitch.setChecked(originalMaleVal);
        originalFemaleVal = DataCache.getInstance().settings.isShowFemale();
        femaleSwitch.setChecked(originalFemaleVal);
    }

    private boolean checkChangedSettings() {
        if(DataCache.getInstance().settings.isShowFamilyLines() != originalFamilyTreeVal) {
            return true;
        } else if (DataCache.getInstance().settings.isShowLifeEventLines() !=  originalLifeStoryVal) {
            return true;
        } else if (DataCache.getInstance().settings.isShowSpouseLine() != originalSpouseVal) {
            return true;
        } else if (DataCache.getInstance().settings.isShowFatherSide() != originalFatherVal) {
            return true;
        } else if (DataCache.getInstance().settings.isShowMotherSide() != originalMotherVal) {
            return true;
        } else if (DataCache.getInstance().settings.isShowMale() != originalMaleVal) {
            return true;
        } else if(DataCache.getInstance().settings.isShowFemale() != originalFemaleVal) {
            return true;
        } else {
            return false;
        }
    }


    private void setResponseResult(boolean isLogout) {
        Intent data = new Intent();
        data.putExtra(CHANGED_SETTINGS_KEY, checkChangedSettings());
        data.putExtra(LOGOUT_KEY, isLogout);
        setResult(Activity.RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            setResponseResult(false);
            return true;
        } else {
            Log.d(TAG, "Error in onOptionsItemSelected!!!");
            return super.onOptionsItemSelected(item);
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SettingsActivity.class);
        return intent;
    }
}