package ru.seventech.service.requests.dto.service.detail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;
import ru.seventech.service.requests.dto.service.fields.WayGetDoc;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientDocs implements Uuidable {

    private UUID uuid;
    private String orderDoc;
    private List<WayGetDoc> wayGetDoc;
    private String url;
    private String numberDetail;
    private String name;
}
