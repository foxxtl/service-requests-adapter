package ru.seventech.service.requests.dto.service.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LegalReasonServEKU implements Uuidable {
    private UUID uuid;
    private String name;
    private String link;
    private String numberDetail;
}
