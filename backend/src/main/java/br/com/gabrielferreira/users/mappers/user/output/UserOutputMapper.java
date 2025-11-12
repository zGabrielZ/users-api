package br.com.gabrielferreira.users.mappers.user.output;

import br.com.gabrielferreira.users.dtos.output.user.UserOutputDTO;
import br.com.gabrielferreira.users.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserOutputMapper {

    UserOutputDTO toOutputDto(UserEntity userEntity);
}
