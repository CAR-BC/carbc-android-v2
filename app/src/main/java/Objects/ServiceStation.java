package Objects;

public class ServiceStation {
    private String name;
    private String address;
    private String publicKey;
    private String role;
    private String longtitude;
    private String lattitude;

    public ServiceStation(String name, String address) {
        this.name = name;
        this.address = address;

    }

    public ServiceStation(String name, String address, String publicKey, String role, String lattitude, String longtitude) {
        this.name = name;
        this.address = address;
        this.setPublicKey(publicKey);
        this.setRole(role);
        this.lattitude = lattitude;
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getLattitude() {
        return lattitude;
    }
}
