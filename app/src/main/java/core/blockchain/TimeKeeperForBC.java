package core.blockchain;

import core.consensus.BlockchainRequester;

public class TimeKeeperForBC extends Thread{

    @Override
    public void run() {
        try{
            System.out.println("Inside TimeKeeperForBC");
            Thread.sleep(5000);
            System.out.println("time expired for requesting blockchain");
            BlockchainRequester.getInstance().requestBlockchain();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
