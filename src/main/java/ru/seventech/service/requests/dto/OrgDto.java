package ru.seventech.service.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO справочника org")
public class OrgDto implements Uuidable {

    private UUID uuid;
    private String caption;

}
