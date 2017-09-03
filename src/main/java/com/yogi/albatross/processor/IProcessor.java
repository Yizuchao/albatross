package com.yogi.albatross.processor;

import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import io.netty.buffer.ByteBuf;

public interface IProcessor {
    void process(SimpleEncapPacket packet) throws Exception;
}
