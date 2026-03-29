package br.com.gabrielferreira.users.api.mappers.user.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.user.UserOutputDTO;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserOutputMapper {

    UserOutputDTO toOutputDto(UserEntity userEntity);

    default PageResponse<UserOutputDTO> toPageDto(Page<UserEntity> userEntities) {
        if (CollectionUtils.isEmpty(userEntities.getContent())) {
            return PageResponse.<UserOutputDTO>builder()
                    .content(Collections.emptyList())
                    .number((long) userEntities.getNumber())
                    .size((long) userEntities.getSize())
                    .totalElements(userEntities.getTotalElements())
                    .totalPages((long) userEntities.getTotalPages())
                    .build();
        }
        List<UserOutputDTO> userOutputDTOList = userEntities.stream()
                .map(this::toOutputDto)
                .toList();

        return PageResponse.<UserOutputDTO>builder()
                .content(userOutputDTOList)
                .number((long) userEntities.getNumber())
                .size((long) userEntities.getSize())
                .totalElements(userEntities.getTotalElements())
                .totalPages((long) userEntities.getTotalPages())
                .build();
    }
}
