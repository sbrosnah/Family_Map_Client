package edu.byu.cs240.familymapclient;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Event;
import model.Person;

public class MapsFragmentViewModel extends ViewModel {
    private Event currentEvent;
    private List<Event> displayedEvents;
    private Polyline currSpouseLine;
    private String name;
    private String eventDetails;
    private List<Polyline> currLifeStoryLines;
    private List<Polyline> currFamilyLines;
    private Set<Marker> displayedMarkers;
    private char gender;


    public List<Polyline> getCurrFamilyLines() {
        return currFamilyLines;
    }

    public void resetCurrFamilyLines() {
        if(currFamilyLines != null) {
            currFamilyLines.clear();
        }
    }

    public void resetCurrLifeStoryLines() {
        if(currLifeStoryLines != null) {
            currLifeStoryLines.clear();
        }
    }

    public void addToDisplayedMarkers(Marker marker) {
        if(displayedMarkers == null) {
            displayedMarkers = new HashSet<>();
        }
        displayedMarkers.add(marker);
    }

    public void resetDisplayedMarkers() {
        if(displayedMarkers != null) {
            displayedMarkers.clear();
        }
    }

    public Set<Marker> getDisplayedMarkers(){return displayedMarkers;}

    public void addToDisplayedEvents(Event event) {
        if(displayedEvents == null) {
            displayedEvents = new ArrayList<>();
        }
        displayedEvents.add(event);
    }

    public void resetDisplayedEvents(){
        if(displayedEvents != null) {
            displayedEvents.clear();
        }
    }

    public List<Event> getDisplayedEvents() {
        return displayedEvents;
    }

    public void addToFamilyLines(Polyline line) {
        if(currFamilyLines == null) {
            currFamilyLines = new ArrayList<>();
        }
        currFamilyLines.add(line);
    }

    public List<Polyline> getCurrLifeStoryLines() {
        return currLifeStoryLines;
    }

    public void addToCurrLifeStoryLines(Polyline line) {
        if(currLifeStoryLines == null) {
            currLifeStoryLines = new ArrayList<>();
        }
        currLifeStoryLines.add(line);
    }

    public void setCurrSpouseLine(Polyline line) {
        currSpouseLine = line;
    }

    public Polyline getCurrSpouseLine() {
        return currSpouseLine;
    }

    public void setCurrentEvent(Event event) {
        currentEvent = event;
        if(event != null) {
            setTextFields();
        }
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    private void setTextFields() {
        Person associatedPerson = DataCache.getInstance().getPerson(currentEvent.getPersonID());
        name = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
        eventDetails = currentEvent.getEventType().toUpperCase() + ": " + currentEvent.getCity() +
                ", " + currentEvent.getCountry() + " (" + currentEvent.getYear() + ")";
        gender = associatedPerson.getGender();
    }

    public String getEventPersonID() {
        if(currentEvent != null) {
            return currentEvent.getPersonID();
        }
        return null;
    }

    public Event getEarliestEvent(List<Event> events) {
        Event earliest = null;
        if(events != null && !events.isEmpty()) {
            earliest = events.get(0);
            for (Event event : events) {
                if(event.getYear() < earliest.getYear()) {
                    earliest = event;
                }
            }
        }
        return earliest;
    }

    public String getName() {
        return name;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public char getGender() {return gender;}


}
