package ru.seventech.service.requests.dto.service.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubsideType {

    SERVICES(1, "Субсидии на оказание услуг (выполнение работ)"),
    INDIVIDUAL_PAYMENTS(2, "Субсидии для последующих выплат физическим лицам (в том числе гранты - премии)"),
    OPERATORS(3, "Субсидии 'операторам' (для последующего предоставления средств иным лицам)"),
    PURCHASE(4, "Субсидии на приобретение товаров, работ, услуг"),
    PRODUCTION(5, "Субсидии на производство (реализацию) продукции"),
    CAPITAL_INVESTMENTS(6, "Субсидии на осуществление капитальных вложений"),
    NOT_APPLICABLE(7, "Не применимо");


    private final int id;
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SubsideType fromId(int id) {
        for (SubsideType type : values()) {
            if (type.id == id) return type;
        }
        return null;
    }
}
