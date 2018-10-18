package core.consensus;

import java.util.ArrayList;

public class PeerDetailsCollector {

    private static PeerDetailsCollector peerDetailsCollector;
    ArrayList<PeerDetail> peerDetails;

    private PeerDetailsCollector() {
        peerDetails = new ArrayList<>();
    }

    public static PeerDetailsCollector getInstance() {
        if (peerDetailsCollector == null) {
            peerDetailsCollector = new PeerDetailsCollector();
        }
        return peerDetailsCollector;
    }

    public void addPeerDetail(PeerDetail peerDetail) {
        peerDetails.add(peerDetail);
    }

    public String getRequstedType(String peerID) {
        for(PeerDetail peerDetail: peerDetails) {
            if(peerID.equals(peerDetail.getPeerID())) {
                return peerDetail.getType();
            }
        }
        return null;
    }

}
