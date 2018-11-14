package core.consensus;

import android.content.Intent;
import android.util.Log;

import com.example.madhushika.carbc_android_v3.MainActivity;
import com.example.madhushika.carbc_android_v3.MyApp;
import com.example.madhushika.carbc_android_v3.NotificationActivity;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import config.EventConfigHolder;
import core.blockchain.Block;
import core.connection.BlockJDBCDAO;
import core.connection.IdentityJDBC;
import core.smartContract.OwnershipExchange;
import core.smartContract.Registration;
import network.communicationHandler.MessageSender;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
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
    private boolean existence = true;


    public AgreementCollector(Block block) throws SQLException {
        this.agreementCollectorId = generateAgreementCollectorId(block);
        this.block = block;
        this.agreements = new ArrayList<>();
        this.rating = new Rating(block.getBlockBody().getTransaction().getEvent());
        this.blockJDBCDAO = new BlockJDBCDAO();
        this.identityJDBC = new IdentityJDBC();
        this.mandatoryValidators = new ArrayList<>();
        this.specialValidators = new ArrayList<>();
        this.threshold = 1;

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
                String myPubKey = KeyGenerator.getInstance().getPublicKeyAsString();


                //TODO: need to check whether parties are real or not before adding to the arrays
                switch (event) {
                    case "ExchangeOwnership":
                        String vehicleId = block.getBlockBody().getTransaction().getAddress();
                        String sender = block.getBlockBody().getTransaction().getSender();

                        OwnershipExchange ownershipExchange = new OwnershipExchange(vehicleId, sender);

                        try {

                            if (ownershipExchange.isAuthorizedToSeller()) {

                                String newOwnerPubKey = secondaryParties.getJSONObject("NewOwner").getString("publicKey");
                                getMandatoryValidators().add(newOwnerPubKey);

                                JSONObject obj = getIdentityJDBC().getIdentityByRole("RMV");
                                String RmvPubKey = obj.getString("publicKey");
                                getMandatoryValidators().add(RmvPubKey);


                                if (newOwnerPubKey.equals(myPubKey)) {
                                    //show notification icon 2
                                    MainActivity.criticalNotificationList.add(block);
                                    Intent intent = new Intent("newCriticalBlockReceived");
                                    intent.putExtra("newCriticalBlockReceived", "newCriticalBlock");
                                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                    MyApp.getContext().sendBroadcast(intent);

                                } else if (RmvPubKey.equals(myPubKey)) {
                                    //show notification in service station
                                } else {
                                    //show notification icon 1
                                    MainActivity.notificationList.add(block);
                                    Intent intent = new Intent("newBlockReceived");
                                    intent.putExtra("newNomApprovedBlockReceived", "newBlock");
                                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                    MyApp.getContext().sendBroadcast(intent);
                                }

                            }
                            System.out.println("mandatory validators is = " + getMandatoryValidators().size());

                        } catch (NullPointerException e) {
                            System.out.println("error occurred in smart contract");
                        } finally {
                            break;
                        }


                    case "ServiceRepair":
                        boolean show = true;
                        String serviceStationPubKey = secondaryParties.getJSONObject("serviceStation").getString("publicKey");
                        getMandatoryValidators().add(serviceStationPubKey);

                        if (isMandatoryPartyValid("ServiceStation", serviceStationPubKey)) {

                            JSONArray sparePartProvider = thirdParties.getJSONArray("SparePartProvider");
                            for (int i = 0; i < sparePartProvider.length(); i++) {
                                String sparePartPubKey = sparePartProvider.getString(i);
                                getSpecialValidators().add(sparePartPubKey);

                                if (sparePartPubKey.equals(myPubKey)) {
                                    System.out.println("I am a spare part provider");
                                    show = false;
                                    //show notification in notification icon 2

                                    MainActivity.criticalNotificationList.add(block);
                                    Intent intent = new Intent("MainActivity");
                                    intent.putExtra("newCriticalBlockReceived", "newCriticalBlock");
                                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                    System.out.println("+++++++++++newCriticalBlockReceived++++++++++");
                                    MyApp.getContext().sendBroadcast(intent);
                                }
                            }
                            if (show) {
                                //show notification in notification icon 1

                                MainActivity.notificationList.add(block);
                                Intent intent = new Intent("MainActivity");
                                intent.putExtra("newNomApprovedBlockReceived", "newBlock");
                                System.out.println("+++++++++++newNomApprovedBlockReceived++++++++++");

                                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                MyApp.getContext().sendBroadcast(intent);

                            }
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
                        log.info("executing Registration smart contract");
                        Registration registrationSmartContract = new Registration(blockData);

                        if (registrationSmartContract.isAuthorized()){
                            //show notification in notification icon 1
                            if (!myPubKey.equals(blockData.getString("currentOwner"))){
                                MainActivity.notificationList.add(block);
                                Intent intent = new Intent("MainActivity");
                                intent.putExtra("newNomApprovedBlockReceived", "newBlock");
                                System.out.println("+++++++++++newNomApprovedBlockReceived++++++++++");

                                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                MyApp.getContext().sendBroadcast(intent);

                            }
                            log.info("Registration is authorized");

                            JSONObject object = getIdentityJDBC().getIdentityByRole("RMV");
                            String rmvPubKey = object.getString("publicKey");

                            if (isMandatoryPartyValid("RMV", rmvPubKey)){
                                getMandatoryValidators().add(object.getString("publicKey"));
                                log.info("added RMV to mandatory validator array");
                                log.info("no of mandatory validators: {}", getMandatoryValidators().size());
                            }
                        }else{
                            log.info("Registration is not authorized");
                            existence = false;
                        }

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

                    case "BuyVehicle":
                        String vehicleNumber = block.getBlockBody().getTransaction().getAddress();
//                    String preOwner = blockData.getString("preOwner");
                        String preOwner = secondaryParties.getJSONObject("PreOwner").getString("publicKey");

                        log.info("initializing OwnershipExchange smart contract");
                        OwnershipExchange ownershipExchge = new OwnershipExchange(vehicleNumber, preOwner);

                        try{
                            if (ownershipExchge.isAuthorizedToSeller()){
                                log.info("seller is authorized");
                                String preOwnerPubKey = secondaryParties.getJSONObject("PreOwner").getString("publicKey");
                                getMandatoryValidators().add(preOwnerPubKey);
                                log.info("added new owner to mandatory validator array");
                                log.info("no of mandatory validators: {}", getMandatoryValidators().size());

                                JSONObject obj = getIdentityJDBC().getIdentityByRole("RMV");
                                String RmvPubKey = obj.getString("publicKey");
                                getMandatoryValidators().add(RmvPubKey);
                                log.info("added RMV to mandatory validator array");
                                log.info("no of mandatory validators: {}", getMandatoryValidators().size());

                                if(preOwnerPubKey.equals(myPubKey)) {
                                    //show notification icon 2
                                    MainActivity.criticalNotificationList.add(block);
                                    //TODO: remove this line
                                    MainActivity.notificationList.add(block);
                                    Intent intent = new Intent("MainActivity");
                                    intent.putExtra("newCriticalBlockReceived", "newCriticalBlock");
                                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                    System.out.println("+++++++++++newCriticalBlockReceived++++++++++");
                                    MyApp.getContext().sendBroadcast(intent);

//                                    Thread.sleep(6000);
//                                    log.info("pre owner sending agreements");
//                                    Consensus.getInstance().sendAgreementForBlock(block.getBlockHeader().getHash());
                                }
                            }else{
                                log.info("seller is not authorized");
                                log.info("smart contract failed");
                            }

                        }catch (NullPointerException e){
                            System.out.println("error occurred in smart contract");
                        } finally {
                            break;
                        }

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
        try {
            IdentityJDBC identityJDBC = new IdentityJDBC();
            JSONObject jsonObject = identityJDBC.getIdentityByAddress(pubKey);

            if (role.equals(jsonObject.getString("role"))) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


    private void sendBlockToNotification() {
        Intent intent = new Intent("ReceivedTransactionData");
        intent.putExtra("nonAprovrdBlock", (Serializable) block);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        MyApp.getContext().sendBroadcast(intent);
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
                        System.out.println("Agreement received from a mandatory validator");
                        System.out.println(agreement.getPublicKey());
                        System.out.println("mandatory validator size = " + getMandatoryValidators().size());
                        getMandatoryValidators().remove(agreement.getPublicKey());
                        System.out.println("mandatory validator size = " + getMandatoryValidators().size());
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

    public boolean isExistence() {
        return existence;
    }
}
