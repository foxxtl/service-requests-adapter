package ru.seventech.service.requests.dto.service.detail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegionServEKU implements Uuidable {

    private UUID uuid;
    private Name name;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Name implements Uuidable{
        private UUID uuid;
        private String regionName;
    }
}
