package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.enums.service.AvailabilityRegions;
import ru.seventech.service.requests.dto.enums.service.BranchExpImp;
import ru.seventech.service.requests.dto.enums.service.ClientCategoryEKU;
import ru.seventech.service.requests.dto.enums.service.MspEKU;
import ru.seventech.service.requests.dto.enums.service.SubsideRecSelection;
import ru.seventech.service.requests.dto.enums.service.SubsideType;
import ru.seventech.service.requests.dto.enums.service.TerrSementEKU;
import ru.seventech.service.requests.dto.enums.service.TypeServicePayment;
import ru.seventech.service.requests.dto.mdm.CatalogItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Schema(description = "DTO справочника service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDto extends ServiceSearchDto {

    private String caption;
    private String interfaceUrl;
    private String otherInformEKU;
    private String infoVED;
    private String productInfo;
    private String recipient;
    private String codeBudget;
    private String subsideMax;
    private String subsideGoal;
    private String nameGRBS;
    private String description;
    private String result;
    private String shortNameGRBS;
    private String termServiceSite;
    private String subsideSum;
    private String subsideUID;
    private String idExtService;

    private Integer duration;

    private Boolean freezed;
    private Boolean myExportServiceEKU;
    private Boolean serviceActivityEKU;

    // ссылка
    private CatalogItemDTO directionVedId;

    // значения из списка
    private MspEKU mspEKU;
    private SubsideType subsideType;
    private List<BranchExpImp> branchExpImp;
    private TerrSementEKU terrSementEKU;
    private TypeServicePayment typeServicePayment;
    private AvailabilityRegions availabilityRegions;
    private List<ClientCategoryEKU> clientCategoryEKU;
    private SubsideRecSelection subsideRecipientsSelection;

    // детализации
    private List<CatalogItemDTO> ecmDocs;
    private List<CatalogItemDTO> clientDocs;
    private List<CatalogItemDTO> wayResultEKU;
    private List<CatalogItemDTO> regionServEKU;
    private List<CatalogItemDTO> wayApplicationEKU;
    private List<CatalogItemDTO> legalReasonServEKU;
    private List<CatalogItemDTO> costServiceDescEKU;
    private List<ContactsOrgEkuDto> contactsOrgEKU = new ArrayList<>();

    @JsonIgnore
    public List<String> getContactEmails() {
        return contactsOrgEKU.stream()
                .map(ContactsOrgEkuDto::getEmail)
                .filter(Objects::nonNull)
                .toList();
    }

}
