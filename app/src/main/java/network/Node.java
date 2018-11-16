package network;

import Exceptions.FileUtilityException;
import chainUtil.KeyGenerator;
import config.CommonConfigHolder;
import config.NodeConfig;
import constants.Constants;
import core.blockchain.Blockchain;
import core.connection.NeighbourDAO;
import network.Client.Client;
import network.Client.RequestMessage;
import network.Listener.Listener;
import network.Protocol.HelloMessageCreator;
import network.communicationHandler.MessageSender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.impl.SimpleLogger;
import utils.FileUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public final class Node {
    private final Logger log = LoggerFactory.getLogger(Node.class);
    private static final Node instance = new Node();
    Listener listener;
    Client client;
    private NodeConfig nodeConfig;
    private List<Neighbour> tempNeighbour;

    private Node() {
        tempNeighbour = new ArrayList<>();
    }

    public static Node getInstance() {
        return instance;
    }

    public void init() throws FileUtilityException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {

        /* Set config and its parameters */
//        Random random = new Random();
//        long peerID = random.nextLong();
        String peerID = KeyGenerator.getInstance().getPublicKeyAsString().substring(10);


        //Create config
        this.nodeConfig = new NodeConfig(peerID);

        //Set port to listen on
        JSONObject commonConfig = CommonConfigHolder.getInstance().getConfigJson();
//        nodeConfig.setListenerPort(commonConfig.getInt("listener_port"));

        //Add neighbours list
//        JSONArray neighbours = commonConfig.getJSONArray("neighbours");

        //getting ips if they available and rejoin netowrk
        if (KeyGenerator.getInstance().getResourcesFilePath("peersDetails.json") != null) {
            String resourcePath = System.getProperty(Constants.CARBC_HOME)
                    + "/src/main/resources/" + "peersDetails.json";
//            JSONObject peersListObject = new JSONObject(FileUtils.readFileContentAsText(resourcePath));

//            String path = KeyGenerator.getInstance().getResourcesFilePath("peersDetails.json");
//            System.out.println(path);
//            JSONObject peersListObject = new JSONObject(FileUtils.readFileContentAsText(resourcePath));
//            System.out.println(peersListObject.toString());
//            JSONArray peersList = peersListObject.getJSONArray("peers");
//            for (int i = 0; i < peersList.length(); i++) {
//                System.out.println(peersList.getJSONObject(i).toString());
//                JSONObject neighbourJson = peersList.getJSONObject(i);
//                String neightbourIP = neighbourJson.getString("ip");
//                int neightbourPort = neighbourJson.getInt("ListeningPort");
//                Neighbour neighbour = new Neighbour(neightbourIP, neightbourPort);
//                nodeConfig.addNeighbour(neighbour);
//            }
        }
        log.info("Initializing Node:{}", peerID);
    }

    //revert later

    public void initTest() {

        /* Set config and its parameters */
//        Random random = new Random();
//        long peerID = random.nextLong();
        String publicKey = KeyGenerator.getInstance().getPublicKeyAsString();
        String peerID = publicKey.substring(publicKey.length()-40);

        //Create config
        this.nodeConfig = new NodeConfig(peerID);

        //Set port to listen on
        JSONObject commonConfig = CommonConfigHolder.getInstance().getConfigJson();
        try {
            nodeConfig.setListenerPort(commonConfig.getInt("listener_port"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Add neighbours list

//        JSONArray neighbours = commonConfig.getJSONArray("neighbours");
//        for (int i = 0; i < neighbours.length(); i++) {
//            JSONObject neighbourJson = neighbours.getJSONObject(i);
//            String neightbourIP = neighbourJson.getString("ip");
//            int neightbourPort = neighbourJson.getInt("port");
//            Neighbour neighbour = new Neighbour(neightbourIP, neightbourPort);
//            nodeConfig.addNeighbour(neighbour);
//        }
        log.info("Initializing Node:{}", peerID);

    }

    public void initTest2(String peerID, int port) {
        this.nodeConfig = new NodeConfig(peerID);
        nodeConfig.setListenerPort(port);
        log.info("Initializing Node:{}", peerID);
    }

    public void startListening() {
        this.listener = new Listener();
        this.listener.init(nodeConfig.getListenerPort());
        this.listener.start();
        log.info("Initialized listener");
    }

    public void sendMessageToNeighbour(int neighnourIndex, RequestMessage requestMessage) {
        Client client = new Client();
        Neighbour neighbour1 = nodeConfig.getNeighbours().get(neighnourIndex);
        client.init(neighbour1, requestMessage);
        client.start();
        log.info("Initialized client");
    }

    //send message to a specific peer
    public void sendMessageToPeer(String IP, int port, RequestMessage requestMessage) {
        Client client = new Client();
        client.initTest(IP,port,requestMessage);
        client.start();
        log.info("Initialized client");
    }

    //broadcast message to the network
    public void broadcast(RequestMessage requestMessage) {
        System.out.println("neighbours list size: " + nodeConfig.getNeighbours().size());
        for(Neighbour neighbour: nodeConfig.getNeighbours()) {
            System.out.println("broadcasted to: " + neighbour.getIp());
            Client client = new Client();
            client.initTest(neighbour.getIp(),neighbour.getPort(),requestMessage);
            client.start();
        }
    }

//    public void addPeers(String data) {
//        JSONObject peersList = new JSONObject(data);
//        JSONArray peers = peersList.getJSONArray("peers");
//        for (int i = 0; i < peers.length(); i++) {
//            JSONObject peersJson = peers.getJSONObject(i);
//            String peerIP = peersJson.getString("ip");
//            int peerPort = peersJson.getInt("ListeningPort");
////            String peerPublicKey = peersJson.getString("publicKey");
//            Neighbour neighbour = new Neighbour(peerIP,peerPort);
//            nodeConfig.addNeighbour(neighbour);
//        }
//    }

    public void addPeerToTempList(String data) {
        JSONArray peers = null;
        try {
            JSONObject peersList = new JSONObject(data);
            peers = peersList.getJSONArray("peers");
            for (int i = 0; i < peers.length(); i++) {
                JSONObject peersJson = peers.getJSONObject(i);
                String peerIP = peersJson.getString("ip");
                String peerID = peersJson.getString("peerID");
                int peerPort = peersJson.getInt("ListeningPort");
//            String peerPublicKey = peersJson.getString("publicKey");
                Neighbour neighbour = new Neighbour(peerID, peerIP,peerPort);
                tempNeighbour.add(neighbour);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void talkToPeers(List<Neighbour> neighbours) {
        for(Neighbour neighbour: neighbours) {
            JSONObject portInfo = new JSONObject();
            try {
                portInfo.put("ListeningPort",nodeConfig.getListenerPort());
                portInfo.put("nodeID", nodeConfig.getNodeID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestMessage helloMsg = HelloMessageCreator.createHelloMessage(portInfo);
            sendMessageToPeer(neighbour.getIp(),neighbour.getPort(),helloMsg);
        }
    }

    public List<Neighbour> getTempNeighbour() {
        return tempNeighbour;
    }

    public void joinNetwork(String data) {
        addPeerToTempList(data);
        talkToPeers(tempNeighbour);
    }

    public void addActiveNeighbour(String peerID, String ip, int port) {
        Neighbour node = getPeer(peerID);
//        NeighbourDAO neighbourDAO = new NeighbourDAO();

        if(node == null) {
            Neighbour neighbour = new Neighbour(peerID, ip, port);
            nodeConfig.addNeighbour(neighbour);
//            neighbourDAO.saveNeighbours(neighbour);
            log.info("Active Peer Added: {}" , peerID);
        } else {
            String nodeIp = node.getIp();
            int NodePort = node.getPort();

            if(!ip.equals(nodeIp) || port != NodePort) {
                Node.getInstance().getNodeConfig().updateNeighbourDetails(peerID, ip, port);
//                neighbourDAO.updatePeer(peerID, ip, port);
                log.info("peer data updated successfully: ", peerID);
            }else {
                log.info("peer data already have");
            }
        }

        for(Neighbour peer: nodeConfig.getNeighbours()) {
            System.out.println("IP: "+ peer.getIp() + " port: " + peer.getPort());
        }
    }

    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public Neighbour getPeer(String peerID) {
        for(Neighbour neighbour: nodeConfig.getNeighbours()) {
            if(peerID.equals(neighbour.getPeerID())) {
                return neighbour;
            }
        }
        return null;
    }

    public String getNodeId() {
        return nodeConfig.getNodeID();
    }

    public void startNode() {
//        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

        /*
         * Set the main directory as home
         * */
        System.setProperty(Constants.CARBC_HOME, System.getProperty("user.dir"));

        /*
         * At the very beginning
         * A Config common to all: network, blockchain, etc.
         * */
        CommonConfigHolder commonConfigHolder = CommonConfigHolder.getInstance();
        commonConfigHolder.setConfigUsingResource("peer1");

        /*
         * when initializing the network
         * */
        Node node = Node.getInstance();
        node.initTest();

        /*
         * when we want our node to start listening
         * */
        node.startListening();

        /**
         * collecting peer details
         */
        MessageSender.requestIP();

        /**
         * Getting blockchain
         */
        Blockchain.runBlockChain();
    }

    public void startNode(String peerID, int port) {
//        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

        /*
         * Set the main directory as home
         * */
        System.setProperty(Constants.CARBC_HOME, System.getProperty("user.dir"));

        /*
         * At the very beginning
         * A Config common to all: network, blockchain, etc.
         * */
//        CommonConfigHolder commonConfigHolder = CommonConfigHolder.getInstance();
//        commonConfigHolder.setConfigUsingResource(peer);

        /*
         * when initializing the network
         * */
        Node node = Node.getInstance();
        node.initTest2(peerID, port);

        /*
         * when we want our node to start listening
         * */
        node.startListening();
    }

    public void deletePeers() {
        nodeConfig.deleteNeigbours();
    }


}
