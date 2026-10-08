package ru.seventech.service.requests.service.notify;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.seventech.basetemplate.util.BaseSecurityHelper;
import ru.seventech.service.requests.dto.NoticeTemplate;
import ru.seventech.service.requests.service.BaseService;
import ru.seventech.service.requests.service.mdm.SearchService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Component
@Slf4j
public class NotificationService extends BaseService {


    @Value("${notify-api-adapter.base-url}")
    private String baseUrl;

    @Value("${notify-api-adapter.notify-url}")
    private String notifyUrl;

    public NotificationService(BaseSecurityHelper securityHelper, SearchService searchService, RestTemplate rest) {
        super(searchService, securityHelper, rest);
    }


    /**
     * Отправка email уведомления о результате обработки 61/62/63 запроса
     */
    @SneakyThrows
    public void sendEmail(String emailAddress, String templateName, Object parameters) {
        NoticeTemplate noticeTemplate = searchService.searchNoticeTemplate(templateName);

//            String emailMessage = placeholderRenderer.render(noticeTemplate.getTemplate(), placeholders);
//            String subject = placeholderRenderer.render(noticeTemplate.getSubject(), placeholders);

        sendMessage(emailAddress, UUID.randomUUID(), noticeTemplate.getTemplate(), noticeTemplate.getSubject());
        log.info("Email уведомление успешно отправлено по адресу {}", emailAddress);
    }

    private void sendMessage(String emailAddress, UUID recipientId, String emailMessage, String subject) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(baseUrl + notifyUrl);
        builder.addParameter("subject", subject);
        builder.addParameter("recipient_user", String.valueOf(recipientId));
        builder.addParameter("notice_type", "instant");
        builder.addParameter("transport_type", "email");
        builder.addParameter("transport_params", "{\"to\":\"" + emailAddress + "\"" + "}");
        URI uri = builder.build();

        getRest().exchange(new RequestEntity<>(wrapText(emailMessage), headers(true), HttpMethod.POST, uri), Object.class);
    }

    private Object wrapText(Object text) {
        return StringUtils.isNotBlank(text.toString()) ? ("\"" + text.toString().replaceAll("\"", "\\\\\"") + "\"") : null;
    }


}
