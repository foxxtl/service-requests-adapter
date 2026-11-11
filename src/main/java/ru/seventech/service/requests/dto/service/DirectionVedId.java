package ru.seventech.service.requests.dto.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seventech.service.requests.dto.annotation.Uuidable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DirectionVedId implements Uuidable {

    private UUID uuid;
    private String directionName;
}
