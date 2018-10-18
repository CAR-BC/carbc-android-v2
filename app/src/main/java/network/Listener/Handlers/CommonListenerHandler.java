package network.Listener.Handlers;

import network.communicationHandler.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network.Client.RequestMessage;
import network.Protocol.AckMessageCreator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class CommonListenerHandler extends ChannelInboundHandlerAdapter {
    private final Logger log = LoggerFactory.getLogger(CommonListenerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            if (msg instanceof RequestMessage) {
                RequestMessage requestMessage = (RequestMessage) msg;
                Map<String, String> headers = requestMessage.readHeaders(); //TODO: Inspect headers
                String data = requestMessage.readData();

                //adding client ip to Json Object
                String clientIP = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
                JSONObject receivedObject = new JSONObject(data);
                receivedObject.put("ip", clientIP);
                RequestHandler.getInstance().handleRequest(headers, receivedObject.toString());

                log.info("=====================================");
                log.info("        at the server side           ");
                log.info("=====================================");
                log.info("----------headers----------------");
                log.info(headers.toString());
                log.info("----------data----------------");
                log.info(data);

                String messageType = (String) headers.get("messageType");
                String sender = (String) headers.get("sender");
                RequestMessage ackMessage = AckMessageCreator.createAckMessage(messageType, sender);
                ackMessage.addHeader("keepActive", "false");
                ChannelFuture f = ctx.writeAndFlush(ackMessage);

                log.info("Message received from: {}", clientIP);

                //if the msg we received had the header "keepActive" set to false
                //then close the channel
                if ("false".equals(headers.get("keepActive"))) {
                    //finish the process
                    f.addListener(ChannelFutureListener.CLOSE);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
