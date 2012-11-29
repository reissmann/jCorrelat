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

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDecoder extends OneToOneDecoder {
    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    
    private final ObjectMapper mapper;

    public MessageDecoder() {
        this.mapper = new ObjectMapper();
    }

    @Override
    protected Object decode(final ChannelHandlerContext context, final Channel channel, final Object object) throws Exception {
        BigEndianHeapChannelBuffer buffer = (BigEndianHeapChannelBuffer) object;
        ChannelBufferInputStream stream = new ChannelBufferInputStream(buffer);

        Message message = this.mapper.readValue(stream, Message.class);

        return message;
    }
}
