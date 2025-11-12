package br.com.gabrielferreira.users.api.mappers.user.output;

import br.com.gabrielferreira.users.api.dtos.output.user.UserOutputDTO;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserOutputMapper {

    UserOutputDTO toOutputDto(UserEntity userEntity);
}
