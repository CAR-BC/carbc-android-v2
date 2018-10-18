package core.consensus;

import core.blockchain.Block;

public class Rating {

    private double value;
    private Block block;
    private String[] mandatoryValidators;

    public Rating(Block block) {
        this.block = block;
        value = 0;
    }

    public double reAdjustRating(Agreement agreement) {
        return value;
    }

}
