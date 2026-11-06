package ru.seventech.service.requests.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogNames {

    SERVICE("service"),
    NOTICE_TEMPLATE("notice_template"),
    SERVICE_CHANGE_REQUEST("service_change_request"),
    SERVICE_REQ_ADD_VALUES("service_req_add_values");

    private final String origin;
}
