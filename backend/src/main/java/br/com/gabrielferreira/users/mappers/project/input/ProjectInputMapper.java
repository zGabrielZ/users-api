package br.com.gabrielferreira.users.mappers.project.input;

import br.com.gabrielferreira.users.dtos.input.CreateProjectInputDTO;
import br.com.gabrielferreira.users.dtos.input.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.entities.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectInputMapper {

    ProjectEntity toProjectEntity(CreateProjectInputDTO createProjectInputDTO);

    ProjectEntity toProjectEntity(UpdateProjectInputDTO updateProjectInputDTO);
}
