package ru.seventech.service.requests.dto;

import org.springframework.data.domain.Pageable;

public record SearchServiceCacheKey(ItemSearchRequestDto requestBody, Pageable pageable) {
}
