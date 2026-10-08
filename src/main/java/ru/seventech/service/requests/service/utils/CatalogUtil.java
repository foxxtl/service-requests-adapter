package ru.seventech.service.requests.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.seventech.service.requests.dto.enums.CatalogNames;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class CatalogUtil {

    @Value("${base-json-path}")
    String baseJsonPath;

    private final Map<String, List<Object>> fetchFieldsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Arrays.stream(CatalogNames.values())
                .forEach(catalog -> initFetchFields(catalog.getOrigin()));
    }

    private void initFetchFields(String catalogName) {
        try {
            String fullPath = baseJsonPath + catalogName + ".json";

            ClassPathResource classPathResource = new ClassPathResource(fullPath);
            if (classPathResource.exists()) {
                String jsonContent = new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, ArrayList<Object>> fields = objectMapper.readValue(jsonContent, new TypeReference<>() {
                });
                fetchFieldsMap.put(catalogName, fields.get("fetchFields"));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("При обработке считанного файла возникла ошибка", e);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать JSON для полей выборки.", e);
        }
    }

    public List<Object> getFetchFields(String catalogName) {
        return fetchFieldsMap.get(catalogName);
    }

}