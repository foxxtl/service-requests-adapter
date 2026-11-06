package ru.seventech.service.requests.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
@Component
public class PlaceholderRenderer {

    public String render(String template, Map<String, Object> placeholders) {
        if (StringUtils.isBlank(template) || CollectionUtils.isEmpty(placeholders)) {
            return template;
        }

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", Objects.toString(entry.getValue(), ""));
        }
        return template;
    }
}


