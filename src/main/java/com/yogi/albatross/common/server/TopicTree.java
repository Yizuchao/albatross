package com.yogi.albatross.common.server;

import com.google.common.collect.Lists;
import com.yogi.albatross.common.mqtt.MqttChannel;
import com.yogi.albatross.utils.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 主题详细匹配规则[4.7.1]：https://mcxiaoke.gitbooks.io/mqtt-cn/content/mqtt/04-OperationalBehavior.html
 */
public class TopicTree {
    private static final char PATH_SEPARATOR='/';
    private static final char SINGLE_LEVEL='+';
    private static final char MULTI_LEVEL='#';
    private static final char SYS_RETAIN='$';
    private static final char SPACE=' ';
    private static final TopicTrie trie = new TopicTrie();
    /**
     * @param subscribeTopic       此时topic可包含特殊字符
     * @param mqttChannel
     */
    public static void subscribe(String subscribeTopic, MqttChannel mqttChannel) {
        synchronized (trie){
            trie.add(subscribeTopic, mqttChannel);
        }
    }
    public static void subscribe(List<String> subscribeTopics, MqttChannel mqttChannel) {
        synchronized (trie){
            for (String subscribeTopic:subscribeTopics){
                trie.add(subscribeTopic, mqttChannel);
            }
        }
    }

    /**
     * @param publishTopic 此时topic不能包含除了"/"的其它特殊字符
     * @return
     */
    public static List<Consumer> searchSubscriber(String publishTopic) {
        if (Objects.isNull(publishTopic)) {
            return null;
        }
        List<Consumer> channels = trie.searchChannels(publishTopic);
        if(!CollectionUtils.isEmpty(channels)){
            return channels.stream().filter(mqttChannel -> mqttChannel.isSubscribe()).collect(Collectors.toList());
        }
        return channels;
    }

    public static void addTopics(List<String> topics){
        synchronized (trie){
            for (String topic:topics){
                trie.add(topic,null);
            }
        }
    }


    private static class TopicTrie {
        private final Node[] roots = new Node[67];

        public TopicTrie() {
            for (int i = 48; i < 58; i++) {
                roots[i - 48] = new Node((char) i);
            }
            for (int i = 65; i < 91; i++) {
                roots[i - 55] = new Node((char) i);
            }
            for (int i = 97; i < 122; i++) {
                roots[i - 61] = new Node((char) i);
            }
            roots[62] = new Node(SPACE);
            roots[63] = new Node(SYS_RETAIN);
            roots[64] = new Node(MULTI_LEVEL);
            roots[65] = new Node(SINGLE_LEVEL);
            roots[66] = new Node(PATH_SEPARATOR);
        }

        public boolean add(String topic, MqttChannel mqttChannel) {
            if (Objects.isNull(topic)) {
                return false;
            }
            char[] cs = topic.toCharArray();
            if (cs.length == 0) {
                return false;
            }
            if(!validAddTopic(cs)){//验证名称是否合法
                return false;
            }
            char firstChar = cs[0];
            int rootIndex = getRootIndex(firstChar);
            if (rootIndex == -1) {
                return false;
            }
            Node firstNode = roots[rootIndex];
            if (Objects.isNull(firstNode)) {
                return false;
            }
            if (cs.length == 1) {
                firstNode.addConsumer(mqttChannel);
            }
            Node preNode = firstNode;
            for (int i = 1; i < cs.length; i++) {
                preNode = preNode.addNextIfNotExists(cs[i]);
                if (i == cs.length - 1) {
                    preNode.addConsumer(mqttChannel);
                }
            }
            return true;
        }

        private boolean validAddTopic(char[] cs){
            for (int i = 0; i < cs.length; i++) {
                if(cs[i]==SYS_RETAIN){//'$'只能系统使用
                    return false;
                }
                if(i<cs.length-1 && cs[i]!=PATH_SEPARATOR && cs[i+1]==MULTI_LEVEL){
                    return false;
                }
            }
            return true;
        }

        /**
         * '#'保存了订阅所有主题的channel
         */
        private List<Consumer> getPound(){
            return roots[getRootIndex(MULTI_LEVEL)].getConsumers();
        }

