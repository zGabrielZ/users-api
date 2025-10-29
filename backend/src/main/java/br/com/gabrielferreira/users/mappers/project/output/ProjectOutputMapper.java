package br.com.gabrielferreira.users.mappers.project.output;

import br.com.gabrielferreira.users.dtos.output.ProjectOutputDTO;
import br.com.gabrielferreira.users.entities.ProjectEntity;
import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectOutputMapper {

    ProjectOutputDTO toProjectOutputDto(ProjectEntity projectEntity);

    default List<ProjectOutputDTO> toCollectionDto(List<ProjectEntity> projectEntities) {
        if (CollectionUtils.isEmpty(projectEntities)) {
            return Collections.emptyList();
        }

        return projectEntities.stream()
                .map(this::toProjectOutputDto)
                .toList();
    }
}
