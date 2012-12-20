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

import java.util.Date;
import sh.lab.jcorrelat.Message.Facility;
import sh.lab.jcorrelat.Message.Severity;

public class MessageFactory {
    
    private final Message message;
    
    private MessageFactory() {
        this.message = new Message();
    }
    
    public static MessageFactory messageFactory() {
        return new MessageFactory();
    }

    public MessageFactory setId(final String id) {
        message.setId(id);
        
        return this;
    }

    public MessageFactory setTime(final Date time) {
        message.setTime(time);
        
        return this;
    }

    public MessageFactory setHost(final String host) {
        message.setHost(host);
        
        return this;
    }

    public MessageFactory setFacility(final Facility facility) {
        message.setFacility(facility);
        
        return this;
    }

    public MessageFactory setSeverity(final Severity severity) {
        message.setSeverity(severity);
        
        return this;
    }

    public MessageFactory setProgram(final String program) {
        message.setProgram(program);
        
        return this;
    }

    public MessageFactory setMessage(final String message) {
        this.message.setMessage(message);
        
        return this;
    }
    
    public MessageFactory addTag(final String tag) {
        this.message.addTag(tag);
        
        return this;
    }
    
    public MessageFactory addStructure(final String key, final Object value) {
        this.message.addStructure(key, value);
        
        return this;
    }
    
    public Message message() {
        return this.message;
    }
}
