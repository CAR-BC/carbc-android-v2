package core.consensus;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import config.EventConfigHolder;
import core.blockchain.Block;
import core.connection.BlockJDBCDAO;
import core.connection.IdentityJDBC;
import network.communicationHandler.MessageSender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AgreementCollector extends Thread{

    private String agreementCollectorId;
    private Block block;
    private Agreement[] mandotaryAgreements;
    private ArrayList<String> agreedNodes;
    private Rating rating;

    private ArrayList<String> mandatoryValidators;
    private ArrayList<String> specialValidators;
    private ArrayList<Agreement> agreements;
    private BlockJDBCDAO blockJDBCDAO;
    private IdentityJDBC identityJDBC;
    private int threshold;
    private final Logger log = LoggerFactory.getLogger(AgreementCollector.class);


    public AgreementCollector(Block block) {
        this.agreementCollectorId = generateAgreementCollectorId(block);
        this.block = block;
        this.agreements = new ArrayList<>();
        this.mandotaryAgreements = new Agreement[2]; //get from the block
        this.rating = new Rating(block);
        this.blockJDBCDAO = new BlockJDBCDAO();
        this.identityJDBC = new IdentityJDBC();
        this.mandatoryValidators = new ArrayList<>();
        this.specialValidators = new ArrayList<>();
        this.threshold = 1;

        setMandatoryAgreements();

        //TODO: Here we have assumed that all the agreements come after creating this agreement collector
        //TODO: I have not handled the other case
    }

    public void setMandatoryAgreements() {
        try {
            synchronized (this) {
                String event = this.block.getBlockBody().getTransaction().getEvent();
                JSONObject blockData = new JSONObject(block.getBlockBody().getTransaction().getData());
                System.out.println(blockData);
                JSONObject secondaryParties = blockData.getJSONObject("SecondaryParty");
                JSONArray thirdParties = blockData.getJSONArray("ThirdParty");

                switch (event) {
                    case "ExchangeOwnership":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("NewOwner").getString("publicKey"));
//                    JSONObject obj = getIdentityJDBC().getIdentityByRole("RMV");
//                    getMandatoryValidators().add(obj.getString("publicKey"));
                        break;

                    case "ServiceRepair":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("ServiceStation")
                                .getString("address"));
                        for (int i = 0; i < thirdParties.length(); i++) {
                            getSpecialValidators().add(thirdParties.getString(i));
                        }
                        break;

                    case "Insure":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("InsuranceCompany")
                                .getString("address"));
                        break;

                    case "Lease":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("LeasingCompany")
                                .getString("address"));
                        break;

                    case "BankLoan":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("Bank")
                                .getString("address"));
                        break;

                    case "RenewRegistration":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("RMV")
                                .getString("address"));
                        break;

                    case "RegisterVehicle":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("RMV")
                                .getString("address"));
                        break;

                    case "RenewInsurance":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("InsuranceCompany")
                                .getString("address"));
                        break;

                    case "BuySpareParts":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("SparePartProvider")
                                .getString("address"));
                        break;

                }
            }

            if (mandatoryValidators.size() > 0) {
                for (int i = 0; i < mandatoryValidators.size(); i++) {
                    System.out.println(mandatoryValidators.get(i));
                }
            }

            if (specialValidators.size() > 0) {
                for (int i = 0; i < specialValidators.size(); i++) {
                    System.out.println(specialValidators.get(i));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMandotaryAgreementsOld(){
        try {
            String event = this.block.getBlockBody().getTransaction().getEvent();

            JSONObject eventDetail = EventConfigHolder.getInstance()
                    .getEventJson()
                    .getJSONObject(event);

            JSONArray mandatoryValidatorArray = eventDetail
                    .getJSONArray("mandatoryValidators");
            // InsuranceCompany, LeasingCompany, RMV, ServiceStation

            JSONArray secondaryParties = eventDetail
                    .getJSONObject("params")
                    .getJSONArray("secondaryParty");

            BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();

            for (int i = 0; i < mandatoryValidatorArray.length(); i++) {
                String validatorRole = mandatoryValidatorArray.getString(i);
                boolean isPresent = false;

                for (int j = 0; j < secondaryParties.length(); j++) {
                    JSONObject jsonObject = secondaryParties.getJSONObject(j);
                    String role = jsonObject.getString("role");

                    if (role.equals(validatorRole)) {
                        String secondaryPartyAddress = jsonObject.getString("address");

                        synchronized (this) {
//                        mandatoryValidators.put(validatorRole, secondaryPartyAddress);
                        }

                        isPresent = true;
                        break;
                    }
                }
                if (!isPresent) {

                }
            }
            //now need to check the relevant part is registered as a mandatory validator

        }catch (Exception  e) {
            e.printStackTrace();


        }
    }

    public synchronized boolean addAgreedNode(String agreedNode) {
        if(!getAgreedNodes().contains(agreedNode)){
            getAgreedNodes().add(agreedNode);
            return true;
        }else {
            return false;
        }
    }

    //adding agreements
    public synchronized boolean addAgreementForBlock(Agreement agreement) {
        System.out.println("Inside addAgreementForBlock method");
        if(agreementCollectorId.equals(agreement.getBlockHash())) {
            if(!isDuplicateAgreement(agreement)) {
                PublicKey publicKey = KeyGenerator.getInstance().getInstance().getPublicKey(agreement.getPublicKey());
                if(ChainUtil.getInstance().signatureVerification(agreement.getPublicKey(), agreement.getSignedBlock(),
                                agreement.getBlockHash())) {
                    getAgreements().add(agreement);
                    //check for mandatory
                    if (getMandatoryValidators().contains(agreement.getPublicKey())){
                        getMandatoryValidators().remove(agreement.getPublicKey());
                        // add rating
                    }else if (getSpecialValidators().contains(agreement.getPublicKey())){
                        getSpecialValidators().remove(agreement.getPublicKey());
                        // add rating
                    }

                    log.info("agreement added for block: {}", agreement.getBlockHash());
                    return true;
                }
            }
        }
        return false;
    }

    public static String generateAgreementCollectorId(Block block) {
        return block.getBlockHeader().getHash();
    }

    //no need synchronizing
    public boolean isDuplicateAgreement(Agreement agreement) {
        if(getAgreements().contains(agreement)) {
            return true;
        }
        return false;
    }

    public Block getBlock() {
        return block;
    }

    public ArrayList<String> getAgreedNodes() {
        return agreedNodes;
    }

    public String getAgreementCollectorId() {
        return agreementCollectorId;
    }

    public Agreement[] getMandotaryAgreements() {
        return mandotaryAgreements;
    }

    public ArrayList<Agreement> getAgreements() {
        return agreements;
    }

    public int getAgreedNodesCount() {
        return getAgreedNodes().size();
    }


    public ArrayList<String> getMandatoryValidators() {
        return mandatoryValidators;
    }

    public ArrayList<String> getSpecialValidators() {
        return specialValidators;
    }

    public BlockJDBCDAO getBlockJDBCDAO() {
        return blockJDBCDAO;
    }

    public int getThreshold() {
        return threshold;
    }

    public IdentityJDBC getIdentityJDBC() {
        return identityJDBC;
    }
}