        public List<Consumer> searchChannels(String topic){
            if(Objects.isNull(topic)){
                return null;
            }
            char[] cs=topic.toCharArray();
            int rootIndex = getRootIndex(cs[0]);
            if(rootIndex==-1){
                return null;
            }

            List<Consumer> channels=getPound();
            channels=Objects.isNull(channels)?Lists.newArrayList():channels;
            Node rootNode=roots[rootIndex];
            if(cs.length==1){
                CollectionUtils.addAll(channels,rootNode.getConsumers());
                CollectionUtils.addAll(channels,getSeparatorPound(rootNode));
                return channels;
            }
            Node plusRootNextLevelNode=roots[getRootIndex(SINGLE_LEVEL)].getNext(PATH_SEPARATOR);
            if(Objects.nonNull(plusRootNextLevelNode)){
                for (int i = 0; i < cs.length; i++) {
                    if(cs[i]==PATH_SEPARATOR){
                        CollectionUtils.addAll(channels,searchChannels(plusRootNextLevelNode,cs,i));
                        break;
                    }
                }
            }
            CollectionUtils.addAll(channels,searchChannels(rootNode.getNext(cs[1]),cs,1));
            return channels;
        }
        private List<Consumer> searchChannels(Node startNode,char[] topicArr,int start){
            if(Objects.isNull(startNode) || start>=topicArr.length){
                return null;
            }
            List<Consumer> consumers=Lists.newArrayList();
            Node preSimgleLevelMatchNode=null;
            for (int i = start; i < topicArr.length; i++) {
                if(topicArr[i]==PATH_SEPARATOR){
                    if(Objects.nonNull(preSimgleLevelMatchNode)){
                        if(i==topicArr.length-1){
                            CollectionUtils.addAll(consumers,preSimgleLevelMatchNode.getNextConsumer(PATH_SEPARATOR));
                        }else {
                            CollectionUtils.addAll(consumers,searchChannels(preSimgleLevelMatchNode.skipToNextLevelStart(topicArr[i+1]),topicArr,i+1));
                        }
                    }
                    if(Objects.nonNull(startNode)){
                        CollectionUtils.addAll(consumers,startNode.getNextConsumer(MULTI_LEVEL));
                        preSimgleLevelMatchNode=startNode.getNext(SINGLE_LEVEL);
                    }
                }
                if(i==topicArr.length-1){
                    if(Objects.nonNull(startNode)){
                        CollectionUtils.addAll(consumers,startNode.getConsumers());
                        CollectionUtils.addAll(consumers,getSeparatorPound(startNode));
                        if(topicArr[i]==PATH_SEPARATOR){
                            CollectionUtils.addAll(consumers,startNode.getNextConsumer(SINGLE_LEVEL));
                        }
                    }
                    if(Objects.nonNull(preSimgleLevelMatchNode)){
                        CollectionUtils.addAll(consumers,preSimgleLevelMatchNode.getConsumers());
                    }
                    return consumers;
                }
                if(Objects.nonNull(startNode)){
                    startNode=startNode.getNext(topicArr[i+1]);
                } else if(Objects.isNull(preSimgleLevelMatchNode)){
                    return consumers;
                }
            }
            return consumers;
        }
        private List<Consumer> getSeparatorPound(Node node){
            Node separatorNode=node.getNext(PATH_SEPARATOR);
            if(Objects.isNull(separatorNode)){
                return null;
            }
            Node poundNode = separatorNode.getNext(MULTI_LEVEL);
            if(Objects.isNull(poundNode)){
                return null;
            }
            return poundNode.getConsumers();
        }
        private int getRootIndex(char c) {
            if (c > 96 && c < 122) {
                return c - 61;
            }
            if (c > 64 && c < 91) {
                return c - 55;
            }
            if (c > 47 && c < 58) {
                return c - 48;
            }
            if (c == SPACE) {
                return 62;
            }
            if (c == SYS_RETAIN) {
                return 63;
            }
            if (c == MULTI_LEVEL) {
                return 64;
            }
            if (c == SINGLE_LEVEL) {
                return 65;
            }
            if(c == PATH_SEPARATOR){
                return 66;
            }
            return -1;
        }

        private class Node {
            private char c;
            private volatile ServerTopic serverTopic;
            private List<Node> nexts;

            public Node(char c) {
                this.c = c;
            }

            public void addConsumer(MqttChannel channel) {
                if(Objects.isNull(serverTopic)){
                    synchronized (serverTopic){
                        if(Objects.isNull(serverTopic)){
                            serverTopic = new ServerTopic();
                        }
                    }
                }
                serverTopic.addConsumer(channel);
            }
            public List<Consumer> getConsumers(){
                if(Objects.nonNull(serverTopic)){
                    System.out.println("this node char:"+this.c);
                    serverTopic.consumers();
                }
                return null;
            }
            public Node skipToNextLevelStart(char nextLevelStartChar){
                Node levelNode = this.getNext(PATH_SEPARATOR);
                if(Objects.nonNull(levelNode)){
                    return levelNode.getNext(nextLevelStartChar);
                }
                return null;
            }

            public List<Consumer> getNextConsumer(char c){
                if(Objects.isNull(nexts)){
                    return null;
                }
                Node next = getNext(c);
                if(Objects.nonNull(next)){
                    return next.getConsumers();
                }
                return null;
            }

            /**
             * 返回当前的节点
             * @param c
             * @return
             */
            public Node addNextIfNotExists(char c) {
                if (CollectionUtils.isEmpty(nexts)) {
                    Node node=new Node(c);
                    nexts = Lists.newArrayList();
                    nexts.add(node);
                    return node;
                }
                for (Node node : nexts) {
                    if (node.c == c) {
                        return node;
                    }
                }
                Node node=new Node(c);
                nexts.add(node);
                return node;
            }
            public Node getNext(char c){
                if(Objects.isNull(nexts)){
                    return null;
                }
                for (Node node:nexts){
                    if(node.c==c){
                        return node;
                    }
                }
                return null;
            }
        }
    }

    public static void main(String[] args) {
        TopicTree.subscribe("#",new MqttChannel(null));
        TopicTree.subscribe("+/bbbb/+",new MqttChannel(null));
        TopicTree.subscribe("bbbb/#",new MqttChannel(null));
        TopicTree.subscribe("ccccc/+",new MqttChannel(null));
        TopicTree.subscribe("ddddd",new MqttChannel(null));
        TopicTree.subscribe("aaaa/#",new MqttChannel(null));
        TopicTree.searchSubscriber("aaaa/bbbb/vvvvv");
    }
}
