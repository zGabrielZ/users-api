package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.filter.ProjectFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.project.ProjectOutputDTO;
import br.com.gabrielferreira.users.api.mappers.project.input.ProjectInputMapper;
import br.com.gabrielferreira.users.api.mappers.project.output.ProjectOutputMapper;
import br.com.gabrielferreira.users.core.utils.PageTranslate;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.ProjectFilter;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Projects", description = "Project management endpoints")
@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectInputMapper projectInputMapper;

    private final ProjectOutputMapper projectOutputMapper;

    @Operation(summary = "Create a new project")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully"
            )
    })
    @PostMapping
    public ResponseEntity<ProjectOutputDTO> create(@Valid @RequestBody CreateProjectInputDTO payload) {
        ProjectEntity projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.save(projectEntity);

        ProjectOutputDTO projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectOutputDTO);
    }

    @Operation(summary = "Retrieve a project by external ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project retrieved successfully"
            )
    })
    @GetMapping("/{projectExternalId}")
    public ResponseEntity<ProjectOutputDTO> retrieveByExternalId(
            @Parameter(
                    description = "External ID of the project",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @PathVariable UUID projectExternalId) {
        ProjectEntity projectEntity = projectService.getOneProject(projectExternalId);

        ProjectOutputDTO projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.ok(projectOutputDTO);
    }

    @Operation(summary = "Update an existing project")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully"
            )
    })
    @PutMapping("/{projectExternalId}")
    public ResponseEntity<ProjectOutputDTO> update(
            @Parameter(
                    description = "External ID of the project",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @PathVariable UUID projectExternalId,
            @Valid @RequestBody UpdateProjectInputDTO payload) {
        ProjectEntity projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.update(projectExternalId, projectEntity);

        ProjectOutputDTO projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.ok(projectOutputDTO);
    }

    @Operation(summary = "List all projects")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Projects listed successfully"
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<ProjectOutputDTO>> listAll(
            @ParameterObject @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.ASC)
            Pageable pageable,
            ProjectFilterDTO filter) {
        pageable = PageTranslate.toPageable(pageable, PageTranslate.getProjectPageableFieldsMapping());
        ProjectFilter projectFilter = projectInputMapper.toProjectFilter(filter);
        Page<ProjectEntity> projectEntities = projectService.getAllProjects(projectFilter, pageable);

        PageResponse<ProjectOutputDTO> projectOutputDTOs = projectOutputMapper.toPageDto(projectEntities);
        return ResponseEntity.ok(projectOutputDTOs);
    }

    @Operation(summary = "Delete a project by external ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully"
            )
    })
    @DeleteMapping("/{projectExternalId}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "External ID of the project",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @PathVariable UUID projectExternalId) {
        projectService.delete(projectExternalId);
        return ResponseEntity.noContent().build();
    }
}
