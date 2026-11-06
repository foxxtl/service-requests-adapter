package ru.seventech.service.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO справочника org")
public class OrgDto {

    private UUID uuid;
    private String caption;
    private String inn;
    private String compGroup;

    private List<ContactPersonDto> contactPerson = new ArrayList<>();

}
