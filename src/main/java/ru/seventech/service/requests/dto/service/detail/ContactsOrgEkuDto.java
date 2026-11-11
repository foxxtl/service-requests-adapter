package ru.seventech.service.requests.dto.service.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Детализация справочника service")
public class ContactsOrgEkuDto implements Uuidable {

    private UUID uuid;
    private String email;
}
