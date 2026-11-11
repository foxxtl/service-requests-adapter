package ru.seventech.service.requests.service.request;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.ItemSearchRequestDto;
import ru.seventech.service.requests.dto.ServiceSearchDto;
import ru.seventech.service.requests.dto.enums.service.PortalEku;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.dto.SearchServiceCacheKey;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.dto.mdm.PageResponse;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.mdm.SearchService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final SearchService searchService;

    private final Cache<SearchServiceCacheKey, PageResponse<ServiceSearchDto>> cacheMap;

    public ServiceResearchService(BaseSecurityHelper securityHelper, RestTemplate rest, SearchService searchService,
                                  @Value("${cache.search.time-to-live}") Duration timeToLive,
                                  @Value("${cache.search.capacity}") int capacity) {
        super(securityHelper, rest);
        this.searchService = searchService;
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

    public PageResponse<ServiceSearchDto> search(ItemSearchRequestDto requestDto, Pageable pageable) {
        PageResponse<ServiceSearchDto> cacheResult = cacheMap.getIfPresent(new SearchServiceCacheKey(requestDto, pageable));
        if (Objects.nonNull(cacheResult)) {
            return cacheResult;
        }

        Set<UUID> serviceIdList = null;
        List<ProviderDto> providerDtos = new ArrayList<>();
        PageResponse<ServiceSearchDto> serviceList = new PageResponse<>();
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
                saveToCache(requestDto, serviceList, pageable);
                return serviceList;
            }
            serviceIdList = reqAddValuesDtos.stream().map(ServiceReqAddValuesDto::getServiceUuid).collect(Collectors.toSet());
        }

        if (hasChangeRequestFilter) {
            Map<String, Object> filter = getChangeRequestFilter(serviceIdList);
            providerDtos = searchService.searchProviders(filter);

            if (CollectionUtils.isEmpty(providerDtos)) {
                saveToCache(requestDto, serviceList, pageable);
                return serviceList;
            }

            Set<UUID> providerServiceIds = providerDtos.stream().map(ProviderDto::getServiceUuid).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(serviceIdList)) {
                serviceIdList = providerServiceIds;
            } else {
                serviceIdList.retainAll(providerServiceIds);
                if (serviceIdList.isEmpty()) {
                    saveToCache(requestDto, serviceList, pageable);
                    return serviceList;
                }
            }
        }

        Map<String, Object> serviceFilter = getServiceFilter(requestDto.getServiceFilter(), hasAnyFilter, serviceIdList);
        serviceList = searchService.searchServices(serviceFilter, pageable);

        if (CollectionUtils.isEmpty(serviceList.getContent())) {
            saveToCache(requestDto, serviceList, pageable);
            return serviceList;
        }
        sortResult(serviceList.getContent(), pageable);
        mapReqAddValues(serviceList.getContent(), reqAddValuesDtos);
        mapProviderField(serviceList.getContent(), providerDtos);
        saveToCache(requestDto, serviceList, pageable);
        return serviceList;
    }

    private void saveToCache(ItemSearchRequestDto requestDto, PageResponse<ServiceSearchDto> response, Pageable pageable) {
        cacheMap.put(new SearchServiceCacheKey(requestDto, pageable), response);
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

    private void mapReqAddValues(List<ServiceSearchDto> serviceList, List<ServiceReqAddValuesDto> reqAddValuesDtos) {
        if (!CollectionUtils.isEmpty(serviceList)) {
            List<UUID> serviceIds = serviceList.stream().map(ServiceSearchDto::getUuid).toList();
            if (CollectionUtils.isEmpty(reqAddValuesDtos)) {
                reqAddValuesDtos = searchService.searchServiceReqAddValues(Map.of("serviceId_in", serviceIds));
            }
            Map<UUID, ServiceReqAddValuesDto> providerMap = reqAddValuesDtos.stream().collect(Collectors.toMap(ServiceReqAddValuesDto::getServiceUuid, item -> item));
            serviceList.forEach(service -> service.setServiceReqAddValues(providerMap.get(service.getUuid())));
        }
    }

    private void mapProviderField(List<ServiceSearchDto> serviceList, List<ProviderDto> providerDtos) {
        if (!CollectionUtils.isEmpty(serviceList)) {
            List<UUID> serviceIds = serviceList.stream().map(ServiceSearchDto::getUuid).toList();
            if (CollectionUtils.isEmpty(providerDtos)) {
                providerDtos = searchService.searchProviders(Map.of("serviceId_in", serviceIds));
            }
            Map<UUID, ProviderDto> providerMap = providerDtos.stream().collect(Collectors.toMap(ProviderDto::getServiceUuid, item -> item));
            serviceList.forEach(service -> service.setProviderInfo(providerMap.get(service.getUuid())));
        }
    }

    private void sortResult(List<ServiceSearchDto> serviceList, Pageable pageable) {
        if (CollectionUtils.isEmpty(serviceList) || pageable.getSort().isUnsorted()) {
            return;
        }

        Comparator<ServiceSearchDto> comparator = null;

        for (Sort.Order order : pageable.getSort()) {
            Comparator<ServiceSearchDto> currentComparator = getComparatorForProperty(order.getProperty());

            if (currentComparator != null) {
                if (order.isDescending()) {
                    currentComparator = currentComparator.reversed();
                }

                if (comparator == null) {
                    comparator = currentComparator;
                } else {
                    comparator = comparator.thenComparing(currentComparator);
                }
            }
        }
        if (comparator == null) {
            return;
        }
        serviceList.sort(comparator);
    }

    private Comparator<ServiceSearchDto> getComparatorForProperty(String property) {
        return switch (property) {
            case "orgId" -> Comparator.comparing(ServiceSearchDto::getOrgCaption, Comparator.nullsLast(Comparator.naturalOrder()));
            case "productName" -> Comparator.comparing(ServiceSearchDto::getProductName, Comparator.nullsLast(Comparator.naturalOrder()));
            case "productNumber" -> Comparator.comparing(ServiceSearchDto::getProductNumber, Comparator.nullsLast(Comparator.naturalOrder()));
            case "noticeSendDate" -> Comparator.comparing(ServiceSearchDto::getNoticeSendDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "serviceActualizeDate" -> Comparator.comparing(ServiceSearchDto::getServiceActualizeDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> null;
        };
    }

}
