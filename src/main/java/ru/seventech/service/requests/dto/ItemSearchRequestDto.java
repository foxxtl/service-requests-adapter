package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ItemSearchRequestDto {

    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Набор фильтров для справочника service")
    private Map<String, Object> serviceFilter;

    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Набор фильтров для справочника service_req_add_values")
    private Map<String, Object> reqAddValuesFilter;

    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(description = "Набор фильтров для справочника service_change_request")
    private Map<String, Object> changeRequestFilter;

}
