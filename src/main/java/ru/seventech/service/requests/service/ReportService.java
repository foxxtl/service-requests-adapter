package ru.seventech.service.requests.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.seventech.service.requests.dto.ItemSearchRequestDto;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.ServiceReqAddValuesDto;
import ru.seventech.service.requests.service.request.ServiceResearchService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ServiceResearchService serviceResearchService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final List<String> HEADERS = List.of(
            "Наименование услуги",
            "Код услуги",
            "Статус публикации в ЕКУ",
            "Провайдер услуги",
            "Дата формирования запроса",
            "Дата актуализации услуги",
            "Есть запрос в работе"
    );

    public byte[] createXlsReport(ItemSearchRequestDto requestDTO) {
        List<ServiceDto> serviceList = serviceResearchService.search(requestDTO);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Услуги");
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADERS.size(); i++) {
                headerRow.createCell(i).setCellValue(HEADERS.get(i));
            }
            int rowIndex = 1;

            for (ServiceDto service : serviceList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(getStringValue(service.getProductName()));
                row.createCell(1).setCellValue(getStringValue(service.getProductNumber()));
                row.createCell(2).setCellValue(getStringValue(service.getPublicPortalEKU().getValue()));
                row.createCell(3).setCellValue(getStringValue(service.getOrgId().getCaption()));

                ProviderDto providerInfo = Objects.nonNull(service.getProviderInfo()) ? service.getProviderInfo() : new ProviderDto();
                ServiceReqAddValuesDto reqAddValuesDto = Objects.nonNull(service.getServiceReqAddValues()) ? service.getServiceReqAddValues() : new ServiceReqAddValuesDto();

                row.createCell(4).setCellValue(dateToString(providerInfo.getCreateDate()));
                row.createCell(5).setCellValue(dateToString(reqAddValuesDto.getServiceActualizeDate()));
                row.createCell(6).setCellValue(getStringValue(providerInfo.getRequestNumber()));
            }

            for (int i = 0; i < HEADERS.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка формирования XLSX-отчета", e);
        }
    }

    private String getStringValue(Object value) {
        return isNull(value) ? StringUtils.EMPTY : value.toString();
    }

    private String dateToString(LocalDate date) {
        return isNull(date) ? StringUtils.EMPTY : date.format(DATE_FORMATTER);
    }

    private String dateToString(OffsetDateTime date) {
        return isNull(date) ? StringUtils.EMPTY : date.format(DATE_TIME_FORMATTER);
    }

    public HttpHeaders reportHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache());
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("Список услуг.xlsx", StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );
        return headers;
    }

}
