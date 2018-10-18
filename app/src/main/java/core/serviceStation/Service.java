package core.serviceStation;

public class Service {
    private int record_id;
    private int service_id;
    private String spare_part_serial_number;

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public String getSparePart() {
        return spare_part_serial_number;
    }

    public void setSparePart(String sparePart) {
        this.spare_part_serial_number = sparePart;
    }
}
