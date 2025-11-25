package br.com.gabrielferreira.users.api.mappers.role.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.role.RoleOutputDTO;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleOutputMapper {

    RoleOutputDTO toOutputDto(RoleEntity roleEntity);

    default PageResponse<RoleOutputDTO> toPageDto(Page<RoleEntity> roleEntities) {
        if (CollectionUtils.isEmpty(roleEntities.getContent())) {
            return PageResponse.<RoleOutputDTO>builder()
                    .content(Collections.emptyList())
                    .number((long) roleEntities.getNumber())
                    .size((long) roleEntities.getSize())
                    .totalElements(roleEntities.getTotalElements())
                    .totalPages((long) roleEntities.getTotalPages())
                    .build();
        }
        List<RoleOutputDTO> projectOutputDTOList = roleEntities.stream()
                .map(this::toOutputDto)
                .toList();

        return PageResponse.<RoleOutputDTO>builder()
                .content(projectOutputDTOList)
                .number((long) roleEntities.getNumber())
                .size((long) roleEntities.getSize())
                .totalElements(roleEntities.getTotalElements())
                .totalPages((long) roleEntities.getTotalPages())
                .build();
    }
}
