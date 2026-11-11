package ru.seventech.service.requests.dto.service.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeServicePayment {

    PAYMENT(1, "Услуга платная"),
    FREE(2, "Услуга бесплатная"),
    NOT_VISIBLE(3, "Не отображать на портале");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeServicePayment fromId(int id) {
        for (TypeServicePayment type : values()) {
            if (type.id == id) return type;
        }
        return null;
    }

}
