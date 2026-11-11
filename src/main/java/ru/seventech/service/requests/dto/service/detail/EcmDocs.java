package ru.seventech.service.requests.dto.service.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EcmDocs implements Uuidable {

    private UUID uuid;
    private String ecmDocType;
    private UUID ecmDocUuid;
    private LocalDate ecmDocUpdateDate;
    private LocalDate ecmDocCreateDate;
    private String ecmDocName;
    private String ecmDocAuthor;
    private String docKind;
}
