package Objects;

public class BlockInfo {
    private String vid;
    private String descr;
    private String init_date;

    public BlockInfo(String vid, String descr, String init_date) {
        this.vid = vid;
        this.descr = descr;
        this.init_date = init_date;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getInit_date() {
        return init_date;
    }

    public void setInit_date(String init_date) {
        this.init_date = init_date;
    }
}
