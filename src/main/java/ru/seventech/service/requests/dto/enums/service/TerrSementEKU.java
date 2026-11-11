package ru.seventech.service.requests.dto.enums.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TerrSementEKU {

    FEDERAL(1, "Федеральный"),
    REGION(2, "Региональный"),
    MUNICIPAL(3, "Муниципальный");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TerrSementEKU fromId(int id) {
        for (TerrSementEKU eku : values()) {
            if (eku.id == id) return eku;
        }
        return null;
    }
}
