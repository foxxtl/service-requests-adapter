package ru.seventech.service.requests.dto.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.service.detail.ContactsOrgEkuDto;
import ru.seventech.service.requests.dto.service.detail.ClientDocs;
import ru.seventech.service.requests.dto.service.detail.CostServiceDescEKU;
import ru.seventech.service.requests.dto.service.detail.EcmDocs;
import ru.seventech.service.requests.dto.service.detail.LegalReasonServEKU;
import ru.seventech.service.requests.dto.service.detail.RegionServEKU;
import ru.seventech.service.requests.dto.service.detail.WayApplicationEKU;
import ru.seventech.service.requests.dto.service.detail.WayResultEKU;
import ru.seventech.service.requests.dto.service.fields.AvailabilityRegions;
import ru.seventech.service.requests.dto.service.fields.BranchExpImp;
import ru.seventech.service.requests.dto.service.fields.ClientCategoryEKU;
import ru.seventech.service.requests.dto.service.fields.MspEKU;
import ru.seventech.service.requests.dto.service.fields.SubsideRecSelection;
import ru.seventech.service.requests.dto.service.fields.SubsideType;
import ru.seventech.service.requests.dto.service.fields.TerrSementEKU;
import ru.seventech.service.requests.dto.service.fields.TypeServicePayment;
import ru.seventech.service.requests.dto.annotation.NotFetch;

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
    private DirectionVedId directionVedId;

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
    private List<EcmDocs> ecmDocs;
    private List<ClientDocs> clientDocs;
    private List<WayResultEKU> wayResultEKU;
    private List<RegionServEKU> regionServEKU;
    private List<WayApplicationEKU> wayApplicationEKU;
    private List<LegalReasonServEKU> legalReasonServEKU;
    private List<CostServiceDescEKU> costServiceDescEKU;
    private List<ContactsOrgEkuDto> contactsOrgEKU = new ArrayList<>();

    @NotFetch
    @JsonIgnore
    public List<String> getContactEmails() {
        return contactsOrgEKU.stream()
                .map(ContactsOrgEkuDto::getEmail)
                .filter(Objects::nonNull)
                .toList();
    }

}
