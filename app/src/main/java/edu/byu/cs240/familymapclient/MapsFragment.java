package edu.byu.cs240.familymapclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.Event;
import model.Person;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private TextView nameTextView;
    private TextView eventTextView;
    private ImageView genderImageView;
    private ConstraintLayout detailsView;
    private boolean isEventView;

    private static final Float MAX_WIDTH = (float) 20.0;
    private static final Float DIVISOR = (float) 2.0;
    private static final int FAMILY_LINES_COLOR = Color.BLUE;
    private static final int LIFE_STORY_LINES_COLOR = Color.RED;
    private static final int SPOUSE_LINE_COLOR = Color.GREEN;
    private static final String TAG = "MapsFragment";


    private MapsFragmentViewModel getMapsFragmentViewModel() {
        return ViewModelProviders.of(this).get(MapsFragmentViewModel.class);
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            Event startEvent = DataCache.getInstance().getStartEvent();
            LatLng location = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            placeEventMarkers();
            if(!isEventView){
                promptEventSelection();
            } else {
                getMapsFragmentViewModel().setCurrentEvent(DataCache.getInstance().getStartEvent());
                updateDetailsView();
                updateLines();
            }

            googleMap.setOnMarkerClickListener(MapsFragment.this);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);

        isEventView = getArguments().getBoolean(DataCache.IS_EVENT_KEY);

        nameTextView = view.findViewById(R.id.name_text);
        eventTextView = view.findViewById(R.id.event_text);
        genderImageView = view.findViewById(R.id.imageView);
        detailsView = view.findViewById(R.id.details_view);

        detailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMapsFragmentViewModel().getCurrentEvent() != null){
                    switchToPersonActivity();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Event associatedEvent = (Event) marker.getTag();
        getMapsFragmentViewModel().setCurrentEvent(associatedEvent);
        updateDetailsView();
        updateLines();
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!isEventView){
            inflater.inflate(R.menu.fragment_maps, menu);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search){
            switchToSearchActivity();
        } else if (item.getItemId() == R.id.settings){
            switchToSettingsActivity();
        }  else {
            Log.d(TAG, "Error in onOptionsItemSelected!!!");
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void switchToSettingsActivity() {
        Intent intent = SettingsActivity.newIntent(getActivity());
        startActivityForResult(intent, 1);
    }

    public void determineDetailsType() {
        if(!getMapsFragmentViewModel().getDisplayedEvents()
                .contains(getMapsFragmentViewModel().getCurrentEvent())){
            //maybe consider changing the currentEvent
            promptEventSelection();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 1) {
            boolean settingsChanged = data.getBooleanExtra(SettingsActivity.CHANGED_SETTINGS_KEY, false);
            boolean logoutClicked = data.getBooleanExtra(SettingsActivity.LOGOUT_KEY, false);
            if(logoutClicked) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                Fragment loginFragment = new LoginFragment();
                fragmentTransaction.add(R.id.fragment_container, loginFragment);
                fragmentTransaction.commit();
            }else if(settingsChanged) {
                updateCurrentEvent();

                removePreviousEventMarkers();
                getMapsFragmentViewModel().resetDisplayedEvents();
                getMapsFragmentViewModel().resetDisplayedMarkers();

                placeEventMarkers();

                determineDetailsType();

                updateLines();
            }
        }
    }

    private void updateCurrentEvent() {
        if(getMapsFragmentViewModel().getCurrentEvent() != null &&
                !DataCache.getInstance().checkSettingCompliance(getMapsFragmentViewModel().getCurrentEvent())){
            getMapsFragmentViewModel().setCurrentEvent(null);
        }
    }

    private void switchToPersonActivity() {
        Intent intent = PersonActivity.newIntent(getActivity());
        String personID = getMapsFragmentViewModel().getCurrentEvent().getPersonID();
        intent.putExtra(PersonActivity.PERSON_ID_KEY, personID);
        startActivity(intent);
    }

    private void switchToSearchActivity() {
        Intent intent = SearchActivity.newIntent(getActivity());
        startActivity(intent);
    }

    private void updateLines() {
        updateLifeStoryLines();
        updateSpouseLines();
        updateFamilyTreeLines();
    }

    private void checkAndRemoveLines(List<Polyline> lines) {
        if(lines != null && !lines.isEmpty()) {
            for(Polyline eventLine : lines) {
                eventLine.remove();
            }
        }
    }

    //make this one color. This function orders events chronologically. Same width lines
    private void updateLifeStoryLines() {
        List<Polyline> currLifeStoryLines = getMapsFragmentViewModel().getCurrLifeStoryLines();
        checkAndRemoveLines(currLifeStoryLines);

        getMapsFragmentViewModel().resetCurrLifeStoryLines();


        if(DataCache.getInstance().settings.isShowLifeEventLines() == false) {
            return;
        }

        if(getMapsFragmentViewModel().getCurrentEvent() == null) {
            return;
        }

        String personID = getMapsFragmentViewModel().getEventPersonID();
        List<Event> personEvents = DataCache.getInstance().getPersonEvents(personID);

        Collections.sort(personEvents, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                if(event.getYear() == t1.getYear()) {
                    return 0;
                } else if (event.getYear() < t1.getYear()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        for(int i = 0; i < personEvents.size() - 1; i++) {
            Event firstEvent = personEvents.get(i);
            Event secondEvent = personEvents.get(i + 1);

            LatLng start = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());
            LatLng end = new LatLng(secondEvent.getLatitude(), secondEvent.getLongitude());

            Polyline line = drawLine(start, end, LIFE_STORY_LINES_COLOR, MAX_WIDTH);
            getMapsFragmentViewModel().addToCurrLifeStoryLines(line);
        }
    }

    //connects to spouse's first event
    private void updateSpouseLines() {
        if(getMapsFragmentViewModel().getCurrSpouseLine() != null) {
            getMapsFragmentViewModel().getCurrSpouseLine().remove();
        }

        getMapsFragmentViewModel().setCurrSpouseLine(null);

        if(DataCache.getInstance().settings.isShowSpouseLine() == false) {
            return;
        }

        if(getMapsFragmentViewModel().getCurrentEvent() == null) {
            return;
        }

        String personID = getMapsFragmentViewModel().getEventPersonID();
        String spouseID = DataCache.getInstance().getPerson(personID).getSpouseID();

        if(spouseID.equals("") || spouseID == null) {
            return;
        }

        Event earliestSpouseEvent = getMapsFragmentViewModel().
                getEarliestEvent(DataCache.getInstance().getPersonEvents(spouseID));

        if(!DataCache.getInstance().checkSettingCompliance(earliestSpouseEvent)){
            return;
        }

        Event currentEvent = getMapsFragmentViewModel().getCurrentEvent();
        LatLng start = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
        LatLng end = new LatLng(earliestSpouseEvent.getLatitude(), earliestSpouseEvent.getLongitude());

        Polyline line = drawLine(start, end, SPOUSE_LINE_COLOR, MAX_WIDTH);
        getMapsFragmentViewModel().setCurrSpouseLine(line);
    }

    //connects the clicked event to the events persons parents' first events and recursively up through generations
    //This is a different color
    private void updateFamilyTreeLines() {
        List<Polyline> currFamilyLines = getMapsFragmentViewModel().getCurrFamilyLines();
        checkAndRemoveLines(currFamilyLines);

        getMapsFragmentViewModel().resetCurrFamilyLines();

        if(DataCache.getInstance().settings.isShowFamilyLines() == false) {
            return;
        }

        if(getMapsFragmentViewModel().getCurrentEvent() == null) {
            return;
        }

        Event currEvent = getMapsFragmentViewModel().getCurrentEvent();
        Person eventPerson = DataCache.getInstance().getPerson(getMapsFragmentViewModel()
                .getEventPersonID());

        setMotherFatherLines(currEvent, eventPerson, MAX_WIDTH);
    }

    private void updateFamilyTreeLinesHelper(Event event, float prevWidth) {
        Float newWidth = prevWidth / DIVISOR;
        Person eventPerson = DataCache.getInstance().getPerson(event.getPersonID());

        setMotherFatherLines(event, eventPerson, newWidth);
    }

    private void setMotherFatherLines(Event currEvent, Person eventPerson, float width) {

        //Find earliest events of mother and father
        List<Event> motherEvents = DataCache.getInstance()
                .getPersonEvents(eventPerson.getMotherID());
        List<Event> fatherEvents = DataCache.getInstance()
                .getPersonEvents(eventPerson.getFatherID());
        if(motherEvents != null && !motherEvents.isEmpty() && fatherEvents != null
                && !fatherEvents.isEmpty()){
            Event earliestMotherEvent = getMapsFragmentViewModel().getEarliestEvent(motherEvents);
            Event earliestFatherEvent = getMapsFragmentViewModel().getEarliestEvent(fatherEvents);
            //Draw the lines
            LatLng start = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            LatLng endOne = new LatLng(earliestMotherEvent.getLatitude(),
                    earliestMotherEvent.getLongitude());
            LatLng endTwo = new LatLng(earliestFatherEvent.getLatitude(),
                    earliestFatherEvent.getLongitude());

            if(DataCache.getInstance().checkSettingCompliance(earliestMotherEvent)){
                Polyline motherLine = drawLine(start, endOne, FAMILY_LINES_COLOR, width);
                getMapsFragmentViewModel().addToFamilyLines(motherLine);
                updateFamilyTreeLinesHelper(earliestMotherEvent, width);
            }
            if(DataCache.getInstance().checkSettingCompliance(earliestFatherEvent)){
                Polyline fatherLine = drawLine(start, endTwo, FAMILY_LINES_COLOR, width);
                getMapsFragmentViewModel().addToFamilyLines(fatherLine);
                updateFamilyTreeLinesHelper(earliestFatherEvent, width);
            }



        }
    }

    private Polyline drawLine(LatLng start, LatLng end, int color, float width) {
        PolylineOptions options = new PolylineOptions()
                .add(start)
                .add(end)
                .color(color)
                .width(width);
        return map.addPolyline(options);
    }

    private void updateDetailsView() {
        nameTextView.setText(getMapsFragmentViewModel().getName());
        eventTextView.setText(getMapsFragmentViewModel().getEventDetails());
        char gender = getMapsFragmentViewModel().getGender();
        Drawable genderIcon;
        if(gender == 'm'){
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                    .colorRes(R.color.male_icon).sizeDp(40);
        } else {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                    .colorRes(R.color.female_icon).sizeDp(40);
        }
        genderImageView.setImageDrawable(genderIcon);
    }

    private void promptEventSelection() {
        nameTextView.setText("Click on a marker to see event details");
        eventTextView.setText(null);
        Drawable androidIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android)
                .colorRes(R.color.android_icon).sizeDp(40);
        genderImageView.setImageDrawable(androidIcon);
    }

    private Float getEventColor(String eventType) {
        return DataCache.getInstance().getEventColors().get(eventType);
    }

    private void removePreviousEventMarkers() {
        if(getMapsFragmentViewModel().getDisplayedMarkers() == null ||
            getMapsFragmentViewModel().getDisplayedMarkers().isEmpty()) {
            return;
        }
        for(Marker marker : getMapsFragmentViewModel().getDisplayedMarkers()) {
            marker.remove();
        }
    }

    private void placeEventMarkers() {

        ArrayList<Event> events = DataCache.getInstance().getEventList();
        for(Event event : events) {
            if(DataCache.getInstance().checkSettingCompliance(event)){
                LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

                Marker currMarker = map.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(getEventColor(event.getEventType()))));

                getMapsFragmentViewModel().addToDisplayedMarkers(currMarker);
                getMapsFragmentViewModel().addToDisplayedEvents(event);
                currMarker.setTag(event);
            }
        }
    }
}