package Test;

import org.json.JSONArray;
import org.json.JSONObject;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.blockchain.Block;
import core.blockchain.BlockBody;
import core.blockchain.BlockHeader;
import core.blockchain.Transaction;
import network.Node;
import network.communicationHandler.MessageSender;

public class BlockSendingTest {

    public static void main(String[] args) {
        Controller controller = new Controller();
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectNewOwner = new JSONObject();
            JSONObject jsonSecondary = new JSONObject();

            jsonObjectNewOwner.put("name", "Ashan");
            jsonObjectNewOwner.put("publicKey", KeyGenerator.getInstance().getPublicKeyAsString());

            jsonSecondary.put("NewOwner", jsonObjectNewOwner);
            jsonObject.put("SecondaryParty", jsonSecondary);
            jsonObject.put("ThirdParty", new JSONArray());

            System.out.println(jsonObject);
//
            String sender = KeyGenerator.getInstance().getPublicKeyAsString();
            String nodeID = Node.getInstance().getNodeConfig().getNodeID();
            Transaction transaction = new Transaction("V",sender,"ExchangeOwnership", jsonObject.toString(), nodeID);

            BlockBody blockBody = new BlockBody();
            blockBody.setTransaction(transaction);
            String blockHash = ChainUtil.getInstance().getBlockHash(blockBody);
            BlockHeader blockHeader = new BlockHeader(blockHash);

            Block block = new Block(blockHeader, blockBody);
            MessageSender.getInstance().broadCastBlockTest(block);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
