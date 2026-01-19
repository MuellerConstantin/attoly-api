package de.mueller_constantin.attoly.api.web.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageDto<T> {

    @Builder.Default
    private List<T> content = new ArrayList<>();

    private long perPage;
    private long page;
    private long totalPages;
    private long totalElements;
}
