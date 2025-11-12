package br.com.gabrielferreira.users.api.mappers.role.output;

import br.com.gabrielferreira.users.api.dtos.output.role.RoleOutputDTO;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleOutputMapper {

    RoleOutputDTO toOutputDto(RoleEntity roleEntity);

    default List<RoleOutputDTO> toCollectionDto(List<RoleEntity> roleEntities) {
        if (CollectionUtils.isEmpty(roleEntities)) {
            return Collections.emptyList();
        }
        return roleEntities.stream()
                .map(this::toOutputDto)
                .toList();
    }
}
