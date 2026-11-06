package ru.seventech.service.requests.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.NoticeRequestDto;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.dto.enums.CatalogNames;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.PlaceholderRenderer;
import ru.seventech.service.requests.service.mdm.MdmApiService;
import ru.seventech.service.requests.service.mdm.SearchService;
import ru.seventech.service.requests.service.notify.NotificationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceRequestService extends BaseService {

    private final MdmApiService mdmApiService;
    private final NotificationService notificationService;
    private final PlaceholderRenderer placeholderRenderer;

    private final String NO_CONTACT_PERSON = "По услуге ${productName} (Код продукта: ${productNumber}) не найдено контактное лицо Провайдера";
    private final String NOTICE_ERROR_MESSAGE = "По услуге ${productName} (Код продукта: ${productNumber}) не удалось отправить уведомление контактному лицу Провайдера на email ${contactEmail}";
    private final String EMPTY_SERVICE_LIST_MESSAGE = "По выбранным услугам не отправлено ни одного уведомления.";
    private final String EMPTY_REQUEST_DTO = "Не выбрана ни одна услуга для отправки уведомлений.";

    public ServiceRequestService(BaseSecurityHelper securityHelper, RestTemplate rest,
                                 SearchService searchService, NotificationService notificationService,
                                 MdmApiService mdmApiService, PlaceholderRenderer placeholderRenderer) {
        super(searchService, securityHelper, rest);
        this.notificationService = notificationService;
        this.mdmApiService = mdmApiService;
        this.placeholderRenderer = placeholderRenderer;
    }

    public void notice(NoticeRequestDto noticeRequestDto) {
        List<String> sendErrorsArray = new ArrayList<>();

        if (CollectionUtils.isEmpty(noticeRequestDto.getUuidList())) {
            sendErrorsArray.add(EMPTY_REQUEST_DTO);
            notificationService.sendSimpleEmail(getUserEmail(), "armProviderActualizationNoticeReport", Map.of("sendErrorsArray", sendErrorsArray));
            return;
        }

        List<ServiceDto> serviceList = searchService.searchServices(Map.of("uuid_in", noticeRequestDto.getUuidList()));

        if (CollectionUtils.isEmpty(serviceList)) {
            sendErrorsArray.add(EMPTY_SERVICE_LIST_MESSAGE);
        }

        for (ServiceDto serviceDto : serviceList) {
            List<String> mainEmails = serviceDto.getMainEmails();

            if (!CollectionUtils.isEmpty(mainEmails)) {
                for (String email : mainEmails) {
                    try {
                        notificationService.sendEmail(email, "armProviderNeedActualization", noticeRequestDto.getNoticeText());
                    } catch (Exception e) {
                        log.error("Error while sending email", e);
                        sendErrorsArray.add(placeholderRenderer.render(NOTICE_ERROR_MESSAGE, getErrorNoticePlaceholders(serviceDto, email)));
                    }
                    updateServiceReqAddValues(serviceDto.getUuid());
                }
            } else {
                sendErrorsArray.add(placeholderRenderer.render(NO_CONTACT_PERSON, getSdPlaceholders(serviceDto)));
            }
        }

        notificationService.sendEmail(getUserEmail(), "armProviderActualizationNoticeReport", sendErrorsArray);
    }

    private void updateServiceReqAddValues(UUID serviceUuid){
        List<ServiceReqAddValuesDto> serviceReqAdd = searchService.searchServiceReqAddValues(Map.of("serviceId", serviceUuid));
        if (!CollectionUtils.isEmpty(serviceReqAdd)) {
            ServiceReqAddValuesDto item = serviceReqAdd.get(0);
            item.setNoticeSendDate(LocalDate.now());
            mdmApiService.updateCatalogItem(CatalogNames.SERVICE_REQ_ADD_VALUES.getOrigin(), item.getUuid(), item, new ParameterizedTypeReference<>() {
            });
        } else {
            ServiceReqAddValuesDto item = new ServiceReqAddValuesDto(serviceUuid, LocalDate.now());
            mdmApiService.createCatalogItem(CatalogNames.SERVICE_REQ_ADD_VALUES.getOrigin(), item, new ParameterizedTypeReference<>() {
            });
        }
    }

    private Map<String, Object> getErrorNoticePlaceholders(ServiceDto serviceDto, String email) {
        Map<String, Object> placeholders = getSdPlaceholders(serviceDto);
        placeholders.put("contactEmail", email);
        return placeholders;
    }


    private Map<String, Object> getSdPlaceholders(ServiceDto serviceDto) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("productName", serviceDto.getProductName());
        placeholders.put("productNumber", serviceDto.getProductNumber());
        return placeholders;
    }


    public List<ServiceDto> search(ItemParamsRequestDTO requestDto) {
        if (requiredUserOrgFilter()) {
            requestDto.getFilter().put("orgId", getUserOrgId());
        }
        List<ServiceDto> response = searchService.searchServices(requestDto.getFilter());
        response = response.parallelStream().map(this::mapProviderField).collect(Collectors.toList());

        return response;
    }

    public ServiceDto getItem(UUID uuid) {
        ServiceDto response = searchService.getServiceItem(uuid);
        if (requiredUserOrgFilter() && !Objects.equals(response.getOrgId().getUuid(), getUserOrgId())) {
            throw new AccessDeniedException("Запрашиваемая запись не принадлежит организации");
        }

        return mapProviderField(response);
    }

    private ServiceDto mapProviderField(ServiceDto serviceDto) {
        List<ProviderDto> providerDtos = searchService.searchProviders(Map.of("serviceId", serviceDto.getUuid()));
        if (!CollectionUtils.isEmpty(providerDtos)) {
            serviceDto.setProviderInfo(providerDtos.get(0));
        }
        return serviceDto;
    }

}
