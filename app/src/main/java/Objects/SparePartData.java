package Objects;

public class SparePartData {
    private String seller;
    private String sparePart;

    public SparePartData(String seller, String sparePart) {
        this.seller = seller;
        this.sparePart = sparePart;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSparePart() {
        return sparePart;
    }

    public void setSparePart(String sparePart) {
        this.sparePart = sparePart;
    }
}
