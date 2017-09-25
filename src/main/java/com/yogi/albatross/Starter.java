package com.yogi.albatross;

import com.yogi.albatross.decoder.MQTTHandleDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
    private static final Logger logger= LoggerFactory.getLogger(Starter.class);
    public static void main(String rags[]){

    }
    public static void run(int port){
        NioEventLoopGroup selectorGroup=new NioEventLoopGroup();
        NioEventLoopGroup group=new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(selectorGroup,group).localAddress(port).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline=socketChannel.pipeline();
                    pipeline.addLast(new MQTTHandleDecoder());//mqtt decoder
                }
            });
            ChannelFuture future = bootstrap.channel(NioServerSocketChannel.class).bind();
            future.sync();//block
            logger.info("albatross success started on port:{}",port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            selectorGroup.shutdownGracefully();
            group.shutdownGracefully();
        }

    }
}
