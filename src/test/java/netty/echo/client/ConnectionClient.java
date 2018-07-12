package netty.echo.client;

import com.google.common.collect.Lists;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MD5Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.math.NumberUtils;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionClient{
    private final String host;
    private final int port;
    private final CountDownLatch latch;
    private UUID clientId = UUID.randomUUID();

    public ConnectionClient(String host, int port) {
        this.host = host;
        this.port = port;
        latch = new CountDownLatch(1);
    }
    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new IdleStateHandler(0, 0, 15, TimeUnit.SECONDS));
                    pipeline.addLast(new HeartBeatClientHandler());
                    pipeline.addLast(new EchoClientHandler());
                }
            });
            ChannelFuture f = bootstrap.connect().sync();
            latch.await();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public BlockingConnection connect() throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setClientId(clientId.toString());
        mqtt.setKeepAlive((short) 10);
        mqtt.setCleanSession(true);
        mqtt.setUserName("admin");
        mqtt.setPassword(MD5Utils.encode("123456"));
        mqtt.setHost("127.0.0.1", 10090);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        if (!connection.isConnected()) {
            System.out.println("connect fail");
        }
        return connection;
    }
}
