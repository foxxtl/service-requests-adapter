package ru.seventech.service.requests.dto.service.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientCategoryEKU {

    LEGAL(1, "Юридическое лицо"),
    INDIVIDUAL(2, "Индивидуальный предприниматель"),
    READY(3, "Физическое лицо"),
    PRIVATE(4, "Иностранное юридическое лицо"),
    FOREIGN_PRIVATE(5, "Иностранное физическое лицо"),
    OTHER(6, "Прочее"),
    SELF(7, "Самозанятый");

    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ClientCategoryEKU fromId(int id) {
        for (ClientCategoryEKU eku : values()) {
            if (eku.id == id) return eku;
        }
        return null;
    }

}
