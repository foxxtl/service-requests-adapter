package ru.seventech.service.requests.dto.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.OrgDto;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.dto.service.fields.PortalEku;
import ru.seventech.service.requests.dto.annotation.NotFetch;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO справочника service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSearchDto implements Uuidable {

    private UUID uuid;

    private String productNumber;

    private String productName;

    private PortalEku publicPortalEKU;

    private OrgDto orgId;

    @NotFetch
    @Schema(description = "Данные по связанной записи service_change_request")
    private ProviderDto providerInfo;

    @NotFetch
    @Schema(description = "Данные по связанной записи service_req_add_values")
    private ServiceReqAddValuesDto serviceReqAddValues;

    @NotFetch
    @JsonIgnore
    public String getOrgCaption() {
        return Objects.nonNull(orgId) ? orgId.getCaption() : "";
    }

    @NotFetch
    @JsonIgnore
    public UUID getOrgUuid() {
        return Objects.nonNull(orgId) ? orgId.getUuid() : null;
    }

    @NotFetch
    @JsonIgnore
    public LocalDate getNoticeSendDate() {
        return Objects.nonNull(serviceReqAddValues) ? serviceReqAddValues.getNoticeSendDate() : null;
    }

    @NotFetch
    @JsonIgnore
    public LocalDate getServiceActualizeDate() {
        return Objects.nonNull(serviceReqAddValues) ? serviceReqAddValues.getServiceActualizeDate() : null;
    }

}
