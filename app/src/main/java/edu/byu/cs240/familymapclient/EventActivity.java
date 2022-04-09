package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class EventActivity extends AppCompatActivity {

    private EventActivityViewModel getEventActivityViewModel() {
        return ViewModelProviders.of(this).get(EventActivityViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment =
                fragmentManager.findFragmentById(R.id.map_fragment_container);
        if(currentFragment == null){
            Bundle bundle = new Bundle();
            bundle.putBoolean(DataCache.IS_EVENT_KEY, true);
            MapsFragment fragment = new MapsFragment();
            fragment.setArguments(bundle);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.map_fragment_container, fragment)
                    .commit();
        }


    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        return intent;
    }
}