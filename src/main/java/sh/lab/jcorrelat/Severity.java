package sh.lab.jcorrelat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonSerialize(using = ToStringSerializer.class)
public enum Severity {
    EMERG,
    ALERT,
    CRIT,
    ERROR,
    WARNING,
    NOTICE,
    INFO,
    DEBUG;

    @JsonCreator
    public static Severity forValue(String value) {
        return Severity.valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
