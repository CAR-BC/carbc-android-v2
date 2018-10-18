package core.smartContract;

import core.consensus.Consensus;
import java.sql.SQLException;
import java.text.ParseException;

public class TimeKeeper extends Thread{
    String blockHash;

    public TimeKeeper(String blockHash){
        this.blockHash = blockHash;
    }

    @Override
    public void run() {
        try {
            System.out.println("Inside TimeKeeper");
            Thread.sleep(60000);
            Consensus.getInstance().checkAgreementsForBlock(blockHash);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
