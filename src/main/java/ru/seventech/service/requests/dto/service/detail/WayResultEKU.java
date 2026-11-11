package ru.seventech.service.requests.dto.service.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WayResultEKU implements Uuidable {

    private UUID uuid;
    private WayDeliveryResult wayDeliveryResult;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WayDeliveryResult implements Uuidable {
        private UUID uuid;
        private String resultDeliveryMethod;
        private Integer itemOrder;
        private String type;
        private String slug;
    }
}
