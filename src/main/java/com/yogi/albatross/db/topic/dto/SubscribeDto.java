package com.yogi.albatross.db.topic.dto;

import com.yogi.albatross.constants.common.SubscribeQos;

public class SubscribeDto {
    private String name;
    private Long creator;
    private SubscribeQos qos;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public SubscribeQos getQos() {
        return qos;
    }

    public void setQos(SubscribeQos qos) {
        this.qos = qos;
    }
}
