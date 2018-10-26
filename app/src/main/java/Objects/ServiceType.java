package Objects;

import java.util.ArrayList;

public class ServiceType {
    private String serviceType;
    private ArrayList<SparePartData> spareParts;

    public ServiceType(String serviceType, ArrayList<SparePartData> spareParts) {
        this.serviceType = serviceType;
        this.spareParts = spareParts;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public ArrayList<SparePartData> getSpareParts() {
        return spareParts;
    }

    public void setSpareParts(ArrayList<SparePartData> spareParts) {
        this.spareParts = spareParts;
    }
}
