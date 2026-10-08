package ru.seventech.service.requests.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.seventech.service.requests.dto.mdm.ItemParamsRequestDTO;
import ru.seventech.service.requests.dto.ProviderDto;
import ru.seventech.service.requests.service.request.ProviderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/provider")
@PreAuthorize("@baseSecurityHelper.getCurrentUserOnline().hasAnyRole({'service_req_employee','service_req_chief','service_req_provider'})")
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/search")
    @Operation(summary = "Получение перечня услуг, доступных пользователю")
    public List<ProviderDto> search(@RequestBody ItemParamsRequestDTO requestDTO) {
        return providerService.search(requestDTO);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Получение данных услуги, доступной пользователю")
    public ProviderDto getItem(@PathVariable UUID uuid) {
        return providerService.getItem(uuid);
    }


}
