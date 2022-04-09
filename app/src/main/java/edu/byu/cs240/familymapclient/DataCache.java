package edu.byu.cs240.familymapclient;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;

import model.Person;
import model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DataCache {
    public static final String IS_EVENT_KEY = "isEvent";
    private static DataCache instance;

    private static final String TAG = "Datacache";

    private String authtoken;
    private String personID;
    private String username;
    private String firstname;
    private String lastname;
    private Event startEvent;

    private ArrayList<Person> personList;
    private ArrayList<Event> eventList;
    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private Map<String, List<String>> personChildren;
    private Map<String, Float> eventColors;

    private Set<String> maternalAncestors;
    private Set<String> paternalAncestors;
    private Set<String> eventTypes;

    Settings settings = new Settings();

    public static DataCache getInstance() {
        if(instance == null){
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {}



    public void OrganizeData() {
        loadPeople();
        setName();
        loadEvents();
        determinePaternalMaternalAncestors();
    }

    public List<String> getPersonChildren(String personID) {
        if(personChildren.containsKey(personID)){
            return personChildren.get(personID);
        } else {
            return null;
        }
    }

    private void associateEventToPerson(Event event) {
        String eventPersonID = event.getPersonID();
        if(!personEvents.containsKey(eventPersonID)) {
            personEvents.put(eventPersonID, new ArrayList<>());
        }
        personEvents.get(eventPersonID).add(event);
    }

    private void loadEvents() {
        events = new HashMap<>();
        personEvents = new HashMap<>();
        eventTypes = new HashSet<>();
        for(Event event : eventList) {
            events.put(event.getEventID(), event);
            associateEventToPerson(event);
            eventTypes.add(event.getEventType());
        }
        try {
            setEventColors();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }

    private void setEventColors() throws Exception {
        int counter = 0;
        int numColors = 10;
        eventColors = new HashMap<>();
        for(String eventType : eventTypes) {
            Float color;
            switch (counter) {
                case 0:
                    color = BitmapDescriptorFactory.HUE_RED;
                    break;
                case 5:
                    color = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case 8:
                    color = BitmapDescriptorFactory.HUE_YELLOW;
                    break;
                case 3:
                    color = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case 4:
                    color = BitmapDescriptorFactory.HUE_CYAN;
                    break;
                case 7:
                    color = BitmapDescriptorFactory.HUE_AZURE;
                    break;
                case 2:
                    color = BitmapDescriptorFactory.HUE_BLUE;
                    break;
                case 1:
                    color = BitmapDescriptorFactory.HUE_VIOLET;
                    break;
                case 6:
                    color = BitmapDescriptorFactory.HUE_MAGENTA;
                    break;
                case 9:
                    color = BitmapDescriptorFactory.HUE_ROSE;
                    break;
                default:
                    throw new Exception("Problem assigning colors");
            }
            eventColors.put(eventType, color);
            counter = (counter + 1) % numColors;
        }
    }

    public Person getAssociatedPerson(String eventID) {
        Event event = events.get(eventID);
        return people.get(event.getPersonID());
    }

    public Person getPerson(String personID) {
        return people.get(personID);
    }

    private void setName() {
        String selfID = personID;
        Person self = people.get(selfID);
        firstname = self.getFirstName();
        lastname = self.getLastName();
    }

    private void determinePaternalMaternalAncestors() {
        Person rootPerson = people.get(personID);
        String motherID = rootPerson.getMotherID();
        String fatherID = rootPerson.getFatherID();
        paternalAncestors = new HashSet<>();
        maternalAncestors = new HashSet<>();
        if(motherID != null && fatherID != null) {
            maternalAncestors.add(motherID);
            paternalAncestors.add(fatherID);
            addToFamilySide(motherID, maternalAncestors);
            addToFamilySide(fatherID, paternalAncestors);
        }
    }

    private void addToFamilySide(String currID, Set<String> familySideSet) {
        Person currPerson = people.get(currID);
        String motherID = currPerson.getMotherID();
        String fatherID = currPerson.getFatherID();
        if(motherID == null || fatherID == null || motherID.equals("") || fatherID.equals("")) {
            return;
        }
        familySideSet.add(motherID);
        familySideSet.add(fatherID);
        addToFamilySide(motherID, familySideSet);
        addToFamilySide(fatherID, familySideSet);
    }

    private void loadPeople() {
        people = new HashMap<>();
        personChildren = new HashMap<>();
        for(Person person : personList){
            people.put(person.getPersonID(), person);
            addAsChild(person);
        }
    }

    private void addAsChild(Person person) {
        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();
        String childID = person.getPersonID();

        addAsChildHelper(fatherID, childID);
        addAsChildHelper(motherID, childID);
    }

    private void addAsChildHelper(String parentID, String childID) {
        if(personChildren.containsKey(parentID)){
            personChildren.get(parentID).add(childID);
        } else {
            List<String> children = new ArrayList<>();
            children.add(childID);
            personChildren.put(parentID, children);
        }
    }

    public static void reset() {
        instance = null;
    }

    public Event getStartEvent() {
        if(startEvent == null){
            List<Event> mainPersonEvents = personEvents.get(personID);
            Event birthEvent = null;
            for(Event event : mainPersonEvents) {
                if(event.getEventType().toLowerCase().equals("birth")) {
                    birthEvent = event;
                    break;
                }
            }
            assert birthEvent != null;
            return birthEvent;
        } else {
            return startEvent;
        }
    }

    private boolean fitsGenderConstraints(Event event) {
        if(!settings.isShowMale() &&
                getAssociatedPerson(event.getEventID()).getGender() == 'm') {
            return false;
        } else if (!settings.isShowFemale() &&
                getAssociatedPerson(event.getEventID()).getGender() == 'f') {
            return false;
        } else {
            return true;
        }
    }

    private boolean fitsParentalConstraints(Event event) {
        if(!settings.isShowFatherSide() &&
                paternalAncestors.contains(event.getPersonID())){
            return false;
        } else if(!settings.isShowMotherSide() &&
                maternalAncestors.contains(event.getPersonID())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSettingCompliance(Event event) {
        if(!fitsGenderConstraints(event)) {
            return false;
        } else if(!fitsParentalConstraints(event)) {
            return false;
        }
        return true;
    }

    public Event getSetStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Event event) {
        startEvent = event;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public List<Event> getPersonEvents(String personID) {
        return personEvents.get(personID);
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPersonList(ArrayList<Person> personList) {
        this.personList = personList;
    }

    public ArrayList<Person> getPersonList() { return personList;}

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Map<String, Float> getEventColors() {
        return eventColors;
    }
}
