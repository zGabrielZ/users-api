package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.domain.services.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Users Roles", description = "User Roles management endpoints")
@RestController
@RequestMapping("/v1/users/{userExternalId}/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "Associate Role to User")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role associated to User successfully"
            )
    })
    @PutMapping("/{roleExternalId}")
    public ResponseEntity<Void> associateRole(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId,
            @Parameter(
                    description = "External ID of the role",
                    example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051",
                    required = true
            )
            @PathVariable UUID roleExternalId
    ) {
        userRoleService.associateRole(userExternalId, roleExternalId, projectExternalId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Disassociate Role from User")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Role disassociated from User successfully"
            )
    })
    @DeleteMapping("/{roleExternalId}")
    public ResponseEntity<Void> disassociateRole(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId,
            @Parameter(
                    description = "External ID of the role",
                    example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051",
                    required = true
            )
            @PathVariable UUID roleExternalId
    ) {
        userRoleService.disassociateRole(userExternalId, roleExternalId, projectExternalId);
        return ResponseEntity.noContent().build();
    }
}
