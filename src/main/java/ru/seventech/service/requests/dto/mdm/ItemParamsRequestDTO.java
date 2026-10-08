package ru.seventech.service.requests.dto.mdm;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ItemParamsRequestDTO {

    private List<Object> fetchFields;

    @JsonSetter(nulls = Nulls.SKIP)
    private Map<String, Object> detail;

    @JsonSetter(nulls = Nulls.SKIP)
    private Map<String, Object> filter;

    public ItemParamsRequestDTO(Map<String, Object> filter, List<Object> fetchFields) {
        this.filter = filter;
        this.fetchFields = fetchFields;
    }
}
