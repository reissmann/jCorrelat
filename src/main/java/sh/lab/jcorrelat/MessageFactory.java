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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.mvel2.templates.TemplateRuntime;
import sh.lab.jcorrelat.Facility;
import sh.lab.jcorrelat.Severity;

public class MessageFactory {
    
    private final Message message;
    
    private MessageFactory(final Message template) {
        this.message = new Message();
        
        this.message.setTime(Calendar.getInstance().getTime());
        
        this.message.setHost(template.getHost());
        this.message.setFacility(template.getFacility());
        this.message.setSeverity(template.getSeverity());
        this.message.setProgram(template.getProgram());
        this.message.setMessage(template.getMessage());
        
        this.message.setData(new HashMap<String, Object>(template.getData()));
        this.message.setTags(new HashSet<String>(template.getTags()));
    }
    
    public static MessageFactory messageFactory(final Message template) {
        return new MessageFactory(template);
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
    
    public MessageFactory setMessage(final String pattern) {
        this.message.setMessage(TemplateRuntime.eval(pattern, this.message).toString());
        
        return this;
    }
    
    public MessageFactory addData(final String key, final Object value) {
        this.message.addData(key, value);
        
        return this;
    }
    
    public MessageFactory removeData(final String key) {
        this.message.removeData(key);
        
        return this;
    }
    
    public MessageFactory addTag(final String tag) {
        this.message.addTag(tag);
        
        return this;
    }
    
    public MessageFactory removeTag(final String tag) {
        this.message.removeTag(tag);
        
        return this;
    }
    
    public Message message() {
        return this.message;
    }
}
