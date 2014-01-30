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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDecoder.class);
    
    public static final int BUFFER_SIZE = 8192;
    
    private final byte[] buffer = new byte[BUFFER_SIZE];
    
    private final ObjectMapper mapper;

    public MessageDecoder() {
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        in.readBytes(this.buffer, 0, in.readableBytes());
        in.clear();
        
        LOG.trace("Received line: {}", this.buffer);
        
        final Message message = this.mapper.readValue(this.buffer, Message.class);
        
        LOG.trace("Received message: {}", message);
        
        out.add(message);
    }
}
