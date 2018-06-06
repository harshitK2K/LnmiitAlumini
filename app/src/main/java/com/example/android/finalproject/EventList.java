package com.example.android.finalproject;

public class EventList {

    private String title;
    private String description;
    private String eventLocation;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String imageUri;

    public EventList(){}
    public EventList(String title, String description, String eventLocation, String startDate, String startTime, String endDate, String endTime, String imageUri) {
        this.title = title;
        this.description = description;
        this.eventLocation = eventLocation;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.imageUri = imageUri;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getImageUri() {
        return imageUri;
    }


}
