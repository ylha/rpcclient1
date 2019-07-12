package com.test;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class NettyRpcProxy {
    public static Object createProxy(final Class target) {
        return Proxy.newProxyInstance(target.getClassLoader(),new Class[]{target}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //封装ClassInfo
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(target.getName());
                classInfo.setMethodName(method.getName());
                classInfo.setObjects(args);
                classInfo.setTypes(method.getParameterTypes());
                //开始用Netty发送数据
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                final ResultHandler resultHandler = new ResultHandler();
                Bootstrap b = new Bootstrap();
                b.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //编码器
                        pipeline.addLast("encoder", new ObjectEncoder())
                                //解码器,构造方法第一个参数设置二进制的最大字节数,第二个参数设置具体使用哪个类解析器
                                .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                //客户端业务处理类
                                .addLast("handler", resultHandler);
                    }
                });
                ChannelFuture future = b.connect("127.0.0.1", 9999).sync();
                future.channel().writeAndFlush(classInfo).sync();
                future.channel().closeFuture().sync();
                return resultHandler.getResponse();
            }
        });
    }

    public static void main(String[] args) {


        HelloRPC helloRPC = (HelloRPC) NettyRpcProxy.createProxy(HelloRPC.class);
        System.out.println(helloRPC.hello("RPC"));
    }
}
