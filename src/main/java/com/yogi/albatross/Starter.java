package com.yogi.albatross;

import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.decoder.MQTTDispatchDecoder;
import com.yogi.albatross.handler.ServerIdleStateHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
    private static final Logger logger= LoggerFactory.getLogger(Starter.class);
    public static void main(String args[]){
        int port=10090;
        if(args!=null && args.length>=1){
            port= NumberUtils.toInt(args[0],port);
        }
        run(port);
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
                    //ChannelOutboundHandler 在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler。
                    pipeline.addLast(new MQTTDispatchDecoder());//mqtt decoder
                }
            });
            ChannelFuture future = bootstrap.channel(NioServerSocketChannel.class).bind();

            //init
            init();

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

    /**
     * init
     */
    public static void init(){
        //dao init
        DaoManager.init("com.yogi.albatross.db");

    }
}
