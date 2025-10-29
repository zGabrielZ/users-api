package br.com.gabrielferreira.users.dtos.output;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record ProjectOutputDTO(
        UUID projectExternalId,

        String name
) implements Serializable {
}
