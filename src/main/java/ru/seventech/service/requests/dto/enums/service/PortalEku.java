package ru.seventech.service.requests.dto.enums.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PortalEku {

    NEW("1", "Новая"),
    EDIT("2", "На редактировании"),
    READY("3", "Готова к публикации"),
    PUBLISH("4", "Опубликована"),
    REMOVE("5", "Снята с публикации");

    private final String id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PortalEku fromId(String id) {
        for (PortalEku eku : values()) {
            if (Objects.equals(eku.id, id)) return eku;
        }
        return null;
    }

}
