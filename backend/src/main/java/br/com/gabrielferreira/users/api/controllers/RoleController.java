package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.dtos.input.role.UpdateRoleInputDTO;
import br.com.gabrielferreira.users.dtos.output.role.RoleOutputDTO;
import br.com.gabrielferreira.users.mappers.role.input.RoleInputMapper;
import br.com.gabrielferreira.users.mappers.role.output.RoleOutputMapper;
import br.com.gabrielferreira.users.services.RoleService;
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

@Tag(name = "Roles", description = "Role management endpoints")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    private final RoleInputMapper roleInputMapper;

    private final RoleOutputMapper roleOutputMapper;

    @Operation(summary = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role created successfully"
            )
    })
    @PostMapping
    public ResponseEntity<RoleOutputDTO> create(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Valid @RequestBody CreateRoleInputDTO payload
    ) {
        var roleEntity = roleInputMapper.toEntity(payload);
        roleEntity = roleService.save(roleEntity, projectExternalId);

        var roleOutputDto = roleOutputMapper.toOutputDto(roleEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleOutputDto);
    }

    @Operation(summary = "Retrieve a role by external ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role retrieved successfully"
            )
    })
    @GetMapping("/{roleExternalId}")
    public ResponseEntity<RoleOutputDTO> retrieveByExternalId(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the role",
                    example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051",
                    required = true
            )
            @PathVariable UUID roleExternalId
    ) {
        var roleEntity = roleService.getOneRole(roleExternalId, projectExternalId);

        var roleOutputDto = roleOutputMapper.toOutputDto(roleEntity);
        return ResponseEntity.ok(roleOutputDto);
    }

    @Operation(summary = "Update an existing role")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role updated successfully"
            )
    })
    @PutMapping("/{roleExternalId}")
    public ResponseEntity<RoleOutputDTO> update(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the role",
                    example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051",
                    required = true
            )
            @PathVariable UUID roleExternalId,
            @Valid @RequestBody UpdateRoleInputDTO payload
    ) {
        var roleEntity = roleInputMapper.toEntity(payload);
        roleEntity = roleService.update(
                roleExternalId,
                roleEntity,
                projectExternalId
        );

        var roleOutputDto = roleOutputMapper.toOutputDto(roleEntity);
        return ResponseEntity.ok(roleOutputDto);
    }

    @Operation(summary = "List all roles for a project")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles listed successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<RoleOutputDTO>> listAll(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId
    ) {
        var roleEntities = roleService.getAllRoles(projectExternalId);

        var roleOutputDto = roleOutputMapper.toCollectionDto(roleEntities);
        return ResponseEntity.ok(roleOutputDto);
    }

    @Operation(summary = "Delete a role by external ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Role deleted successfully"
            )
    })
    @DeleteMapping("/{roleExternalId}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the role",
                    example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051",
                    required = true
            )
            @PathVariable UUID roleExternalId) {
        roleService.delete(roleExternalId, projectExternalId);
        return ResponseEntity.noContent().build();
    }
}
