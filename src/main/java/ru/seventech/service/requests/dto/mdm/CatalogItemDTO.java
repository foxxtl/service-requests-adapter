package ru.seventech.service.requests.dto.mdm;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class CatalogItemDTO extends HashMap<String, Object> {

    public UUID getUuid() {
        String uuid = (String) get("uuid");
        return StringUtils.isEmpty(uuid) ? null : UUID.fromString(uuid);
    }

}
