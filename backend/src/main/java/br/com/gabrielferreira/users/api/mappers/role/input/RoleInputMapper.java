package br.com.gabrielferreira.users.api.mappers.role.input;

import br.com.gabrielferreira.users.api.dtos.filter.RoleFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.UpdateRoleInputDTO;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.core.utils.Constants;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RoleInputMapper {

    @Mapping(target = "description", source = "description", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "authority", source = "authority", qualifiedByName = "trimIfNotBlank")
    RoleEntity toEntity(CreateRoleInputDTO createRoleInputDTO);

    @Mapping(target = "description", source = "description", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "authority", source = "authority", qualifiedByName = "trimIfNotBlank")
    RoleEntity toEntity(UpdateRoleInputDTO updateRoleInputDTO);

    @Mapping(source = "projectExternalId", target = "projectExternalId")
    @Mapping(source = "roleFilterDTO.roleExternalId", target = "roleExternalId")
    @Mapping(source = "roleFilterDTO.description", target = "description", qualifiedByName = "trimIfNotBlank")
    @Mapping(source = "roleFilterDTO.authority", target = "authority", qualifiedByName = "trimIfNotBlank")
    @Mapping(source = "roleFilterDTO.createdAtFrom", target = "createdAtFrom")
    @Mapping(source = "roleFilterDTO.createdAtTo", target = "createdAtTo")
    RoleFilter toRoleFilter(RoleFilterDTO roleFilterDTO, UUID projectExternalId);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }
}
