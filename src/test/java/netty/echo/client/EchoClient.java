package netty.echo.client;

import com.google.common.collect.Lists;
import com.yogi.albatross.utils.CollectionUtils;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EchoClient {
    private final String host;
    private final int port;
    private final CountDownLatch latch;
    private UUID clientId=UUID.randomUUID();

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
       /* if (args.length != 2) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() +
                            " <host> <port>");
            return;
        }*/

        /*final String host = args[0];
        final int port = NumberUtils.toInt(args[1],6080);*/

        /*BlockingConnection connection=new EchoClient("127.0.0.1", 10090).connect();*///connect

        List<String> topicNames=Lists.newArrayListWithExpectedSize(1);
        topicNames.add("hehe");
        List<Integer> qos=Lists.newArrayListWithExpectedSize(1);
        qos.add(2);
        new EchoClient("127.0.0.1", 10090).subscribe(topicNames,qos);
    }

    public BlockingConnection connect() throws Exception{
        MQTT mqtt = new MQTT();
        mqtt.setClientId(clientId.toString());
        mqtt.setKeepAlive((short)10000);
        mqtt.setCleanSession(true);
        mqtt.setUserName("yogi");
        mqtt.setPassword("123456");
        mqtt.setHost("127.0.0.1",10090);
        BlockingConnection connection=mqtt.blockingConnection();
        connection.connect();
        if(!connection.isConnected()){
            System.out.println("connect fail");
        }
        return connection;
    }
    public void subscribe(List<String> topicNames,List<Integer> qos) throws Exception{
        if(CollectionUtils.isEmpty(topicNames)){
            return;
        }
        int topicNameSize=topicNames.size();
        int qosSize=qos.size();
        if(topicNameSize!=qosSize){
            return;
        }
        List<Topic> topics= Lists.newArrayListWithExpectedSize(topicNameSize);
        for (int i = 0; i < topicNameSize; i++) {
            Topic topic=new Topic(topicNames.get(i),intToQos(qos.get(i)));
            topics.add(topic);
        }
        BlockingConnection connect = connect();
        Topic[] topicsArr=new Topic[topicNameSize];
        connect.subscribe(topics.toArray(topicsArr));
    }
    private QoS intToQos(int qos){
        if(qos==0){
            return QoS.AT_MOST_ONCE;
        }
        if(qos==1){
            return QoS.EXACTLY_ONCE;
        }
        return QoS.AT_LEAST_ONCE;
    }
}
