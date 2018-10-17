package network.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import network.Client.Handlers.CommonClientHandler;
import network.Neighbour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Thread{
    private final Logger log = LoggerFactory.getLogger(Client.class);
    RequestMessage requestMessage;
    private String neighbourIP;
    private int port;

    public void init(Neighbour neighbour1, RequestMessage requestMessage) {
        this.neighbourIP = neighbour1.getIp();
        this.port = neighbour1.getPort();
        this.requestMessage = requestMessage;
    }


    public void initTest(String IP, int port, RequestMessage requestMessage) {
        this.neighbourIP = IP;
        this.port = port;
        this.requestMessage = requestMessage;
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers
                                    .weakCachingResolver(RequestMessage.class.getClassLoader())),
                            new CommonClientHandler(requestMessage));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(neighbourIP, port).sync();
            log.info("Connecting to neighbour: {}:{}", neighbourIP, port);


            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
