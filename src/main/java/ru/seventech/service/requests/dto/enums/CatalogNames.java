package ru.seventech.service.requests.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogNames {

    SERVICE("service"),
    NOTICE_TEMPLATE("notice_template"),
    SERVICE_CHANGE_REQUEST("service_change_request");

    private final String origin;
}
