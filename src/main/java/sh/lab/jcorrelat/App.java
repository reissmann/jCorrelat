/*
 * Copyright 2011 Dustin Frisch<fooker@lab.sh>
 * 
 * This file is part of senchineru.
 * 
 * senchineru is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * senchineru is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with senchineru.  If not, see <http://www.gnu.org/licenses/>.
 */
package sh.lab.jcorrelat;

import ch.qos.logback.classic.Level;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.xml.bind.JAXBException;
import org.elasticsearch.common.netty.channel.DefaultChannelPipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    
    public static final String CONF_BIND_HOST = "127.0.0.1";
    public static final int CONF_BIND_PORT = 10514;
    
    public static final boolean CONF_ES_CLIENT = false;
    public static final String CONF_ES_BIND = "127.0.0.1";
    
    public static final Level CONF_LOG_LEVEL = Level.INFO;

    public static void main(final String[] args) throws Exception {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(CONF_LOG_LEVEL);
        
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        final MessagePersister persister = new MessagePersister();
        
        final EventExecutorGroup correlatorGroup = new DefaultEventExecutorGroup(32);
        final MessageCorrelationHandler correlatorHandler = new MessageCorrelationHandler(persister);
        
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(final SocketChannel channel) throws Exception {
                     final ChannelPipeline pipeline = channel.pipeline();
                     
                     pipeline.addLast("framer", new LineBasedFrameDecoder(MessageDecoder.BUFFER_SIZE));
                     pipeline.addLast("decoder", new MessageDecoder());
                     
                     pipeline.addLast(/*correlatorGroup,*/ "correlator", correlatorHandler);
                 }
             })
             .option(ChannelOption.SO_REUSEADDR, true)
             .option(ChannelOption.SO_BACKLOG, 8)
             .option(ChannelOption.TCP_NODELAY, true)
             .childOption(ChannelOption.SO_KEEPALIVE, true);

            final Channel channel = bootstrap.bind(CONF_BIND_HOST, CONF_BIND_PORT).sync().channel();

            channel.closeFuture().sync();
            
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
