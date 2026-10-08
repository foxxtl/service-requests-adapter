package ru.seventech.service.requests.dto.mdm;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResponse<T> implements Serializable {
    private List<T> content;
    private long number;
    private long size;
    private long totalElements;
    private long totalPages;
}
