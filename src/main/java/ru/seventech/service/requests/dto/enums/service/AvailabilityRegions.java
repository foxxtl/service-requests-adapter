package ru.seventech.service.requests.dto.enums.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AvailabilityRegions {

    PART_PROVIDED(0, "Услуга оказывается в выбранных регионах РФ"),
    NOT_PROVIDED(1, "Услуга НЕ оказывается в выбранных регионах РФ"),
    ALL_PROVIDED(2, "Услуга оказывается во всех регионах РФ");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AvailabilityRegions fromId(int id) {
        for (AvailabilityRegions region : values()) {
            if (region.id == id) return region;
        }
        return null;
    }
}
