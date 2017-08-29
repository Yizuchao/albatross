package netty.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatClientHandler extends ChannelDuplexHandler{
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.fillInStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            System.out.println("send heartbeat package");
            ByteBuf byteBuf=ctx.alloc().buffer(1).writeByte(3);;
            ctx.writeAndFlush(byteBuf);
            //IdleStateEvent event = (IdleStateEvent) evt;
           /* if (event.state() == IdleState.READER_IDLE) {
                System.out.println("read 空闲");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("write 空闲");
            }*/
        }
    }
}
