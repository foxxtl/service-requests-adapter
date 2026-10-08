package ru.seventech.service.requests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.mdm.CatalogItemDTO;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO справочника service")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDto {

    //TODO: атрибут код услуги не известен
    private String code;

    private UUID uuid;

    private String caption;
    private String interfaceUrl;
    private String otherInformEKU;
    private String infoVED;
    private String availabilityRegions;
    private String productInfo;
    private String recipient;
    private String codeBudget;
    private String productNumber;
    private String subsideMax;
    private String productName;
    private String subsideGoal;
    private String nameGRBS;
    private String description;
    private String publicPortalEKU;
    private String result;
    private String shortNameGRBS;
    private String subsideRecipientsSelection;
    private String termServiceSite;
    private String mspEKU;
    private String subsideSum;
    private String terrSementEKU;
    private String typeServicePayment;
    private String subsideType;
    private String subsideUID;
    private String idExtService;

    private Integer duration;

    private Boolean freezed;
    private Boolean myExportServiceEKU;
    private Boolean serviceActivityEKU;

    private List<String> clientCategoryEKU;
    private List<String> branchExpImp;

    private OrgDto orgId;

    private ProviderDto providerInfo;

    // детализации
    private List<CatalogItemDTO> ecmDocs;
    private List<CatalogItemDTO> clientDocs;
    private List<CatalogItemDTO> wayResultEKU;
    private List<CatalogItemDTO> regionServEKU;
    private List<CatalogItemDTO> directionVedId;
    private List<CatalogItemDTO> contactsOrgEKU;
    private List<CatalogItemDTO> wayApplicationEKU;
    private List<CatalogItemDTO> legalReasonServEKU;
    private List<CatalogItemDTO> costServiceDescEKU;

}
