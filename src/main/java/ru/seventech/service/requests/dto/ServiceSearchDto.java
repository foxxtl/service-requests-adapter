package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.enums.service.PortalEku;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO справочника service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSearchDto {

    private UUID uuid;

    private String productNumber;

    private String productName;

    private PortalEku publicPortalEKU;

    private OrgDto orgId = new OrgDto();

    @Schema(description = "Данные по связанной записи service_change_request")
    private ProviderDto providerInfo;

    @Schema(description = "Данные по связанной записи service_req_add_values")
    private ServiceReqAddValuesDto serviceReqAddValues;

    @JsonIgnore
    public String getOrgCaption() {
        return Objects.nonNull(orgId) ? orgId.getCaption() : "";
    }

    @JsonIgnore
    public LocalDate getNoticeSendDate() {
        return Objects.nonNull(serviceReqAddValues) ? serviceReqAddValues.getNoticeSendDate() : null;
    }

    @JsonIgnore
    public LocalDate getServiceActualizeDate() {
        return Objects.nonNull(serviceReqAddValues) ? serviceReqAddValues.getServiceActualizeDate() : null;
    }


}
