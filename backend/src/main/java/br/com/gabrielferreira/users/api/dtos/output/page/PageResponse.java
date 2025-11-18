package br.com.gabrielferreira.users.api.dtos.output.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6981744038457280871L;

    @Schema(
            description = "List of items on the current page"
    )
    private transient List<T> content;

    @Schema(
            description = "Number of items per page",
            example = "10"
    )
    private Long size;

    @Schema(
            description = "Total number of items available",
            example = "50"
    )
    private Long totalElements;

    @Schema(
            description = "Total number of pages",
            example = "5"
    )
    private Long totalPages;

    @Schema(
            description = "Current page number (zero-based)",
            example = "0"
    )
    private Long number;
}
