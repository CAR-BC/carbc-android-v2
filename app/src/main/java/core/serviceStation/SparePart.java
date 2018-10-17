package core.serviceStation;

public class SparePart {
    private String serial_number;
    private String spare_part;
    private String seller;

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSparepart() {
        return spare_part;
    }

    public void setSparepart(String sparepart) {
        this.spare_part = sparepart;
    }
}
