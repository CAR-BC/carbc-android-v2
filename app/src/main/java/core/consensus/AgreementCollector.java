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

public class AgreementCollector extends Thread {

    private String agreementCollectorId;
    private Block block;
    private Agreement[] mandatoryAgreements;
    private ArrayList<String> agreedNodes;
    private Rating rating;
    private int mandatoryCount;
    private int secondaryCount;

    private ArrayList<String> mandatoryValidators;
    private ArrayList<String> specialValidators;
    private ArrayList<Agreement> agreements;
    private BlockJDBCDAO blockJDBCDAO;
    private IdentityJDBC identityJDBC;
    private int threshold;
    private final Logger log = LoggerFactory.getLogger(AgreementCollector.class);

    boolean succeed = false;


    public AgreementCollector(Block block) throws SQLException {
        this.agreementCollectorId = generateAgreementCollectorId(block);
        this.block = block;
        this.agreements = new ArrayList<>();
        this.rating = new Rating(block.getBlockBody().getTransaction().getEvent());
        this.blockJDBCDAO = new BlockJDBCDAO();
        this.identityJDBC = new IdentityJDBC();
        this.mandatoryValidators = new ArrayList<>();
        this.specialValidators = new ArrayList<>();
        this.threshold = 3;

        setMandatoryAgreements();

        //TODO: Here we have assumed that all the agreements come after creating this agreement collector
        //TODO: I have not handled the other case
    }


