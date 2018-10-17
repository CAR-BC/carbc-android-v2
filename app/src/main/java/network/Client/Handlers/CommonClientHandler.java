package network.Client.Handlers;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network.Client.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 *
 */
public class CommonClientHandler extends ChannelInboundHandlerAdapter {
    private final Logger log = LoggerFactory.getLogger(CommonClientHandler.class);
    private RequestMessage requestMessage;

    public CommonClientHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ChannelFuture f = ctx.writeAndFlush(this.requestMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof RequestMessage) {
            RequestMessage requestMessage = (RequestMessage) msg;
            Map<String, String> headers = requestMessage.readHeaders();
            String data = requestMessage.readData();

            log.info("=====================================");
            log.info("        at the client side           ");
            log.info("=====================================");
            log.info("----------headers----------------");
            log.info(headers.toString());
//            log.info("----------data----------------");
//            log.info(data);

            //if the msg we received had the header "keepActive" set to false
            //then close the channel
            if ("false".equals(headers.get("keepActive"))) {
                //finish the process
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
