package core.connection;

import HelperInterface.AsyncResponse;
import chainUtil.ChainUtil;
import core.blockchain.Block;
import core.blockchain.BlockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static core.connection.BlockJDBCDAO.base_url;

public class HistoryDAO implements AsyncResponse {
    private final Logger log = LoggerFactory.getLogger(HistoryDAO.class);

    JSONArray jsonArray;

    APICaller apiCaller = new APICaller();


    public boolean saveBlockWithAdditionalData(Block block, String data) throws SQLException {
        apiCaller.delegate = this;

        if (block != null) {
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setPreviousHash(block.getBlockHeader().getPreviousHash());
            blockInfo.setHash(block.getBlockHeader().getHash());
            blockInfo.setBlockTime(ChainUtil.convertStringToTimestamp(block.getBlockHeader().getBlockTime()));
            blockInfo.setBlockNumber(block.getBlockHeader().getBlockNumber());
            blockInfo.setTransactionId(block.getBlockBody().getTransaction().getTransactionId());
            blockInfo.setSender(block.getBlockBody().getTransaction().getSender());
            blockInfo.setEvent(block.getBlockBody().getTransaction().getEvent());
            blockInfo.setData(block.getBlockBody().getTransaction().getData().toString());
            blockInfo.setAddress(block.getBlockBody().getTransaction().getAddress());

            try {
                int validity = 0;
                if (blockInfo.isValidity()) {
                    validity = 1;
                }
                apiCaller.execute(base_url + "inserthistory" +
                        "?previous_hash=" + blockInfo.getPreviousHash() +
                        "&block_hash=" + blockInfo.getHash() +
                        "&block_timestamp=" + blockInfo.getBlockTime() + "&block_number=" + validity +
                        "&transaction_id=" + blockInfo.getTransactionId() +
                        "&sender=" + blockInfo.getSender() +
                        "&event=" + blockInfo.getEvent() +
                        "&data=" + blockInfo.getData() +
                        "&address=" + blockInfo.getAddress(), "GET", "BlockInfo", blockInfo);
                while (jsonArray == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;

    }


    public JSONObject getBlockData(String blockHash) throws SQLException, JSONException {
        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return jsonArray.getJSONObject(0);
    }


    public String getAdditionalData(String blockHash) throws SQLException, JSONException {

        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsonArray.getString(0);
    }

    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        this.jsonArray = output;
        return output;
    }
}

