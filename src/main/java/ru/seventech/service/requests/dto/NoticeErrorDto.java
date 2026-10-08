package ru.seventech.service.requests.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeErrorDto {

    private String serviceCaption;
    private String serviceCode;
    private String errorMessage;

    public NoticeErrorDto(ServiceDto service, String errorMessage) {
        this.serviceCaption = service.getCaption();
        this.serviceCode = service.getCode();
        this.errorMessage = errorMessage;
    }
}
