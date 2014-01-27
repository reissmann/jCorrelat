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
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import javax.xml.bind.JAXBException;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.oio.OioDatagramChannelFactory;
import org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    
    public static final String CONF_BIND_HOST = "127.0.0.1";
    public static final int CONF_BIND_PORT = 10514;
    
    public static final boolean CONF_ES_CLIENT = false;
    public static final String CONF_ES_BIND = "127.0.0.1";
    
    public static final Level CONF_LOG_LEVEL = Level.WARN;

    public static void main(final String[] args) throws JAXBException {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(CONF_LOG_LEVEL);

//        final DatagramChannelFactory factory = new OioDatagramChannelFactory();
        final ServerChannelFactory factory = new OioServerSocketChannelFactory();

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);
//        final ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(factory);
        bootstrap.setOption("localAddress",
                            new InetSocketAddress(CONF_BIND_HOST,
                                                  CONF_BIND_PORT));
        
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.tcpNoDelay", true);

        final ChannelPipeline pipeline = bootstrap.getPipeline();

        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("parser", new MessageParser());
        pipeline.addLast("handler", new CorrelationChannelHandler());

        final Channel channel = bootstrap.bind();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
            public void run() {
                channel.close();
            }
        }));
    }
}
