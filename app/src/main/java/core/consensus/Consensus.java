package core.consensus;

import android.content.Intent;

import com.example.madhushika.carbc_android_v3.MyApp;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.blockchain.Block;
import core.blockchain.BlockInfo;
import core.blockchain.Transaction;
import core.connection.BlockJDBCDAO;
import core.connection.HistoryDAO;
import core.connection.Identity;
import core.smartContract.BlockValidity;
import network.Neighbour;
import network.Node;
import core.smartContract.TimeKeeper;
import network.communicationHandler.MessageSender;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class Consensus {

    private static Consensus consensus;
    private final Logger log = LoggerFactory.getLogger(Consensus.class);
    private ArrayList<Block> nonApprovedBlocks;
    private ArrayList<AgreementCollector> agreementCollectors;
    private ArrayList<Block> approvedBlocks;
    //to automate agreement process
    private ArrayList<Transaction> addedTransaction;

    private Consensus() {
        nonApprovedBlocks = new ArrayList<>();
        agreementCollectors = new ArrayList<>();
        approvedBlocks = new ArrayList<>();
    }

    public static Consensus getInstance() {
        if (consensus == null) {
            consensus = new Consensus();
        }
        return consensus;
    }

    //block broadcasting and sending agreements

    public void broadcastBlock(Block block, String data) {
        HistoryDAO historyDAO = new HistoryDAO();
        try {
            historyDAO.saveBlockWithAdditionalData(block, data);
            handleNonApprovedBlock(block);
            MessageSender.broadCastBlock(block);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void handleNonApprovedBlock(Block block) throws SQLException {
        if (!isDuplicateBlock(block)) {
            if (ChainUtil.signatureVerification(block.getBlockBody().getTransaction().getSender(),
                    block.getBlockHeader().getSignature(), block.getBlockHeader().getHash())) {

                log.info("signature verified for block: ", block.getBlockHeader().getBlockNumber());

                boolean isPresent = false;
                if (getNonApprovedBlocks().size() > 0) {
                    for (Block b : this.getNonApprovedBlocks()) {
                        if (b.getBlockHeader().getPreviousHash().equals(block.getBlockHeader().getPreviousHash())) {
                            isPresent = true;
                            break;
                        }
                    }
                }

//                getNonApprovedBlocks().add(block);
//                addBlockToNonApprovedBlocks(block);
                this.nonApprovedBlocks.add(block);
                //TODO: should notify the ui
//                WebSocketMessageHandler.testUpdate(nonApprovedBlocks);
                //broadcast json
                Intent intent = new Intent("newBlockReceived");
                intent.putExtra("newBlockReceived", "newBlockAdded");
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                MyApp.getContext().sendBroadcast(intent);

                if (!isPresent) {
                    TimeKeeper timeKeeper = new TimeKeeper(block.getBlockHeader().getPreviousHash());
                    timeKeeper.start();
                }

                AgreementCollector agreementCollector = new AgreementCollector(block);
                System.out.println("agreementcolletor ID: " + agreementCollector.getAgreementCollectorId());
                agreementCollectors.add(agreementCollector);
                if (agreementCollector.succeed) {
                    String blockHash = block.getBlockHeader().getHash();
                    String digitalSignature = ChainUtil.digitalSignature(block.getBlockHeader().getHash());
                    String signedBlock = digitalSignature;
                    Agreement agreement = new Agreement(digitalSignature, signedBlock, blockHash,
                            KeyGenerator.getInstance().getPublicKeyAsString());

                    Consensus.getInstance().handleAgreement(agreement);
                }

                log.info("agreement Collector added, size: {}", agreementCollectors.size());

                //now need to check the relevant party is registered as with desired roles
                //if want, we can check the validity of the block creator/transaction creator

            }

        }
    }

    public void checkAgreementsForBlock(String preBlockHash) throws SQLException, ParseException {
        System.out.println("Inside checkAgreementsForBlock method");

        ArrayList<Block> qualifiedBlocks = new ArrayList<>();
        for (Block b : this.getNonApprovedBlocks()) {
            System.out.println("inside for loop");

            if (b.getBlockHeader().getPreviousHash().equals(preBlockHash)) {
                String blockHash = b.getBlockHeader().getHash();
                AgreementCollector agreementCollector = getAgreementCollector(blockHash);

                synchronized (agreementCollectors) {
                    if (agreementCollector.getMandatoryValidators().size() == 0) {
                        System.out.println("inside if (agreementCollector.getMandatoryValidators().size() == 0)");
                        int agreementCount = agreementCollector.getAgreements().size();
                        if (agreementCount >= agreementCollector.getThreshold()) {
                            System.out.println("adding to qualified blocks");
                            qualifiedBlocks.add(b);

                            //rating calculations
                            agreementCollector.getRating().setAgreementCount(agreementCount);
                            double rating = agreementCollector.getRating().calRating(agreementCollector.
                                    getMandatoryArraySize(), agreementCollector.getSecondaryArraySize());
                            b.getBlockHeader().setRating(rating);
                            this.agreementCollectors.remove(agreementCollector);
                        }
                    } else {
                        //blocks with insufficient agreements
                    }
                }
            }
        }
        addBlockToBlockchain(qualifiedBlocks);
    }

    public Block selectQualifiedBlock(ArrayList<Block> qualifiedBlocks) throws SQLException, ParseException {
        System.out.println("inside consensus/selectQualifiedBlock");

        Block qualifiedBlock = null;

        if (qualifiedBlocks.size() != 0) {
            System.out.println("inside if block; qualifiedBlocks.size() != 0");
            qualifiedBlock = qualifiedBlocks.get(0);

            Timestamp blockTimestamp = ChainUtil.convertStringToTimestamp(qualifiedBlock.getBlockHeader().getBlockTime());

            synchronized (getNonApprovedBlocks()) {
                for (Block b : qualifiedBlocks) {
                    if (blockTimestamp.after(ChainUtil.convertStringToTimestamp(b.getBlockHeader().getBlockTime()))) {
//                        this.getNonApprovedBlocks().remove(qualifiedBlock);
                        removeBlockFromNonApprovedBlocks(qualifiedBlock);
                        qualifiedBlock = b;
                        blockTimestamp = ChainUtil.convertStringToTimestamp(b.getBlockHeader().getBlockTime());
                    } else {
//                        this.getNonApprovedBlocks().remove(b);
                        removeBlockFromNonApprovedBlocks(b);
                    }
                }
            }
            //TODO: for now we discard all delayed blocks. only consider blocks that got enough agreements within the specific time period
//            this.approvedBlocks.add(qualifiedBlock);
        } else {
            //need to restart the timer again
        }
        return qualifiedBlock;
    }

    public void addBlockToBlockchain(ArrayList<Block> qualifiedBlocks) throws SQLException, ParseException {
        System.out.println("inside Consensus/addBlockToBlockchain()");
        Block block = selectQualifiedBlock(qualifiedBlocks);

        if (block != null) {
            System.out.println("if (block != null)");

            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setPreviousHash(block.getBlockHeader().getPreviousHash());
            blockInfo.setHash(block.getBlockHeader().getHash());
            blockInfo.setBlockTime(ChainUtil.convertStringToTimestamp(block.getBlockHeader().getBlockTime()));
            blockInfo.setBlockNumber(block.getBlockHeader().getBlockNumber());
            blockInfo.setTransactionId(block.getBlockBody().getTransaction().getTransactionId());
            blockInfo.setSender(block.getBlockBody().getTransaction().getSender());
            blockInfo.setEvent(block.getBlockBody().getTransaction().getEvent());
            blockInfo.setData(block.getBlockBody().getTransaction().getData().toString());
            blockInfo.setAddress(block.getBlockBody().getTransaction().getAddress());
            blockInfo.setValidity(true);

            Identity identity = null;
            if (block.getBlockBody().getTransaction().getTransactionId().substring(0, 1).equals("I")) {
                JSONObject body = null;
                try {
                    body = new JSONObject(block.getBlockBody().getTransaction().getData());
                    String publicKey = body.getString("publicKey");
                    String role = body.getString("role");
                    String name = body.getString("name");
                    String location = body.getString("location");

                    identity = new Identity(block.getBlockHeader().getHash(), publicKey, role, name, location);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //TODO: need to check that this is the right block to add based on the previous hash
            BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
            blockJDBCDAO.addBlockToBlockchain(blockInfo, identity);
        }
    }

    //no need of synchronizing
    public boolean isDuplicateBlock(Block block) {
        if (nonApprovedBlocks.contains(block)) {
            return true;
        }
        return false;
    }

    //no need of synchronizing
    public void sendAgreementForBlock(String blockHash) {
        String signedBlock = ChainUtil.getInstance().digitalSignature(blockHash);
        MessageSender.sendAgreement(signedBlock, blockHash);
        log.info("Agreement Sent for: {}", blockHash);
    }

    public void sendAgreementForBlockTest(Block block) {
        String blockHash = block.getBlockHeader().getHash();
        String signedBlock = ChainUtil.getInstance().digitalSignature(blockHash);
        MessageSender.sendAgreementTest(signedBlock, blockHash);
        log.info("Agreement Sent for: {}", block.getBlockHeader().getHash());
    }

    //no need of synchronizing
    public void handleAgreement(Agreement agreement) {
        System.out.println("agreement.getBlockHash()" + agreement.getBlockHash());
        getAgreementCollector(agreement.getBlockHash()).addAgreementForBlock(agreement);
    }

    //no need of synchronizing
    private AgreementCollector getAgreementCollector(String id) {
        for (AgreementCollector agreementCollector : this.agreementCollectors) {
            System.out.println(agreementCollector.getAgreementCollectorId());
            if (agreementCollector.getAgreementCollectorId().equals(id)) {
                return agreementCollector;
            }
        }
        return null;
    }

    //no need of synchronizing
    public void handleReceivedAgreement(String signature, String signedBlock, String blockHash, String publicKey) {
        handleAgreement(new Agreement(signature, signedBlock, blockHash, publicKey));
    }

    public void handleReceivedAdditionalData(String blockHash, String additionalData) {
        Block block = getBlockByBlockHash(blockHash);
        try {
            if (block != null) {
                String data = block.getBlockBody().getTransaction().getData();
                JSONObject jsonData = new JSONObject(data);
                String additionalDataField = jsonData.getString("additionalData");
                if (additionalDataField.equals(ChainUtil.getHash(additionalData))) {
                    jsonData.put("additionalData", additionalData);
                    block.getBlockBody().getTransaction().setData(jsonData.toString());
                    Controller controller = new Controller();
                    controller.notifyReceivedAdditionalData();
                    log.info("Additional Data added to the block");
                }
            } else {
                log.info("No block found for blockHash: {} ", blockHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Block getBlockByBlockHash(String blockHash) {
        for (Block block : nonApprovedBlocks) {
            if (blockHash.equals(block.getBlockHeader().getHash())) {
                return block;
            }
        }
        return null;
    }


    public JSONObject getAdditionalDataForBlock(String blockHash) {
        return new JSONObject();
    }

    public ArrayList<Block> getBlocksToBeAdded() {
        return approvedBlocks;
    }

    public ArrayList<Block> getNonApprovedBlocks() {
        return nonApprovedBlocks;
    }

    public void addBlockToNonApprovedBlocks(Block nonApprovedBlock) {
        this.nonApprovedBlocks.add(nonApprovedBlock);
//        setChanged();
//        notifyObservers();
    }

    public void removeBlockFromNonApprovedBlocks(Block nonApprovedBlock) {
        this.nonApprovedBlocks.remove(nonApprovedBlock);
//        setChanged();
//        notifyObservers();
    }

}
