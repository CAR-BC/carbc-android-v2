package core.consensus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.blockchain.Block;

public class Rating {

    private double value;
    private int mandatory;
    private int specialValidators;
    private int agreementCount;
    private String event;
    private final Logger log = LoggerFactory.getLogger(Rating.class);


    public Rating(String event) {
        this.event = event;
        value = 0;
    }

    public void setMandatory(int mandatory) {
        this.mandatory = mandatory;
    }

    public void setSpecialValidators(int specialValidators) {
        this.specialValidators = specialValidators;
    }

    public void setAgreementCount(int agreementCount) {
        this.agreementCount = agreementCount;
    }

    public double calRating(int mandatory, int secondary) {
        switch (event){
            case "ExchangeOwnership":
                ratingForExchangeOwnership(mandatory);
                break;

            case "ServiceRepair":
                ratingForServiceRepair(mandatory, secondary);
                break;

            case "Insure":
                ratingForInsure(mandatory);
                break;

            case "Lease":
                ratingForLease(mandatory);
                break;

            case "RegisterVehicle":
                ratingForRegisterVehicle(mandatory);
                break;

            case "BuyVehicle":
                ratingForBuyVehicle(mandatory);
                break;
//
//            case "BankLoan":
//                getMandatoryValidators().add(secondaryParties.getJSONObject("Bank")
//                        .getString("publicKey"));
//                break;
//
//            case "RenewRegistration":
//                getMandatoryValidators().add(secondaryParties.getJSONObject("RMV")
//                        .getString("publicKey"));
//                break;
//
//
//            case "RenewInsurance":
//                String insure_PK = secondaryParties.getJSONObject("InsuranceCompany").getString("publicKey");
//                if(insure_PK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
//                    validateBlock();
//                }
//                getMandatoryValidators().add(insure_PK);
//
//            case "BuySpareParts":
//                getMandatoryValidators().add(secondaryParties.getJSONObject("SparePartProvider")
//                        .getString("publicKey"));
//                break;

        }
        return value;
    }

    public void ratingForExchangeOwnership(int mandatory) {
        int mandatoryCount = this.mandatory - mandatory;
        int other = agreementCount - mandatoryCount;
        if(other > 40) { other = 40; }
        value = 30 * mandatoryCount + 1 * other ;
    }

    public void ratingForServiceRepair(int mandatory, int secondary) {
        int mandatoryCount = this.mandatory - mandatory;
        int tempSecondary = this.specialValidators;
        int secondaryCount = this.specialValidators - secondary;
        int other = agreementCount - mandatoryCount - secondaryCount;
        if(other > 20) { other = 20; }
        value = 45 * mandatoryCount + (35/tempSecondary) * secondaryCount + 1 * other;
    }

    public void ratingForInsure(int mandatory) {
        int mandatoryCount = this.mandatory - mandatory;
        int other = agreementCount - mandatoryCount;
        if(other > 20) { other = 20; }
        value = 80 * mandatoryCount + 1 * other ;
    }

    public void ratingForLease(int mandatory) {
        int mandatoryCount = this.mandatory - mandatory;
        int other = agreementCount - mandatoryCount;
        if(other > 20) { other = 20; }
        value = 80 * mandatoryCount + 1 * other ;
    }

    public void ratingForRegisterVehicle(int mandatory) {
        int mandatoryCount = this.mandatory - mandatory;
        int other = agreementCount - mandatoryCount;
        if(other > 50) { other = 50; }
        value = 50 * mandatoryCount + 1 * other ;
    }

    public void ratingForBuyVehicle(int mandatory) {
        int mandatoryCount = this.mandatory - mandatory;
        int other = agreementCount - mandatoryCount;
        if(other > 40) { other = 40; }
        value = 30 * mandatoryCount + 1 * other ;
    }

}
