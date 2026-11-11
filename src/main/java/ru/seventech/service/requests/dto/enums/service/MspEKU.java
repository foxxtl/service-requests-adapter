package ru.seventech.service.requests.dto.enums.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MspEKU {

    INCLUDE(1, "Входит в реестр МСП"),
    NOT_INCLUDE(2, "Не входит в реестр МСП"),
    NO_RESTRICTIONS(3, "Ограничений на вхождение в реестр МСП нет");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MspEKU fromId(int id) {
        for (MspEKU eku : values()) {
            if (eku.id == id) return eku;
        }
        return null;
    }
}
