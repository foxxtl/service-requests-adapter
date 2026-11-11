package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.mdm.CatalogItemDTO;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO для справочника service_change_request")
public class ProviderDto {

    @NotBlank
    private String requestNumber;

    @NotNull
    private OrgDto orgProviderId;

    @NotNull
    private CatalogItemDTO serviceId;

    @NotNull
    private OffsetDateTime createDate;

    @NotNull
    private Integer startVersion;

    private Integer nextVersion;

    private LocalDate finishDate;


    @JsonIgnore
    public UUID getServiceUuid() {
        return Objects.nonNull(serviceId) ? serviceId.getUuid() : null;
    }
}
