package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.command.BaseCommand;

@Processor(targetType = FixedHeadType.PINGREQ)
public class PingDecoder extends DecoderAdapter{
    @Override
    protected BaseCommand process0(MqttCommand packet) throws Exception {
        return null;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, BaseCommand request) throws Exception {
        byte[] bytes=new byte[2];
        bytes[0]=(byte) 0xd0;
        bytes[1]=(byte)0x00;
        return bytes;
    }
}
