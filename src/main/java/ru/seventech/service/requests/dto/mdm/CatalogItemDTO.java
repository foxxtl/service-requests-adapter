package ru.seventech.service.requests.dto.mdm;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class CatalogItemDTO extends HashMap<String, Object> implements Uuidable {

    public UUID getUuid() {
        String uuid = (String) get("uuid");
        return StringUtils.isEmpty(uuid) ? null : UUID.fromString(uuid);
    }

}
