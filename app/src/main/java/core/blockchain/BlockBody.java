package core.blockchain;

import java.io.Serializable;

public class BlockBody implements Serializable {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
