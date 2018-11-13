package Objects;

public class StatusItem {
    private String job;
    private String date1;
    private String condition;
    private String registrationNumber;

    public StatusItem(String job, String date1, String condition, String registrationNumber) {
        this.job = job;
        this.date1 = date1;
        this.condition = condition;
        this.setRegistrationNumber(registrationNumber);
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


    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
