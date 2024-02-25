package com.example.eventsearch;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EventDetailsViewModel extends ViewModel {
    private MutableLiveData<String> venueName = new MutableLiveData<>();

    public String eventId = "";
    public String artistName = "";


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LiveData<String> getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName.setValue(venueName);
    }
}