package Objects;

public class EventData {
    private String event;
    private String serviced_date;
    private String rating;

    public EventData(String event, String serviced_date, String rating) {
        this.event = event;
        this.serviced_date = serviced_date;
        this.rating = rating;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getServiced_date() {
        return serviced_date;
    }

    public void setServiced_date(String serviced_date) {
        this.serviced_date = serviced_date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
