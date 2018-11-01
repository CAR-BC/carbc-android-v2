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


    public AgreementCollector(Block block) {
        this.agreementCollectorId = generateAgreementCollectorId(block);
        this.block = block;
        this.agreements = new ArrayList<>();
        this.mandatoryAgreements = new Agreement[2]; //get from the block
        this.blockJDBCDAO = new BlockJDBCDAO();
        this.identityJDBC = new IdentityJDBC();
        this.mandatoryValidators = new ArrayList<>();
        this.specialValidators = new ArrayList<>();
        this.threshold = 1;
        rating = new Rating(block.getBlockBody().getTransaction().getEvent());

        setMandatoryAgreements();

        //TODO: Here we have assumed that all the agreements come after creating this agreement collector
        //TODO: I have not handled the other case
    }

    public void setMandatoryAgreements() {

        synchronized (this) {
            String event = null;
            JSONObject secondaryParties = null;
            JSONArray thirdParties = null;
            try {
                event = this.block.getBlockBody().getTransaction().getEvent();
                JSONObject blockData = new JSONObject(block.getBlockBody().getTransaction().getData());
                System.out.println(blockData);
                secondaryParties = blockData.getJSONObject("SecondaryParty");
                thirdParties = blockData.getJSONArray("ThirdParty");
                secondaryCount = thirdParties.length();
                rating.setSpecialValidators(secondaryCount);


                //TODO: need to check whether parties are real or not before adding to the arrays
                switch (event) {
                    case "ExchangeOwnership":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("NewOwner").getString("publicKey"));
//                    JSONObject obj = getIdentityJDBC().getIdentityByRole("RMV");
//                    getMandatoryValidators().add(obj.getString("publicKey"));
                        break;

                    case "ServiceRepair":
                        String serviceStationPK = secondaryParties.getJSONObject("ServiceStation").getString("publicKey");
                        if (serviceStationPK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
                        }
                        getMandatoryValidators().add(serviceStationPK);
                        for (int i = 0; i < thirdParties.length(); i++) {
                            getSpecialValidators().add(thirdParties.getString(i));
                        }
                        break;

                    case "Insure":
                        String insurePK = secondaryParties.getJSONObject("InsuranceCompany").getString("publicKey");
                        if (insurePK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
                        }
                        getMandatoryValidators().add(insurePK);
                        break;

                    case "Lease":
                        String leasePK = secondaryParties.getJSONObject("LeasingCompany").getString("publicKey");
                        if (leasePK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
                        }
                        getMandatoryValidators().add(leasePK);
                        break;

                    case "BankLoan":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("Bank")
                                .getString("publicKey"));
                        break;

                    case "RenewRegistration":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("RMV")
                                .getString("publicKey"));
                        break;

                    case "RegisterVehicle":
                        String RMVPK = secondaryParties.getJSONObject("RMV").getString("publicKey");
                        if (RMVPK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
                        }
                        getMandatoryValidators().add(RMVPK);
                        break;

                    case "RenewInsurance":
                        String insure_PK = secondaryParties.getJSONObject("InsuranceCompany").getString("publicKey");
                        if (insure_PK.equals(KeyGenerator.getInstance().getPublicKeyAsString())) {
                        }
                        getMandatoryValidators().add(insure_PK);

                    case "BuySpareParts":
                        getMandatoryValidators().add(secondaryParties.getJSONObject("SparePartProvider")
                                .getString("publicKey"));
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
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

    }

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
