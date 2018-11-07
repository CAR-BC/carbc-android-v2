package com.example.madhushika.carbc_android_v3;

import org.junit.Test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import core.blockchain.BlockInfo;
import core.connection.BlockJDBCDAO;
import core.connection.Identity;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws SQLException {

        System.out.println("hello world");
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setAddress("address");
        blockInfo.setBlockNumber(256);
       // blockInfo.setBlockTime();
        blockInfo.setData("data");
        blockInfo.setHash("hash");
        blockInfo.setSender("sender");
        blockInfo.setEvent("event");
        blockInfo.setValidity(true);
        blockInfo.setPreviousHash("previous hash");
        blockInfo.setTransactionId("id");

        Identity identity = new Identity("hash","pub key","role","name");
        blockJDBCDAO.addBlockToBlockchain(blockInfo,identity);
    }
}