package core.consensus;

public class PeerDetail {

    private String peerID;
    private String ip;
    private int listeningPort;
    private String type;

    public PeerDetail(String peerID, String type) {
        this.peerID = peerID;
        this.type = type;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public String getPeerID() {
        return peerID;
    }

    public String getIp() {
        return ip;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public String getType() {
        return type;
    }
}
