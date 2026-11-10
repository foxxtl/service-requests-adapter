package ru.seventech.service.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "dto справочника service_req_add_values")
public class ServiceReqAddValuesDto {

    private UUID uuid;

    private UUID serviceId;

    private LocalDate noticeSendDate;

    private LocalDate serviceActualizeDate;

    public ServiceReqAddValuesDto(UUID serviceId, LocalDate noticeSendDate) {
        this.serviceId = serviceId;
        this.noticeSendDate = noticeSendDate;

    }


}
