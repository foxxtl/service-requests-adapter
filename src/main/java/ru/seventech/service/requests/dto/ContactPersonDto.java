package ru.seventech.service.requests.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO детализации contact_persons справочника org")
public class ContactPersonDto {

    private boolean mainContact;

    private String email;




}
