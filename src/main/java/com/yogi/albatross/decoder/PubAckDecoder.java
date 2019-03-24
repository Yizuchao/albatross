package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.command.PubAckCommand;
import com.yogi.albatross.common.mqtt.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;

@Processor(targetType = FixedHeadType.PUBACK)
public class PubAckDecoder extends DecoderAdapter<PubAckCommand> {
    @Override
    protected PubAckCommand process0(MqttCommand packet) throws Exception {
        String packetId = readUTF(packet.getByteBuf(), 2);
        PubAckCommand command=new PubAckCommand();
        command.setPacketId(packetId);
        return command;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, PubAckCommand request) throws Exception {
        return null;
    }
}
