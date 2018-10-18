package core.blockchain;

import chainUtil.ChainUtil;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Block {
    private BlockHeader blockHeader;
    private BlockBody blockBody;

    public Block(BlockHeader blockHeader, BlockBody blockBody){
        this.blockHeader = blockHeader;
        this.blockBody = blockBody;
    }

    public Block(BlockHeader genesisHeader){
        this.blockHeader =genesisHeader;
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public BlockBody getBlockBody() {
        return blockBody;
    }

    public void setBlockBody(BlockBody blockBody) {
        this.blockBody = blockBody;
    }



    public void broadcast(){}

    public String getBlockHash(){
        return getBlockHeader().getHash();
    }
}
