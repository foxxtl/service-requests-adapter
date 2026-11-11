package ru.seventech.service.requests.dto.service.detail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WayApplicationEKU implements Uuidable{

    private UUID uuid;
    private WayApplication wayApplication;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WayApplication implements Uuidable {
        private UUID uuid;
        private String type;
        private Integer itemOrder;
        private String submissionMethod;
        private String slug;
    }
}
