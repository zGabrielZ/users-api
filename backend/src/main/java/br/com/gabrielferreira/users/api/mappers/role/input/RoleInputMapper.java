package br.com.gabrielferreira.users.api.mappers.role.input;

import br.com.gabrielferreira.users.api.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.UpdateRoleInputDTO;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.core.utils.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoleInputMapper {

    @Mapping(target = "description", source = "description", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "authority", source = "authority", qualifiedByName = "trimIfNotBlank")
    RoleEntity toEntity(CreateRoleInputDTO createRoleInputDTO);

    @Mapping(target = "description", source = "description", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "authority", source = "authority", qualifiedByName = "trimIfNotBlank")
    RoleEntity toEntity(UpdateRoleInputDTO updateRoleInputDTO);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }
}
