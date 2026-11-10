package ru.seventech.service.requests.service.request;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.ItemSearchRequestDto;
import ru.seventech.service.requests.dto.PortalEku;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.dto.SearchServiceCacheKey;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.mdm.SearchService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceResearchService extends BaseService {

    @Value("${cache.search.time-to-live}")
    private Duration timeToLive;;

    @Value("${cache.search.capacity}")
    private int capacity;

    private final Cache<SearchServiceCacheKey, List<ServiceDto>> cacheMap;

    public ServiceResearchService(SearchService searchService, BaseSecurityHelper securityHelper, RestTemplate rest) {
        super(searchService, securityHelper, rest);
        this.cacheMap = Caffeine.newBuilder().expireAfterWrite(timeToLive).maximumSize(capacity).build();
    }

    public ServiceDto getItem(UUID uuid) {
        ServiceDto response = searchService.getServiceItem(uuid);
        if (requiredUserOrgFilter() && !Objects.equals(response.getOrgId().getUuid(), getUserOrgId())) {
            throw new AccessDeniedException("Запрашиваемая запись не принадлежит организации");
        }

        mapReqAddValues(List.of(response), Collections.emptyList());
        mapProviderField(List.of(response), Collections.emptyList());
        return response;
    }

    public List<ServiceDto> search(ItemSearchRequestDto requestDto) {

        List<ServiceDto> cacheResult = cacheMap.getIfPresent(new SearchServiceCacheKey(requestDto));
        if (!CollectionUtils.isEmpty(cacheResult)) {
            return cacheResult;
        }

        Set<UUID> serviceIdList = null;
        List<ServiceDto> serviceList = new ArrayList<>();
        List<ProviderDto> providerDtos = new ArrayList<>();
        List<ServiceReqAddValuesDto> reqAddValuesDtos = new ArrayList<>();

        boolean hasServiceFilter = !CollectionUtils.isEmpty(requestDto.getServiceFilter());
        boolean hasReqAddValuesFilter = !CollectionUtils.isEmpty(requestDto.getReqAddValuesFilter());
        boolean hasChangeRequestFilter = Boolean.TRUE.equals(
                CollectionUtils.isEmpty(requestDto.getChangeRequestFilter()) ? null : requestDto.getChangeRequestFilter().get("hasActiveRequest")
        );
        boolean hasAnyFilter = hasServiceFilter || hasReqAddValuesFilter || hasChangeRequestFilter;

        if (hasReqAddValuesFilter) {
            reqAddValuesDtos = searchService.searchServiceReqAddValues(requestDto.getReqAddValuesFilter());

            if (CollectionUtils.isEmpty(reqAddValuesDtos)) {
                saveToCache(requestDto, serviceList);
                return serviceList;
            }
            serviceIdList = reqAddValuesDtos.stream().map(ServiceReqAddValuesDto::getServiceId).collect(Collectors.toSet());
        }

        if (hasChangeRequestFilter) {
            Map<String, Object> filter = getChangeRequestFilter(serviceIdList);
            providerDtos = searchService.searchProviders(filter);

            if (CollectionUtils.isEmpty(providerDtos)) {
                saveToCache(requestDto, serviceList);
                return serviceList;
            }

            Set<UUID> providerServiceIds = providerDtos.stream().map(ProviderDto::getServiceId).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(serviceIdList)) {
                serviceIdList = providerServiceIds;
            } else {
                serviceIdList.retainAll(providerServiceIds);
                if (serviceIdList.isEmpty()) {
                    saveToCache(requestDto, serviceList);
                    return serviceList;
                }
            }
        }

        Map<String, Object> serviceFilter = getServiceFilter(requestDto.getServiceFilter(), hasAnyFilter, serviceIdList);
        serviceList = searchService.searchServices(serviceFilter);

        if (CollectionUtils.isEmpty(serviceList)) {
            saveToCache(requestDto, serviceList);
            return serviceList;
        }

        mapReqAddValues(serviceList, reqAddValuesDtos);
        mapProviderField(serviceList, providerDtos);
        saveToCache(requestDto, serviceList);
        return serviceList;
    }

    private void saveToCache(ItemSearchRequestDto requestDto, List<ServiceDto> serviceList) {
        cacheMap.put(new SearchServiceCacheKey(requestDto), serviceList);
    }

    private Map<String, Object> getChangeRequestFilter(Set<UUID> serviceIdList) {
        HashMap<String, Object> filter = new HashMap<>();
        if (!CollectionUtils.isEmpty(serviceIdList)) {
            filter.put("serviceId_in", serviceIdList);
        }
        filter.put("currentStatusId.code_in", List.of("sended_to_rec", "in_progress", "need_additional_info"));
        return filter;
    }

    private Map<String, Object> getServiceFilter(Map<String, Object> serviceFilter, boolean hasAnyFilter, Set<UUID> serviceIdList) {
        HashMap<String, Object> filter = CollectionUtils.isEmpty(serviceFilter) ? new HashMap<>() : new HashMap<>(serviceFilter);

        if (requiredUserOrgFilter()) {
            filter.put("orgId", getUserOrgId());
        }
        if (!hasAnyFilter) {
            filter.put("publicPortalEKU_in", List.of(PortalEku.READY.getId(), PortalEku.PUBLISH.getId()));
        }
        if (!CollectionUtils.isEmpty(serviceIdList)) {
            filter.put("uuid_in", serviceIdList);
        }
        return filter;
    }

    private void mapReqAddValues(List<ServiceDto> serviceList, List<ServiceReqAddValuesDto> reqAddValuesDtos) {
        if (!CollectionUtils.isEmpty(serviceList)) {
            List<UUID> serviceIds = serviceList.stream().map(ServiceDto::getUuid).toList();
            if (CollectionUtils.isEmpty(reqAddValuesDtos)) {
                reqAddValuesDtos = searchService.searchServiceReqAddValues(Map.of("serviceId_in", serviceIds));
            }
            Map<UUID, ServiceReqAddValuesDto> providerMap = reqAddValuesDtos.stream().collect(Collectors.toMap(ServiceReqAddValuesDto::getServiceId, item -> item));
            serviceList.forEach(service -> service.setServiceReqAddValues(providerMap.get(service.getUuid())));
        }
    }

    private void mapProviderField(List<ServiceDto> serviceList, List<ProviderDto> providerDtos) {
        if (!CollectionUtils.isEmpty(serviceList)) {
            List<UUID> serviceIds = serviceList.stream().map(ServiceDto::getUuid).toList();
            if (CollectionUtils.isEmpty(providerDtos)) {
                providerDtos = searchService.searchProviders(Map.of("serviceId_in", serviceIds));
            }
            Map<UUID, ProviderDto> providerMap = providerDtos.stream().collect(Collectors.toMap(ProviderDto::getServiceId, item -> item));
            serviceList.forEach(service -> service.setProviderInfo(providerMap.get(service.getUuid())));
        }
    }

}
