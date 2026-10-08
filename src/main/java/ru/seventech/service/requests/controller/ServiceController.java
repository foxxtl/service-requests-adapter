package ru.seventech.service.requests.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seventech.service.requests.dto.ServiceDto;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.service.request.ServiceRequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/services")
@PreAuthorize("@baseSecurityHelper.getCurrentUserOnline().hasAnyRole({'service_req_employee','service_req_chief','service_req_provider'})")
public class ServiceController {

    private final ServiceRequestService requestService;

    @PostMapping("/notice")
    @Operation(summary = "Рассылка уведомлений об актуализации сведений в услугах")
    public ResponseEntity<HttpStatus> notice(@RequestBody List<UUID> uuidList) {
        requestService.notice(uuidList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/search")
    @Operation(summary = "Получение перечня услуг, доступных пользователю")
    public List<ServiceDto> search(@RequestBody ItemParamsRequestDTO requestDTO) {
        return requestService.search(requestDTO);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Получение данных услуги, доступной пользователю")
    public ServiceDto get(@PathVariable UUID uuid) {
        return requestService.getItem(uuid);
    }
}
