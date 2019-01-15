package com.yogi.albatross.common.server;

import com.google.common.collect.Lists;
import com.yogi.albatross.common.base.MqttChannel;
import com.yogi.albatross.utils.CollectionUtils;
import io.netty.channel.ChannelId;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * 主题详细匹配规则[4.7.1]：https://mcxiaoke.gitbooks.io/mqtt-cn/content/mqtt/04-OperationalBehavior.html
 */
public class ServerTopics {
    private static final TopicTrie trie = new TopicTrie();
    private static final HashSet<String> existChannels=new HashSet<>();
    /**
     * @param subscribeTopic       此时topic可包含特殊字符
     * @param mqttChannel
     */
    public static void subscribe(String subscribeTopic, MqttChannel mqttChannel) {
        synchronized (trie){
            String existKey=mqttChannel.id().asLongText()+subscribeTopic;
            if(!existChannels.contains(existKey)){
                trie.add(subscribeTopic, mqttChannel);
                existChannels.add(existKey);
            }
        }
    }
    public static void subscribe(List<String> subscribeTopics, MqttChannel mqttChannel) {
        for (String s:subscribeTopics){
            System.out.println(s);
        }
        synchronized (trie){
            for (String subscribeTopic:subscribeTopics){
                String existKey=mqttChannel.id().asLongText()+subscribeTopic;
                if(!existChannels.contains(existKey)){
                    trie.add(subscribeTopic, mqttChannel);
                    existChannels.add(existKey);
                }
            }
        }
    }

