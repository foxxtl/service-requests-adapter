package ru.seventech.service.requests.dto.service.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubsideRecSelection {

    TENDER(1, "Конкурс"),
    REQUEST(2, "Запрос предложений");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SubsideRecSelection fromId(int id) {
        for (SubsideRecSelection rec : values()) {
            if (rec.id == id) return rec;
        }
        return null;
    }
}
