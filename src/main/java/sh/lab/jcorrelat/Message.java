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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Message {

    public static enum Severity {
        EMERGENCY,
        ALERT,
        CRITICAL,
        ERROR,
        WARNING,
        NOTICE,
        INFORMATIONAL,
        DEBUG
    }

    public static enum Facility {
        USER,
        MAIL,
        DAEMON,
        SECURITY,
        INTERNAL,
        PRINTER,
        NETWORK,
        UUCP,
        CLOCK0,
        SECURITY1,
        FTP,
        NTP,
        LOG_AUDIT,
        LOG_ALERT,
        CLOCK1,
        LOCAL0,
        LOCAL1,
        LOCAL2,
        LOCAL3,
        LOCAL4,
        LOCAL5,
        LOCAL6,
        LOCAL7
    }
    
    @JsonIgnore()
    private String id;
    
    private Date time;
    private String host;
    private Facility facility;
    private Severity severity;
    private String program;
    private String message;
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private Set<String> tags = new HashSet<String>();

    public Message() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }
    
    public Date getTime() {
        return this.time;
    }

    public void setTime(final Date time) {
        this.time = time;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(final Facility facility) {
        this.facility = facility;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public void setSeverity(final Severity severity) {
        this.severity = severity;
    }
    
    public String getProgram() {
        return this.program;
    }

    public void setProgram(final String program) {
        this.program = program;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return this.data;
    }
    
    public void setData(final Map<String, Object> structures) {
        this.data = structures;
    }

    public void addData(final String key, final Object value) {
        this.data.put(key, value);
    }

    public void removeData(final String key) {
        this.data.remove(key);
    }

    public Object getData(final String key) {
        return this.data.get(key);
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public void setTags(final Set<String> tags) {
        this.tags = tags;
    }

    public void addTag(final String tag) {
        this.tags.add(tag);
    }

    public void removeTag(final String tag) {
        this.tags.remove(tag);
    }

    public boolean hasTag(final String tag) {
        return this.tags.contains(tag);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Message");
        s.append("[");
        s.append("time=").append(this.time.getTime() / 1000).append(", ");
        s.append("host=").append(this.host).append(", ");
        s.append("severity=").append(this.severity).append(", ");
        s.append("facility=").append(this.facility).append(", ");
        s.append("program=").append(this.program).append(", ");
        s.append("message=").append("\"").append(this.message).append("\"").append(", ");
        s.append("data=").append(this.data).append(", ");
        s.append("tags=").append(this.tags);
        s.append("]");

        return s.toString();
    }
}