    /**
     * @param publishTopic 此时topic不能包含除了"/"的其它特殊字符
     * @return
     */
    public static List<MqttChannel> searchSubscriber(String publishTopic) {
        if (Objects.isNull(publishTopic)) {
            return null;
        }
        return trie.searchChannels(publishTopic);
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
            roots[62] = new Node(' ');
            roots[63] = new Node('$');
            roots[64] = new Node('#');
            roots[65] = new Node('+');
            roots[66] = new Node('/');
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
                firstNode.addChannel(mqttChannel);
            }
            Node preNode = firstNode;
            for (int i = 1; i < cs.length; i++) {
                preNode = preNode.addNextIfNotExists(cs[i]);
                if (i == cs.length - 1) {
                    preNode.addChannel(mqttChannel);
                }
            }
            return true;
        }

        private boolean validAddTopic(char[] cs){
            for (int i = 0; i < cs.length; i++) {
                if(cs[i]=='$'){//'$'只能系统使用
                    return false;
                }
                if(i<cs.length-1 && cs[i]!='/' && cs[i+1]=='#'){
                    return false;
                }
            }
            return true;
        }

        /**
         * '#'保存了订阅所有主题的channel
         */
        private List<MqttChannel> getPound(){
            return roots[getRootIndex('#')].getChannels();
        }

        public List<MqttChannel> searchChannels(String topic){
            if(Objects.isNull(topic)){
                return null;
            }
            char[] cs=topic.toCharArray();
            int rootIndex = getRootIndex(cs[0]);
            if(rootIndex==-1){
                return null;
            }

            List<MqttChannel> channels=getPound();
            channels=Objects.isNull(channels)?Lists.newArrayList():channels;
            Node rootNode=roots[rootIndex];
            if(cs.length==1){
                CollectionUtils.addAll(channels,rootNode.getChannels());
                CollectionUtils.addAll(channels,getSeparatorPound(rootNode));
                return channels;
            }
            Node plusRootNextLevelNode=roots[getRootIndex('+')].getNext('/');
            if(Objects.nonNull(plusRootNextLevelNode)){
                for (int i = 0; i < cs.length; i++) {
                    if(cs[i]=='/'){
                        CollectionUtils.addAll(channels,searchChannels(plusRootNextLevelNode,cs,i));
                        break;
                    }
                }
            }
            CollectionUtils.addAll(channels,searchChannels(rootNode.getNext(cs[1]),cs,1));
            return channels;
        }
        private List<MqttChannel> searchChannels(Node startNode,char[] topicArr,int start){
            if(Objects.isNull(startNode) || start>=topicArr.length){
                return null;
            }
            List<MqttChannel> channels=Lists.newArrayList();
            Node preSimgleLevelMatchNode=null;
            for (int i = start; i < topicArr.length; i++) {
                if(topicArr[i]=='/'){
                    if(Objects.nonNull(preSimgleLevelMatchNode)){
                        if(i==topicArr.length-1){
                            CollectionUtils.addAll(channels,preSimgleLevelMatchNode.getNextChannel('/'));
                        }else {
                            CollectionUtils.addAll(channels,searchChannels(preSimgleLevelMatchNode.skipToNextLevelStart(topicArr[i+1]),topicArr,i+1));
                        }
                    }
                    if(Objects.nonNull(startNode)){
                        CollectionUtils.addAll(channels,startNode.getNextChannel('#'));
                        preSimgleLevelMatchNode=startNode.getNext('+');
                    }
                }
                if(i==topicArr.length-1){
                    if(Objects.nonNull(startNode)){
                        CollectionUtils.addAll(channels,startNode.getChannels());
                        CollectionUtils.addAll(channels,getSeparatorPound(startNode));
                        if(topicArr[i]=='/'){
                            CollectionUtils.addAll(channels,startNode.getNextChannel('+'));
                        }
                    }
                    if(Objects.nonNull(preSimgleLevelMatchNode)){
                        CollectionUtils.addAll(channels,preSimgleLevelMatchNode.getChannels());
                    }
                    return channels;
                }
                if(Objects.nonNull(startNode)){
                    startNode=startNode.getNext(topicArr[i+1]);
                } else if(Objects.isNull(preSimgleLevelMatchNode)){
                    return channels;
                }
            }
            return channels;
        }
        private List<MqttChannel> getSeparatorPound(Node node){
            Node separatorNode=node.getNext('/');
            if(Objects.isNull(separatorNode)){
                return null;
            }
            Node poundNode = separatorNode.getNext('#');
            if(Objects.isNull(poundNode)){
                return null;
            }
            return poundNode.getChannels();
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
            if (c == ' ') {
                return 62;
            }
            if (c == '$') {
                return 63;
            }
            if (c == '#') {
                return 64;
            }
            if (c == '+') {
                return 65;
            }
            if(c == '/'){
                return 66;
            }
            return -1;
        }

        private class Node {
            private char c;
            private List<MqttChannel> channels;
            private List<Node> nexts;

            public Node(char c) {
                this.c = c;
            }

            public void addChannel(MqttChannel channel) {
                if (CollectionUtils.isEmpty(channels)) {
                    channels = new ArrayList<>();
                }
                channels.add(channel);
            }
            public List<MqttChannel> getChannels(){
                if(Objects.nonNull(channels)){
                    System.out.println("this node char:"+this.c);
                }
                return channels;
            }
            public Node skipToNextLevelStart(char nextLevelStartChar){
                Node levelNode = this.getNext('/');
                if(Objects.nonNull(levelNode)){
                    return levelNode.getNext(nextLevelStartChar);
                }
                return null;
            }

            public List<MqttChannel> getNextChannel(char c){
                if(Objects.isNull(nexts)){
                    return null;
                }
                Node next = getNext(c);
                if(Objects.nonNull(next)){
                    return next.getChannels();
                }
                return null;
            }
            public char getChar(){
                return c;
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
        ServerTopics.subscribe("#",new MqttChannel(null));
        ServerTopics.subscribe("+/bbbb/+",new MqttChannel(null));
        ServerTopics.subscribe("bbbb/#",new MqttChannel(null));
        ServerTopics.subscribe("ccccc/+",new MqttChannel(null));
        ServerTopics.subscribe("ddddd",new MqttChannel(null));
        ServerTopics.subscribe("aaaa/#",new MqttChannel(null));
        ServerTopics.searchSubscriber("aaaa/bbbb/vvvvv");
    }
}
