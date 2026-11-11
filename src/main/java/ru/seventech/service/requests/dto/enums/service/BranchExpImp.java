package ru.seventech.service.requests.dto.enums.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BranchExpImp {

    INDUSTRY(1, "Промышленность"),
    APK(2, "АПК"),
    SERVICE(3, "Услуги");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BranchExpImp fromId(int id) {
        for (BranchExpImp imp : values()) {
            if (imp.id == id) return imp;
        }
        return null;
    }
}
