package ru.seventech.service.requests.service.mdm;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.dto.mdm.PageResponse;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.utils.MdmEntityToFetchFilterConverterServiceImpl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class MdmApiService extends BaseService {

    private final MdmEntityToFetchFilterConverterServiceImpl fetchFilterConverter;

    @Value("${mdm-adapter.base-url}")
    private String mdmAdapterServiceUrl;

    @Value("${mdm-adapter.catalog-items-search-url-v2}")
    private String searchPath;

    @Value("${mdm-adapter.catalog-item-url-v2}")
    private String itemPath;

    @Value("${mdm-adapter.catalog-item}")
    private String catalogItemPathV1;

    @Value("${mdm-adapter.catalog-item-create}")
    private String createPath;

    public MdmApiService(BaseSecurityHelper securityHelper, RestTemplate rest,
                         MdmEntityToFetchFilterConverterServiceImpl fetchFilterConverter) {
        super(securityHelper, rest);
        this.fetchFilterConverter = fetchFilterConverter;
    }

    public <T> List<T> searchCatalogItems(String catalogIdentifier, Map<String, Object> filter,
                                          Class<T> clazz, int deepRefs, int deepDetails,
                                          ParameterizedTypeReference<PageResponse<T>> typeReference) {
        PageResponse<T> pageResponse = searchPageCatalogItems(catalogIdentifier, filter, null, clazz, deepRefs, deepDetails, typeReference);
        return pageResponse.getContent();
    }

    public <T> PageResponse<T> searchPageCatalogItems(String catalogIdentifier, Map<String, Object> filter, Pageable pageable,
                                                      Class<T> clazz, int deepRefs, int deepDetails,
                                                      ParameterizedTypeReference<PageResponse<T>> typeReference) {
        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + searchPath,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier));

        if (Objects.nonNull(pageable)) {
            expand = setPageSizeParams(expand, pageable);
        }

        List<Object> fetchFields = fetchFilterConverter.convert(clazz, deepRefs, deepDetails);
        ItemParamsRequestDTO paramsRequestDto = new ItemParamsRequestDTO(filter, fetchFields);

        return getPageItemsListAction(paramsRequestDto, expand, HttpMethod.POST, headers(true), typeReference);
    }


    public <T> T getCatalogItem(String catalogIdentifier, UUID uuid,Class<T> clazz, int deepRefs, int deepDetails, ParameterizedTypeReference<T> typeReference) {

        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + itemPath,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier, "uuid", uuid));

        List<Object> fetchFields = fetchFilterConverter.convert(clazz, deepRefs, deepDetails);
        Map<String, Object> requestBody = Map.of("fetchFields", fetchFields);

        return entityAction(requestBody, expand, HttpMethod.POST, headers(true), typeReference);
    }

    public <T> T updateCatalogItem(String catalogIdentifier, UUID uuid, Object updateDto,
                                   ParameterizedTypeReference<T> typeReference) {
        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + catalogItemPathV1,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier, "uuid", uuid));

        Map<String, Object> requestBody = Map.of("fields", updateDto);

        return entityAction(requestBody, expand, HttpMethod.PUT, headers(true), typeReference);
    }

    public <T> T createCatalogItem(String catalogIdentifier, Object createDto,
                                   ParameterizedTypeReference<T> typeReference) {
        URI expand = getRest().getUriTemplateHandler().expand(mdmAdapterServiceUrl + createPath,
                ImmutableMap.of("catalogIdentifier", catalogIdentifier));

        Map<String, Object> requestBody = Map.of("fields", createDto);

        return entityAction(requestBody, expand, HttpMethod.POST, headers(true), typeReference);
    }

    private URI setPageSizeParams(URI expand, Pageable pageable) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(expand)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        return uriBuilder.build(true).toUri();
    }

    private URI setSortParams(URI expand, Pageable pageable) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(expand);

        pageable.getSort().forEach(order -> {
            String res = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            uriBuilder.queryParam("sort", res);
        });
        return uriBuilder.build(true).toUri();
    }

}
