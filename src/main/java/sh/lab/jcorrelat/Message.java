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
    
    private String id;
    private Date time;
    private String host;
    private Facility facility;
    private Severity severity;
    private String program;
    private String message;

    public Message() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Message");
        s.append("[");
        s.append("time=").append(this.time).append(", ");
        s.append("host=").append(this.host).append(", ");
        s.append("severity=").append(this.severity).append(", ");
        s.append("facility=").append(this.facility).append(", ");
        s.append("program=").append(this.program).append(", ");
        s.append("message=").append(this.message);
        s.append("]");
        
        return s.toString();
    }
}
