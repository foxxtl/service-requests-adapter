package ru.seventech.service.requests.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.mdm.SearchService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class ProviderService extends BaseService {

    public ProviderService(BaseSecurityHelper securityHelper, RestTemplate rest, SearchService searchService) {
        super(searchService, securityHelper, rest);
    }

    public List<ProviderDto> search(ItemParamsRequestDTO requestDto) {
        checkFilter(requestDto);

        if (requiredUserOrgFilter()) {
            requestDto.getFilter().put("orgId", getUserOrgId());
        }
        return searchService.searchProviders(requestDto.getFilter());
    }

    public ProviderDto getItem(UUID uuid) {
        ProviderDto response = searchService.getProviderItem(uuid);
        if (requiredUserOrgFilter() && !Objects.equals(response.getOrgProviderId().getUuid(), getUserOrgId())) {
            throw new AccessDeniedException("Запрашиваемая запись не принадлежит организации");
        }
        return response;
    }

    // TODO: а есть ли ограничения на фильтры? Можно ли передавать пустой фильтр ?
    private void checkFilter(ItemParamsRequestDTO requestDto) {
        if (CollectionUtils.isEmpty(requestDto.getFilter())) {
            throw new IllegalArgumentException("filter is empty");
        }
    }





}
