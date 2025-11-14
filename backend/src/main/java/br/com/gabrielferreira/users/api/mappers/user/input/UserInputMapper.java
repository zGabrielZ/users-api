package br.com.gabrielferreira.users.api.mappers.user.input;

import br.com.gabrielferreira.users.api.dtos.input.user.CreateUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateUserInputDTO;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.core.utils.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserInputMapper {

    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "password", source = "password", qualifiedByName = "trimIfNotBlank")
    UserEntity toEntity(CreateUserInputDTO createUserInputDTO);

    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trimIfNotBlank")
    UserEntity toEntity(UpdateUserInputDTO updateUserInputDTO);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }
}
