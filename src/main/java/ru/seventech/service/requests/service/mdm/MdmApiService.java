package ru.seventech.service.requests.service.mdm;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.dto.mdm.PageResponse;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.utils.CatalogUtil;


import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MdmApiService extends BaseService {

    private final CatalogUtil catalogUtil;

    @Value("${mdm-adapter.base-url}")
    private String mdmAdapterServiceUrl;

    @Value("${mdm-adapter.catalog-items-search-url-v2}")
    private String searchPath;

    @Value("${mdm-adapter.catalog-item-url-v2}")
    private String itemPath;

    public MdmApiService(BaseSecurityHelper securityHelper, RestTemplate rest, CatalogUtil catalogUtil) {
        super(securityHelper, rest);
        this.catalogUtil = catalogUtil;
    }

    public <T> List<T> searchCatalogItems(String catalogIdentifier, Map<String, Object> filter,
                                          ParameterizedTypeReference<PageResponse<T>> typeReference) {
        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + searchPath,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier));

        List<Object> fetchFields = catalogUtil.getFetchFields(catalogIdentifier);
        ItemParamsRequestDTO requestBody = new ItemParamsRequestDTO(filter, fetchFields);

        return getItemsListAction(requestBody, expand, HttpMethod.POST, headers(true, MediaType.APPLICATION_JSON), typeReference);
    }

    public <T> T getCatalogItem(String catalogIdentifier, UUID uuid, ParameterizedTypeReference<T> typeReference) {

        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + itemPath,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier, "uuid", uuid));

        List<Object> fetchFields = catalogUtil.getFetchFields(catalogIdentifier);
        Map<String, Object> requestBody = Map.of("fetchFields", fetchFields);

        return entityAction(requestBody, expand, HttpMethod.POST, headers(true), typeReference);
    }
    
}
