package edu.byu.cs240.familymapclient;

import android.content.Intent;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Event;
import model.Person;

public class PersonActivityViewModel extends ViewModel {
    private Person currPerson;
    private List<Event> currPersonEvents;
    private List<Person> currPersonFamily;
    private Person currPersonSpouse;
    private Person currPersonMother;
    private Person currPersonFather;
    private Map<String, String> relationships;

    private static final String SPOUSE_TAG = "Spouse";
    private static final String FATHER_TAG = "Father";
    private static final String MOTHER_TAG = "Mother";
    private static final String CHILD_TAG = "Child";


    public void setPersonData(Intent intent) {
        String personID = intent.getStringExtra(PersonActivity.PERSON_ID_KEY);

        relationships = new HashMap<>();
        currPersonFamily = new ArrayList<>();

        currPerson = DataCache.getInstance().getPerson(personID);
        currPersonEvents = DataCache.getInstance().getPersonEvents(personID);
        sortEvents();

        currPersonFather = DataCache.getInstance().getPerson(currPerson.getFatherID());
        if(currPersonFather != null){
            relationships.put(currPersonFather.getPersonID(), FATHER_TAG);
            currPersonFamily.add(currPersonFather);
        }

        currPersonMother = DataCache.getInstance().getPerson(currPerson.getMotherID());
        if(currPersonMother != null){
            relationships.put(currPersonMother.getPersonID(), MOTHER_TAG);
            currPersonFamily.add(currPersonMother);
        }

        currPersonSpouse = DataCache.getInstance().getPerson(currPerson.getSpouseID());
        if(currPersonSpouse != null){
            relationships.put(currPersonSpouse.getPersonID(), SPOUSE_TAG);
            currPersonFamily.add(currPersonSpouse);
        }

        List<String> personChildren = DataCache.getInstance().getPersonChildren(personID);

        if(personChildren != null){
            for(String childID : personChildren) {
                Person child = DataCache.getInstance().getPerson(childID);
                relationships.put(childID, CHILD_TAG);
                currPersonFamily.add(child);
            }
        }

    }

    private void sortEvents() {
        Collections.sort(currPersonEvents, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                if(event.getYear() == t1.getYear()) {
                    String eventString = event.getEventType().toLowerCase();
                    String t1String = event.getEventType().toLowerCase();
                    return eventString.compareTo(t1String);
                } else if (event.getYear() < t1.getYear()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

    }

    public String getRelation(String personID){
        return relationships.get(personID);
    }

    public List<Person> getCurrPersonFamily(){
        return currPersonFamily;
    }

    public List<Event> getCurrPersonEvents() {
        return currPersonEvents;
    }

    public List<Event> getFilteredCurrPersonEvents() {
        List<Event> filteredEvents = new ArrayList<>();
        for(Event event : currPersonEvents){
            if(DataCache.getInstance().checkSettingCompliance(event)){
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public Person getCurrPersonSpouse() {
        return currPersonSpouse;
    }

    public Person getCurrPersonMother() {
        return currPersonMother;
    }

    public Person getCurrPersonFather() {
        return currPersonFather;
    }

    public void setCurrPerson(Person person) {
        currPerson = person;
    }

    public Person getCurrPerson() {
        return currPerson;
    }
}
