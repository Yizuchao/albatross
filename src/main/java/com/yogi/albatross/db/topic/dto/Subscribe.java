package com.yogi.albatross.db.topic.dto;

import com.yogi.albatross.constants.common.SubscribeQos;

public class Subscribe {
    private Integer id;
    private String topicName;
    private Long subscriber;
    private SubscribeQos qos;

    public Long getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Long subscriber) {
        this.subscriber = subscriber;
    }

    public SubscribeQos getQos() {
        return qos;
    }

    public void setQos(SubscribeQos qos) {
        this.qos = qos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
