package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;


public class EventPageDto {

    private int numberOfPages;
    private List<EventDetailsDto> events;

    public EventPageDto(int numberOfPages, List<EventDetailsDto> events) {
        this.numberOfPages = numberOfPages;
        this.events = events;
    }

    public EventPageDto() {
    }


    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<EventDetailsDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventDetailsDto> events) {
        this.events = events;
    }
}
