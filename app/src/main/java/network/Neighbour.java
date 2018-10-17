package network;

public class Neighbour {
    private String ip;
    private int port;
    private String publicKey;
    private String peerID;

    public Neighbour(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Neighbour(String peerID, String ip, int port) {
        this.peerID = peerID;
        this.ip = ip;
        this.port = port;
    }

    public Neighbour(String peerID, String ip, int port, String publicKey) {
        this.peerID = peerID;
        this.ip = ip;
        this.port = port;
        this.publicKey = publicKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPeerID() {
        return peerID;
    }


}
