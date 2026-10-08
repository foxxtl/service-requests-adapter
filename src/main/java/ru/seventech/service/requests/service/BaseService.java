package ru.seventech.service.requests.service;

import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.dto.auth.UserInfo;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.mdm.PageResponse;
import ru.seventech.service.requests.service.mdm.SearchService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Getter
public abstract class BaseService {

    protected final SearchService searchService;
    private final BaseSecurityHelper securityHelper;
    private final RestTemplate rest;

    public BaseService(SearchService searchService, BaseSecurityHelper securityHelper, RestTemplate rest) {
        this.searchService = searchService;
        this.securityHelper = securityHelper;
        this.rest = rest;
    }

    protected HttpHeaders headers(boolean isPublic, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        prepareAuthenticationHeader(headers, isPublic);
        return headers;
    }

    protected HttpHeaders headers(boolean isPublic) {
        return headers(isPublic, MediaType.APPLICATION_JSON);
    }

    protected <T> RequestEntity<T> request(T object, HttpHeaders headers, HttpMethod method, URI uri) {
        return new RequestEntity<>(object, headers, method, uri);
    }

    protected RequestEntity request(HttpHeaders headers, HttpMethod method, URI uri) {
        return new RequestEntity(headers, method, uri);
    }

    protected String getToken() {
        return securityHelper.obtainSysAuthToken();
    }

    private void prepareAuthenticationHeader(HttpHeaders headers, boolean isPublic) {
        if (isPublic) {
            headers.set("Authorization", getToken());
        } else {
            String token = securityHelper.prepareAuthenticationHeader();

            if (nonNull(token)) {
                headers.set("Authorization", token);
            }
        }
    }

    public <T> T entityAction(Object requestBody, URI url, HttpMethod method, HttpHeaders headers,
                              ParameterizedTypeReference<T> typeReference) {
        ResponseEntity<T> response = getRest().
                exchange(
                        request(requestBody, headers, method, url),
                        typeReference
                );

        return response.getBody();
    }

    public <T> List<T> getItemsListAction(Object requestBody, URI url, HttpMethod method, HttpHeaders headers,
                                          ParameterizedTypeReference<PageResponse<T>> typeReference) {
        ResponseEntity<PageResponse<T>> response = getRest().
                exchange(
                        request(requestBody, headers, method, url),
                        typeReference
                );

        if (nonNull(response.getBody())) {
            return response.getBody().getContent();
        }
        return List.of();
    }

    public UserInfo getCurrentUserOnline() {
        return securityHelper.getCurrentUserOnline();
    }

    public UUID getUserOrgId() {
        UserInfo userInfo = getCurrentUserOnline();
        return UUID.fromString(userInfo.getOrganization().getId());
    }

    public String getUserEmail() {
        UserInfo userInfo = getCurrentUserOnline();
        return userInfo.getUser().getEmail();
    }

    /**
     * Если пользователь не обладает одной из ролей, он получает доступ только к записям своей организации
     */
    public boolean requiredUserOrgFilter() {
        UserInfo userInfo = getCurrentUserOnline();
        return !userInfo.hasAnyRole(List.of("service_req_employee", "service_req_chief"));
    }

}

