package br.com.gabrielferreira.users.api.mappers.project.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.project.ProjectOutputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectOutputMapper {

    ProjectOutputDTO toProjectOutputDto(ProjectEntity projectEntity);

    default PageResponse<ProjectOutputDTO> toPageDto(Page<ProjectEntity> projectEntities) {
        if (CollectionUtils.isEmpty(projectEntities.getContent())) {
            return PageResponse.<ProjectOutputDTO>builder()
                    .content(Collections.emptyList())
                    .number((long) projectEntities.getNumber())
                    .size((long) projectEntities.getSize())
                    .totalElements(projectEntities.getTotalElements())
                    .totalPages((long) projectEntities.getTotalPages())
                    .build();
        }

        List<ProjectOutputDTO> projectOutputDTOList = projectEntities.stream()
                .map(this::toProjectOutputDto)
                .toList();

        return PageResponse.<ProjectOutputDTO>builder()
                .content(projectOutputDTOList)
                .number((long) projectEntities.getNumber())
                .size((long) projectEntities.getSize())
                .totalElements(projectEntities.getTotalElements())
                .totalPages((long) projectEntities.getTotalPages())
                .build();
    }
}
