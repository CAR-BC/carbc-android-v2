package core.consensus;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.blockchain.Block;
import core.connection.HistoryDAO;
import core.connection.IdentityJDBC;
import network.Neighbour;
import network.Node;
import network.communicationHandler.MessageSender;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataCollector {

    private static DataCollector dataCollector;
    private ArrayList<DataRequester> requestedTransactionDataDetails;
    private ArrayList<DataRequester> requestedAdditionalDataDetails;

    private final Logger log = LoggerFactory.getLogger(DataCollector.class);


    private DataCollector() {
        requestedTransactionDataDetails = new ArrayList<>();
        requestedAdditionalDataDetails = new ArrayList<>();
    }

    public static DataCollector getInstance() {
        if (dataCollector == null) {
            dataCollector = new DataCollector();
        }
        return dataCollector;
    }

    public void requestTransactionData(String vehicleID, String date, String peerID) {
        Neighbour dataOwner = Node.getInstance().getPeer(peerID);
        if (dataOwner != null) {
            TransactionDataRequester dataRequester = new TransactionDataRequester(peerID, vehicleID, date);
            dataRequester.setDataOwner(dataOwner);
            requestedTransactionDataDetails.add(dataRequester);
            requestTransactionDataFromDataOwner(vehicleID, date, dataOwner);
        } else {
            log.info("No Peer Details found for: {}", peerID);
            PeerDetailsCollector.getInstance().addPeerDetail(new PeerDetail(peerID, "TransactionData"));
            TransactionDataRequester dataRequester = new TransactionDataRequester(peerID, vehicleID, date);
            requestedTransactionDataDetails.add(dataRequester);
            MessageSender.requestPeerDetails(peerID);
        }
    }

    public void requestTransactionDataFromDataOwner(String vehicleID, String date, Neighbour dataOwner) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vehicleID", vehicleID);
            jsonObject.put("dataOwner", dataOwner.getPeerID());
            jsonObject.put("date", date);
            String nodeID = Node.getInstance().getNodeConfig().getNodeID();
            jsonObject.put("signature", ChainUtil.getInstance().digitalSignature(nodeID));
            jsonObject.put("signedData", nodeID);
            MessageSender.requestTransactionData(jsonObject, dataOwner);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//remove hardcoded value
    public void handleRequestedPeerDetails(JSONObject peerDetails, String signature, String signedData) {
        IdentityJDBC identityJDBC = new IdentityJDBC();
//        String bootstrapNodePK = identityJDBC.getPeerPublicKey("0");
        String bootstrapNodePK = "3081f13081a806072a8648ce38040130819c024100fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17021500962eddcc369cba8ebb260ee6b6a126d9346e38c50240678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca4034400024100f21e9496eb54bc571037e6744b51da50b37c1515ec643aec2d04f3402c0187d8bccf48107bce77c687fb92a4f058878a87911ef6f51f81417d14f72e2517853e"; //change later
        try{
            if (ChainUtil.getInstance().signatureVerification(bootstrapNodePK, signature, signedData)) {
                String ip = peerDetails.getString("ip");
                String peerID = peerDetails.getString("nodeID");
                int listeningPort = peerDetails.getInt("port");
                Neighbour dataOwner = new Neighbour(peerID, ip, listeningPort);
                String requestedType = PeerDetailsCollector.getInstance().getRequstedType(peerID);
                DataRequester dataRequester = getDataRequester(requestedType, peerID);
                dataRequester.setDataOwner(dataOwner);

                if (requestedType.equals("TransactionData")) {
                    TransactionDataRequester transactionDataRequester = (TransactionDataRequester) dataRequester;
                    requestTransactionDataFromDataOwner(transactionDataRequester.getVehicleID(), transactionDataRequester.getDate(), dataOwner);
                } else if (requestedType.equals("AdditionalData")) {
                    AddtionalDataRequester addtionalDataRequester = (AddtionalDataRequester) dataRequester;
                    requestAdditionalDataFromDataOwner(addtionalDataRequester.getBlockHash(), dataOwner);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    public DataRequester getDataRequester(String type, String peerID) {
        if (type.equals("TransactionData")) {
            for (DataRequester dataRequester : requestedTransactionDataDetails) {
                if (peerID.equals(dataRequester.getPeerID())) {
                    log.info("transaction data requester found for : {}", peerID);
                    return dataRequester;
                }
            }
        } else if (type.equals("AdditionalData")) {
            for (DataRequester dataRequester : requestedAdditionalDataDetails) {
                if (peerID.equals(dataRequester.getPeerID())) {
                    log.info("additional data requester found for : {}", peerID);
                    return dataRequester;
                }
            }
        }
        log.info("No data requester found for : {}", peerID);
        return null;
    }

    public void handleReceivedTransactionData(JSONObject transactionData, String signature, String signedData, String peerID) {
        IdentityJDBC identityJDBC = new IdentityJDBC();
        if (ChainUtil.signatureVerification(identityJDBC.getPeerPublicKey(peerID), signature, signedData)) {
            getDataRequester("TransactionData", peerID).setReceivedData(transactionData);
            log.info("Transaction Data Received");
        }
    }

    public void requestAdditionalData(Block block) {
        String blockHash = block.getBlockHeader().getHash();
        String signedBlock = ChainUtil.digitalSignature(blockHash);
        String peerID = block.getBlockBody().getTransaction().getAddress();
        AddtionalDataRequester addtionalDataRequester = new AddtionalDataRequester(peerID, blockHash);
        requestedAdditionalDataDetails.add(addtionalDataRequester);
        Neighbour dataOwner = Node.getInstance().getPeer(peerID);

        if (dataOwner != null) {
            requestAdditionalDataFromDataOwner(blockHash, dataOwner);
        } else {
            log.info("No Peer Details found for: {}", peerID);
            PeerDetailsCollector.getInstance().addPeerDetail(new PeerDetail(peerID, "AdditionalData"));
            MessageSender.requestPeerDetails(peerID);
        }
    }

    public void requestAdditionalDataFromDataOwner(String blockHash, Neighbour dataOwner) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("blockHash", blockHash);
            jsonObject.put("dataOwner", dataOwner.getPeerID());
            jsonObject.put("signature", ChainUtil.digitalSignature(blockHash));
            jsonObject.put("publicKey", KeyGenerator.getInstance().getPublicKeyAsString());
            MessageSender.requestAdditionalData(jsonObject, dataOwner);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void handleReceivedAdditionalData(String blockHash, String signedBlock, Neighbour dataRequester) {
        if (ChainUtil.signatureVerification(dataRequester.getPublicKey(), signedBlock, blockHash)) {
            Controller controller = new Controller();
            controller.handleAdditionalDataRequest(blockHash, dataRequester);
        }
    }

    public void sendAdditionalData(String blockHash, Neighbour dataRequester) {
        HistoryDAO historyDAO = new HistoryDAO();
        try {
            String additionalData = historyDAO.getAdditionalData(blockHash);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("blockHash", blockHash);
            jsonObject.put("additionalData", additionalData);
            MessageSender.sendAdditionalData(jsonObject, dataRequester);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
