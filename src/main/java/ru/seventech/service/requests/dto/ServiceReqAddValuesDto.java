package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.mdm.CatalogItemDTO;
import ru.seventech.service.requests.dto.annotation.NotFetch;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "dto справочника service_req_add_values")
public class ServiceReqAddValuesDto {

    private UUID uuid;

    @NotNull
    private CatalogItemDTO serviceId;

    private LocalDate noticeSendDate;

    private LocalDate serviceActualizeDate;

    public ServiceReqAddValuesDto(UUID serviceId, LocalDate noticeSendDate) {
        CatalogItemDTO catalogItemDTO = new CatalogItemDTO();
        catalogItemDTO.put("uuid", serviceId);
        this.serviceId = catalogItemDTO;
        this.noticeSendDate = noticeSendDate;
    }

    @NotFetch
    @JsonIgnore
    public UUID getServiceUuid() {
        return Objects.nonNull(serviceId) ? serviceId.getUuid() : null;
    }

}
