package Objects;

public class StatusItem {
    private String job;
    private String date1;
    private String condition;

    public StatusItem(String job, String date1, String condition) {
        this.job = job;
        this.date1 = date1;
        this.condition = condition;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
