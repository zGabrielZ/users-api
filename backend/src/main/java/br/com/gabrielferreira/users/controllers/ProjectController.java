package br.com.gabrielferreira.users.controllers;

import br.com.gabrielferreira.users.dtos.input.CreateProjectInputDTO;
import br.com.gabrielferreira.users.dtos.input.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.dtos.output.ProjectOutputDTO;
import br.com.gabrielferreira.users.mappers.project.input.ProjectInputMapper;
import br.com.gabrielferreira.users.mappers.project.output.ProjectOutputMapper;
import br.com.gabrielferreira.users.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectInputMapper projectInputMapper;

    private final ProjectOutputMapper projectOutputMapper;

    @PostMapping
    public ResponseEntity<ProjectOutputDTO> create(@Valid @RequestBody CreateProjectInputDTO payload) {
        var projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.save(projectEntity);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectOutputDTO);
    }

    @GetMapping("/{projectExternalId}")
    public ResponseEntity<ProjectOutputDTO> retrieveByExternalId(@PathVariable UUID projectExternalId) {
        var projectEntity = projectService.getOneProject(projectExternalId);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.ok(projectOutputDTO);
    }

    @PutMapping("/{projectExternalId}")
    public ResponseEntity<ProjectOutputDTO> update(@PathVariable UUID projectExternalId, @Valid @RequestBody UpdateProjectInputDTO payload) {
        var projectEntity = projectInputMapper.toProjectEntity(payload);
        projectEntity = projectService.update(projectExternalId, projectEntity);

        var projectOutputDTO = projectOutputMapper.toProjectOutputDto(projectEntity);
        return ResponseEntity.ok(projectOutputDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProjectOutputDTO>> listAll() {
        var projectEntities = projectService.getAllProjects();

        var projectOutputDTOs = projectOutputMapper.toCollectionDto(projectEntities);
        return ResponseEntity.ok(projectOutputDTOs);
    }

    @DeleteMapping("/{projectExternalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID projectExternalId) {
        projectService.delete(projectExternalId);
        return ResponseEntity.noContent().build();
    }
}