    public void setMandatoryAgreements() throws SQLException {

        try {
            synchronized (this) {
                String event = this.block.getBlockBody().getTransaction().getEvent();
                JSONObject blockData = new JSONObject(block.getBlockBody().getTransaction().getData());
                System.out.println(blockData);
                JSONObject secondaryParties = blockData.getJSONObject("SecondaryParty");
                JSONObject thirdParties = blockData.getJSONObject("ThirdParty");
                String pubKey;
                secondaryCount = thirdParties.length();
                rating.setSpecialValidators(secondaryCount);


                //TODO: need to check whether parties are real or not before adding to the arrays
                switch (event) {
                    case "ExchangeOwnership":
                        pubKey = secondaryParties.getJSONObject("NewOwner").getString("publicKey");
                        getMandatoryValidators().add(pubKey);
//
//                    if(pubKey.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
//                        succeed = RmvValidation.validateBlock(block);
//                    }

                        //TODO: invoke show notification

                        JSONObject obj = getIdentityJDBC().getIdentityByRole("RMV");
                        pubKey = obj.getString("publicKey");
                        getMandatoryValidators().add(pubKey);

                        break;

                    case "ServiceRepair":
                        pubKey = secondaryParties.getJSONObject("ServiceStation").getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        getMandatoryValidators().add(pubKey);

                        JSONArray sparePartProvider = thirdParties.getJSONArray("SparePartProvider");
                        for (int i = 0; i < sparePartProvider.length(); i++) {
                            getSpecialValidators().add(sparePartProvider.getString(i));
                        }
                        break;

                    case "Insure":
                        pubKey = secondaryParties.getJSONObject("InsuranceCompany")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        if (pubKey.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
//                        validateBlock();
                        }
                        getMandatoryValidators().add(pubKey);
                        break;

                    case "Lease":
                        pubKey = secondaryParties.getJSONObject("LeasingCompany")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        break;

                    case "BankLoan":
                        pubKey = secondaryParties.getJSONObject("Bank")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        break;

                    case "RenewRegistration":
                        pubKey = secondaryParties.getJSONObject("RMV")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        break;

                    case "RegisterVehicle":
//                    pubKey = secondaryParties.getJSONObject("RMV")
//                            .getString("publicKey");

                        JSONObject object = getIdentityJDBC().getIdentityByRole("RMV");
                        pubKey = object.getString("publicKey");
                        getMandatoryValidators().add(object.getString("publicKey"));

//                    getMandatoryValidators().add(pubKey);

                        break;

                    case "RenewInsurance":
                        pubKey = secondaryParties.getJSONObject("InsuranceCompany")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        break;

                    case "BuySpareParts":
                        pubKey = secondaryParties.getJSONObject("SparePartProvider")
                                .getString("publicKey");
                        getMandatoryValidators().add(pubKey);
                        break;

                }
            }
            mandatoryCount = mandatoryValidators.size();
            rating.setMandatory(mandatoryCount);

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMandatoryPartyValid(String role, String pubKey) {
        boolean result = false;
        try{
            IdentityJDBC identityJDBC = new IdentityJDBC();
            JSONObject jsonObject = identityJDBC.getIdentityByAddress(pubKey);

            if (role.equals(jsonObject.getString("role"))) {
                result = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

//    public void setMandotaryAgreementsOld() {
//        String event = this.block.getBlockBody().getTransaction().getEvent();
//
//        JSONObject eventDetail = EventConfigHolder.getInstance()
//                .getEventJson()
//                .getJSONObject(event);
//
//        JSONArray mandatoryValidatorArray = eventDetail
//                .getJSONArray("mandatoryValidators");
//        // InsuranceCompany, LeasingCompany, RMV, ServiceStation
//
//        JSONArray secondaryParties = eventDetail
//                .getJSONObject("params")
//                .getJSONArray("secondaryParty");
//
//        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
//
//        for (int i = 0; i < mandatoryValidatorArray.length(); i++) {
//            String validatorRole = mandatoryValidatorArray.getString(i);
//            boolean isPresent = false;
//
//            for (int j = 0; j < secondaryParties.length(); j++) {
//                JSONObject jsonObject = secondaryParties.getJSONObject(j);
//                String role = jsonObject.getString("role");
//
//                if (role.equals(validatorRole)) {
//                    String secondaryPartyAddress = jsonObject.getString("address");
//
//                    synchronized (this) {
////                        mandatoryValidators.put(validatorRole, secondaryPartyAddress);
//                    }
//
//                    isPresent = true;
//                    break;
//                }
//            }
//            if (!isPresent) {
//
//            }
//        }
//        //now need to check the relevant part is registered as a mandatory validator
//    }

    public synchronized boolean addAgreedNode(String agreedNode) {
        if (!getAgreedNodes().contains(agreedNode)) {
            getAgreedNodes().add(agreedNode);
            return true;
        } else {
            return false;
        }
    }

    //adding agreements
    public synchronized boolean addAgreementForBlock(Agreement agreement) {
        System.out.println("Inside addAgreementForBlock method");
        if (agreementCollectorId.equals(agreement.getBlockHash())) {
            if (!isDuplicateAgreement(agreement)) {
                PublicKey publicKey = KeyGenerator.getInstance().getInstance().getPublicKey(agreement.getPublicKey());
                if (ChainUtil.getInstance().signatureVerification(agreement.getPublicKey(), agreement.getSignedBlock(),
                        agreement.getBlockHash())) {
                    getAgreements().add(agreement);
                    //check for mandatory
                    if (getMandatoryValidators().contains(agreement.getPublicKey())) {
                        System.out.println("mandatory validator received");
                        System.out.println(agreement.getPublicKey());
                        getMandatoryValidators().remove(agreement.getPublicKey());
                        // add rating
                    } else if (getSpecialValidators().contains(agreement.getPublicKey())) {
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
        if (getAgreements().contains(agreement)) {
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

    public Agreement[] getMandatoryAgreements() {
        return mandatoryAgreements;
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

//    public void validateBlock() {
//        try {
//            String serviceData = ServiceJDBCDAO.getInstance().getLastServiceRecord(block.getBlockBody().getTransaction().getAddress()).toString();
//            if(block.getBlockBody().getTransaction().getData().equals(serviceData)) {
//                Consensus.getInstance().sendAgreementForBlock(block.getBlockHeader().getHash());
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

    public Rating getRating() {
        return rating;
    }

    public int getMandatoryArraySize() {
        return mandatoryValidators.size();
    }

    public int getSecondaryArraySize() {
        return specialValidators.size();
    }
}
