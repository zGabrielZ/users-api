package br.com.gabrielferreira.users.api.mappers.project.input;

import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.core.utils.Constants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProjectInputMapper {

    @Mapping(source = "name", target = "name", qualifiedByName = "trimIfNotBlank")
    ProjectEntity toProjectEntity(CreateProjectInputDTO createProjectInputDTO);

    @Mapping(source = "name", target = "name", qualifiedByName = "trimIfNotBlank")
    ProjectEntity toProjectEntity(UpdateProjectInputDTO updateProjectInputDTO);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }
}
