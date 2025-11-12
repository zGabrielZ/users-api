package br.com.gabrielferreira.users.api.mappers.project.output;

import br.com.gabrielferreira.users.api.dtos.output.project.ProjectOutputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
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
