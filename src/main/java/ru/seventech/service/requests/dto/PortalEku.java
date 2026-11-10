package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PortalEku {

    NEW(1, "Новая"),
    EDIT(2, "На редактировании"),
    READY(3, "Готова к публикации"),
    PUBLISH(4, "Опубликована"),
    REMOVE(5, "Снята с публикации");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PortalEku fromId(int id) {
        for (PortalEku eku : values()) {
            if (eku.id == id) return eku;
        }
        return null;
    }

}
