package netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EchoClient {
    private final String host;
    private final int port;
    private final CountDownLatch latch;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
        latch=new CountDownLatch(1);
    }
    public void start() throws Exception{
        NioEventLoopGroup group=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host,port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new IdleStateHandler(0,0,15, TimeUnit.SECONDS));
                    pipeline.addLast(new HeartBeatClientHandler());
                    pipeline.addLast(new EchoClientHandler());
                }
            });
            ChannelFuture f = bootstrap.connect().sync();
            latch.await();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() +
                            " <host> <port>");
            return;
        }

        final String host = args[0];
        final int port = NumberUtils.toInt(args[1],6080);

        new EchoClient(host, port).start();
    }

}
