package ru.seventech.service.requests.service.mdm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.seventech.basetemplate.error.NotFoundException;
import ru.seventech.service.requests.dto.NoticeTemplate;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.enums.CatalogNames;
import ru.seventech.service.requests.dto.mdm.CatalogItemDTO;
import ru.seventech.service.requests.dto.ProviderDto;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final MdmApiService mdmApiService;

    public List<ProviderDto> searchProviders(Map<String, Object> filter) {
        return mdmApiService.searchCatalogItems(CatalogNames.SERVICE_CHANGE_REQUEST.getOrigin(),
                filter, new ParameterizedTypeReference<>() {
                });
    }

    public ProviderDto getProviderItem(UUID uuid) {
        return mdmApiService.getCatalogItem(CatalogNames.SERVICE_CHANGE_REQUEST.getOrigin(),
                uuid, new ParameterizedTypeReference<>() {
                });
    }

    public List<ServiceDto> searchServices(Map<String, Object> filter) {
        return mdmApiService.searchCatalogItems(CatalogNames.SERVICE.getOrigin(),
                filter, new ParameterizedTypeReference<>() {
                });
    }

    public ServiceDto getServiceItem(UUID uuid) {
        return mdmApiService.getCatalogItem(CatalogNames.SERVICE.getOrigin(),
                uuid, new ParameterizedTypeReference<>() {
                });
    }


    public NoticeTemplate searchNoticeTemplate(String name) {
        List<NoticeTemplate> template = mdmApiService.searchCatalogItems(CatalogNames.NOTICE_TEMPLATE.getOrigin(),
                Map.of("name", name), new ParameterizedTypeReference<>() {
                });
        return getFirstOrThrowEx(template, () -> new NotFoundException("В справочнике notice_template не найден шаблон для отправки email уведомления. name: ", name));
    }

    private <T> T getFirstOrThrowEx(List<T> response, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (CollectionUtils.isEmpty(response) || Objects.isNull(response.get(0))) {
            throw exceptionSupplier.get();
        }
        return response.get(0);
    }

}
