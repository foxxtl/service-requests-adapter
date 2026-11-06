package ru.seventech.service.requests.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class NoticeErrorDto {

    private String serviceCaption;
    private String serviceCode;
    private String errorMessage;

    public NoticeErrorDto(ServiceDto service, String errorMessage) {
        this.serviceCaption = service.getCaption();
        this.serviceCode = service.getCode();
        this.errorMessage = errorMessage;
    }

    public NoticeErrorDto(String errorMessage) {
        this.serviceCaption = StringUtils.EMPTY;
        this.serviceCode = StringUtils.EMPTY;
        this.errorMessage = errorMessage;
    }
}
