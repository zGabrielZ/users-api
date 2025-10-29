package br.com.gabrielferreira.users.controllers;

import br.com.gabrielferreira.users.dtos.input.CreateProjectInputDTO;
import br.com.gabrielferreira.users.dtos.input.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.dtos.output.ProjectOutputDTO;
import br.com.gabrielferreira.users.mappers.project.input.ProjectInputMapper;
import br.com.gabrielferreira.users.mappers.project.output.ProjectOutputMapper;
import br.com.gabrielferreira.users.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Projects", description = "Project management endpoints")
@RestController
@RequestMapping("/projects")
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
        var projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.save(projectEntity);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
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
        var projectEntity = projectService.getOneProject(projectExternalId);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
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
        var projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.update(projectExternalId, projectEntity);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
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
    public ResponseEntity<List<ProjectOutputDTO>> listAll() {
        var projectEntities = projectService.getAllProjects();

        var projectOutputDTOs = projectOutputMapper.toCollectionDto(projectEntities);
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
