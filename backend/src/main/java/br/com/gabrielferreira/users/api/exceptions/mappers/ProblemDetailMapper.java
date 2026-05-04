package br.com.gabrielferreira.users.api.exceptions.mappers;

import br.com.gabrielferreira.users.api.exceptions.dtos.ProblemDetailDTO;
import br.com.gabrielferreira.users.api.exceptions.dtos.ProblemDetailFieldDTO;
import org.mapstruct.Mapper;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProblemDetailMapper {

    ProblemDetailDTO toProblemDetailDto(
            Integer status,
            String type,
            String title,
            String detail,
            String message,
            OffsetDateTime timestamp,
            List<ProblemDetailFieldDTO> fields
    );

    ProblemDetailFieldDTO toProblemDetailFieldDto(String field, String message);
}
