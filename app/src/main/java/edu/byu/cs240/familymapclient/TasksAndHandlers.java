package edu.byu.cs240.familymapclient;

public class TasksAndHandlers {
    private static TasksAndHandlers instance;

    private TasksAndHandlers() {}

    public TasksAndHandlers getInstance() {
        if(instance == null) {
            instance = new TasksAndHandlers();
        }
        return instance;
    }


}
