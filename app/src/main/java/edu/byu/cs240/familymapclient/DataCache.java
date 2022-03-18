package edu.byu.cs240.familymapclient;

import model.Person;
import model.Event;

import java.util.ArrayList;
import java.util.Map;

public class DataCache {
    private static DataCache instance;
    private String authtoken;
    private String personID;
    private String username;
    private ArrayList<Person> personList;
    private ArrayList<Event> eventList;
    private Map<String, Person> people;


    public static DataCache getInstance() {
        if(instance == null){
            instance = new DataCache();
        }
        return instance;
    }

    private DataCache() {}

    /*
    Map<String, Person> people;
    Map<String, Event> events;
    Map<String, List<Event>> personEvents;
    Set<String> paternalAncestors;
    Set<String> maternalAncestors;

    Settings settings;
    */
    //All the methods needed.

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

    public ArrayList<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(ArrayList<Person> personList) {
        this.personList = personList;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public void setPeople(Map<String, Person> people) {
        this.people = people;
    }
}
