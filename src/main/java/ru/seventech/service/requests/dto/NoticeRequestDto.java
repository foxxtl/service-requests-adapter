package ru.seventech.service.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class NoticeRequestDto {

    @Schema(name = "Массив uuid услуг")
    private List<UUID> uuidList;

    @NotBlank
    @Schema(name = "Текст уведомления")
    private String noticeText;
}
