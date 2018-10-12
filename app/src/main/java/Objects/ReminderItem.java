package Objects;

public class ReminderItem {
    private String vid;
    private String job;
    private String date1;

    public ReminderItem(String vid, String job, String date1) {
        this.vid = vid;
        this.job = job;
        this.date1 = date1;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getjob() {
        return job;
    }

    public void setjob(String job) {
        this.job = job;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }
}
