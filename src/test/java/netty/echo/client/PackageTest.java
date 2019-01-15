package netty.echo.client;

import com.google.common.collect.Lists;
import com.yogi.albatross.utils.CollectionUtils;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PackageTest {
    private BlockingConnection connection;
    @Before
    public void init() throws Exception {
        ConnectionClient client = new ConnectionClient("127.0.0.1", 10090);
        connection = client.connect();
    }

    @Test
    public void echo()  throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> topicNames = Lists.newArrayListWithExpectedSize(1);
        topicNames.add("hehe");
        topicNames.add("haha");
        List<Integer> qos = Lists.newArrayListWithExpectedSize(1);
        qos.add(2);
        qos.add(1);
        this.unsubscribe(topicNames);
        countDownLatch.await();
    }

    @Test
    public void subscribe() throws Exception {
        List<String> topicNames = Lists.newArrayListWithExpectedSize(1);
        topicNames.add("hehe");
        topicNames.add("haha");
        int topicNameSize = topicNames.size();
        List<Topic> topics = Lists.newArrayListWithExpectedSize(topicNameSize);
        for (int i = 0; i < topicNameSize; i++) {
            Topic topic = new Topic(topicNames.get(i), intToQos(1));
            topics.add(topic);
        }
        Topic[] topicsArr = new Topic[topicNameSize];
        byte[] subscribe = connection.subscribe(topics.toArray(topicsArr));
        System.out.println(new String(subscribe));
        Message message = connection.receive();
        System.out.println(new String(message.getPayload()));
    }

    public void unsubscribe(List<String> topicNames) throws Exception {
        if (CollectionUtils.isEmpty(topicNames)) {
            return;
        }
        String[] topicArr = new String[topicNames.size()];
        connection.unsubscribe(topicNames.toArray(topicArr));
    }
    @Test
    public void publish() throws Exception{
        connection.publish("haha","hello".getBytes(),QoS.AT_LEAST_ONCE,false);
    }


    private QoS intToQos(int qos) {
        if (qos == 0) {
            return QoS.AT_MOST_ONCE;
        }
        if (qos == 1) {
            return QoS.AT_LEAST_ONCE;
        }
        return QoS.EXACTLY_ONCE;
    }

    public static void main(String[] args) {
        System.out.println(Integer.toBinaryString((byte)144) );
    }
}
