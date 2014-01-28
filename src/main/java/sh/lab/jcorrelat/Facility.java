package sh.lab.jcorrelat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonSerialize(using = ToStringSerializer.class)
public enum Facility {
    KERN,
    USER,
    MAIL,
    DAEMON,
    AUTH,
    SYSLOG,
    LPR,
    NEWS,
    UUCP,
    CRON,
    SECURITY,
    FTP,
    NTP,
    LOGAUDIT,
    LOGALERT,
    CLOCK,
    LOCAL0,
    LOCAL1,
    LOCAL2,
    LOCAL3,
    LOCAL4,
    LOCAL5,
    LOCAL6;

    @JsonCreator
    public static Facility forValue(@JsonProperty String value) {
        return Facility.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
