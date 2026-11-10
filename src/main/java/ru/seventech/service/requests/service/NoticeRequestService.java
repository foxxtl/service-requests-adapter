package ru.seventech.service.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.NoticeRequestDto;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.dto.enums.CatalogNames;
import ru.seventech.service.requests.service.mdm.MdmApiService;
import ru.seventech.service.requests.service.mdm.SearchService;
import ru.seventech.service.requests.service.notify.NotificationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class NoticeRequestService extends BaseService {

    private final MdmApiService mdmApiService;
    private final SearchService searchService;
    private final NotificationService notificationService;
    private final PlaceholderRenderer placeholderRenderer;

    public NoticeRequestService(BaseSecurityHelper securityHelper, RestTemplate rest, MdmApiService mdmApiService,
                                SearchService searchService, NotificationService notificationService,
                                PlaceholderRenderer placeholderRenderer) {
        super(securityHelper, rest);
        this.mdmApiService = mdmApiService;
        this.searchService = searchService;
        this.notificationService = notificationService;
        this.placeholderRenderer = placeholderRenderer;
    }

    private final String SUCCESS = "Все уведомления отправлены без ошибок.";
    private final String EMPTY_REQUEST_DTO = "Не выбрана ни одна услуга для отправки уведомлений.";
    private final String EMPTY_SERVICE_LIST = "По выбранным услугам не отправлено ни одного уведомления.";
    private final String NO_CONTACT_PERSON = "По услуге ${productName} (Код продукта: ${productNumber}) не найдено контактное лицо Провайдера.";
    private final String ERROR_NOTICE = "По услуге ${productName} (Код продукта: ${productNumber}) не удалось отправить уведомление контактному лицу Провайдера на email ${contactEmail}.";


    public void notice(NoticeRequestDto noticeRequestDto) {
        List<String> sendErrorsArray = new ArrayList<>();

        if (CollectionUtils.isEmpty(noticeRequestDto.getUuidList())) {
            sendErrorsArray.add(EMPTY_REQUEST_DTO);
        } else {
            List<ServiceDto> serviceList = searchService.searchServices(Map.of("uuid_in", noticeRequestDto.getUuidList()));

            if (CollectionUtils.isEmpty(serviceList)) {
                sendErrorsArray.add(EMPTY_SERVICE_LIST);
            }

            for (ServiceDto serviceDto : serviceList) {
                List<String> contactEmails = serviceDto.getContactEmails();

                if (!CollectionUtils.isEmpty(contactEmails)) {
                    for (String email : contactEmails) {
                        try {
                            notificationService.sendEmail(email, "armProviderNeedActualization", noticeRequestDto.getNoticeText(), Map.of());
                        } catch (Exception e) {
                            log.error("Error while sending email", e);
                            sendErrorsArray.add(placeholderRenderer.render(ERROR_NOTICE, getErrorNoticePlaceholders(serviceDto, email)));
                        }
                        updateServiceReqAddValues(serviceDto.getUuid());
                    }
                } else {
                    sendErrorsArray.add(placeholderRenderer.render(NO_CONTACT_PERSON, getServiceInfoPlaceholders(serviceDto)));
                }
            }
            if (CollectionUtils.isEmpty(sendErrorsArray)) {
                sendErrorsArray.add(SUCCESS);
            }
        }
        notificationService.sendEmail(getUserEmail(), "armProviderActualizationNoticeReport", null, Map.of("sendErrorsArray", sendErrorsArray));
    }

    private void updateServiceReqAddValues(UUID serviceUuid) {
        List<ServiceReqAddValuesDto> serviceReqAdd = searchService.searchServiceReqAddValues(Map.of("serviceId", serviceUuid));
        if (CollectionUtils.isEmpty(serviceReqAdd)) {
            ServiceReqAddValuesDto item = new ServiceReqAddValuesDto(serviceUuid, LocalDate.now());
            mdmApiService.createCatalogItem(CatalogNames.SERVICE_REQ_ADD_VALUES.getOrigin(), item, new ParameterizedTypeReference<>() {
            });
        } else {
            ServiceReqAddValuesDto item = serviceReqAdd.get(0);
            item.setNoticeSendDate(LocalDate.now());
            mdmApiService.updateCatalogItem(CatalogNames.SERVICE_REQ_ADD_VALUES.getOrigin(), item.getUuid(), item, new ParameterizedTypeReference<>() {
            });
        }
    }

    private Map<String, Object> getErrorNoticePlaceholders(ServiceDto serviceDto, String email) {
        Map<String, Object> placeholders = getServiceInfoPlaceholders(serviceDto);
        placeholders.put("contactEmail", email);
        return placeholders;
    }

    private Map<String, Object> getServiceInfoPlaceholders(ServiceDto serviceDto) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("productName", serviceDto.getProductName());
        placeholders.put("productNumber", serviceDto.getProductNumber());
        return placeholders;
    }

}
