package ru.seventech.service.requests.service.request;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.error.CustomMessageException;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.NoticeErrorDto;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.mdm.SearchService;
import ru.seventech.service.requests.service.notify.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceRequestService extends BaseService {

    private final NotificationService notificationService;

    private final String NOTICE_ERROR_MESSAGE = "При отправке уведомления произошла ошибка";
    private final String NO_CONTACT_PERSON = "У Провайдера отсутствует контактное лицо";

    public ServiceRequestService(BaseSecurityHelper securityHelper, RestTemplate rest,
                                 SearchService searchService, NotificationService notificationService) {
        super(searchService, securityHelper, rest);
        this.notificationService = notificationService;
    }

    public void notice(List<UUID> uuidList) {
        List<NoticeErrorDto> sendErrorsArray = new ArrayList<>();

        List<ServiceDto> serviceList = searchService.searchServices(Map.of("uuid_in", uuidList));
        for (ServiceDto serviceDto : serviceList) {

            //TODO: тут нет email адреса.
            //TODO: в цепочке может быть NPE (?)
            String email = serviceDto.getOrgId().getContactPerson();
            if (StringUtils.isNotBlank(email)) {
                try {
                    notificationService.sendEmail(email, "service_update_notice", null);
                } catch (CustomMessageException e) {
                    log.error("Ошибка при отправке email уведомления пользователю {}. Error message: {}", email, e.getMessage(), e);
                    sendErrorsArray.add(new NoticeErrorDto(serviceDto, NOTICE_ERROR_MESSAGE));
                }
            } else {
                sendErrorsArray.add(new NoticeErrorDto(serviceDto, NO_CONTACT_PERSON));
            }
        }
        //TODO: точно ли корректно добавятся все параметры в письмо?
        notificationService.sendEmail(getUserEmail(), "service_update_notice_report", sendErrorsArray);
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
