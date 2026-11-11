package ru.seventech.service.requests.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seventech.service.requests.dto.ItemSearchRequestDto;
import ru.seventech.service.requests.dto.NoticeRequestDto;
import ru.seventech.service.requests.dto.service.ServiceDto;
import ru.seventech.service.requests.dto.service.ServiceSearchDto;
import ru.seventech.service.requests.dto.mdm.PageResponse;
import ru.seventech.service.requests.service.NoticeRequestService;
import ru.seventech.service.requests.service.ReportService;
import ru.seventech.service.requests.service.request.ServiceResearchService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/service")
//@PreAuthorize("@baseSecurityHelper.getCurrentUserOnline().hasAnyRole({'service_req_employee','service_req_chief'})")
public class ServiceController {

    private final ServiceResearchService researchService;
    private final NoticeRequestService noticeRequestService;
    private final ReportService reportService;

    @PostMapping("/notice")
    @Operation(summary = "Рассылка уведомлений об актуализации сведений в услугах")
    public ResponseEntity<HttpStatus> notice(@RequestBody @Valid NoticeRequestDto noticeRequestDto) {
        noticeRequestService.notice(noticeRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/search")
    @Operation(summary = "Получение перечня услуг, доступных пользователю")
//    @PreAuthorize("@baseSecurityHelper.getCurrentUserOnline().hasAnyRole({'service_req_provider'})")
    public PageResponse<ServiceSearchDto> search(@RequestBody ItemSearchRequestDto requestDTO, @ParameterObject Pageable pageable) {
        return researchService.search(requestDTO, pageable);
    }

    @PostMapping("/report")
    @Operation(summary = "Получение перечня услуг, доступных пользователю")
//    @PreAuthorize("@baseSecurityHelper.getCurrentUserOnline().hasAnyRole({'service_req_provider'})")
    public ResponseEntity<byte[]> report(@RequestBody ItemSearchRequestDto requestDTO, @ParameterObject Pageable pageable) {
        byte[] report = reportService.createXlsReport(requestDTO, pageable);
        return new ResponseEntity<>(report, reportService.reportHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Получение данных услуги, доступной пользователю")
    public ServiceDto get(@PathVariable UUID uuid) {
        return researchService.getItem(uuid);
    }
}
