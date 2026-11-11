package ru.seventech.service.requests.dto.service.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WayGetDoc {

    PAPER(1, "Бумажный"),
    ONLINE(2, "Электронный");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WayGetDoc fromId(int id) {
        for (WayGetDoc way : values()) {
            if (way.id == id) return way;
        }
        return null;
    }
}
