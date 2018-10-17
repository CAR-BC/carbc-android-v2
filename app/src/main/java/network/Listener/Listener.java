package network.Listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import network.Client.RequestMessage;
import network.Listener.Handlers.CommonListenerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends Thread {
    private final Logger log = LoggerFactory.getLogger(Listener.class);
    private static int port;

    public static void init(int listenerPort){
        port = listenerPort;
        if (listenerPort > 0) {
            port = listenerPort;
        } else {
            port = 8080;
        }
    }

    @Override
    public void run() {
        // accepts an incoming connection
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        // handles the traffic of the accepted connection
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                new ObjectDecoder(ClassResolvers
                                        .weakCachingResolver(RequestMessage.class.getClassLoader())),
                                new ObjectEncoder(),
                                new CommonListenerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
            log.info("Listening on port: {}", port);

            // Wait until the server socket is closed and gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
