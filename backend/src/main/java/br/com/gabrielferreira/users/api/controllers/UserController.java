package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.user.CreateUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateEmailUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdatePasswordUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.output.user.UserOutputDTO;
import br.com.gabrielferreira.users.api.mappers.user.input.UserInputMapper;
import br.com.gabrielferreira.users.api.mappers.user.output.UserOutputMapper;
import br.com.gabrielferreira.users.domain.services.UserService;
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

import java.util.UUID;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserInputMapper userInputMapper;

    private final UserOutputMapper userOutputMapper;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully"
            )
    })
    @PostMapping
    public ResponseEntity<UserOutputDTO> create(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader("projectExternalId") UUID projectExternalId,
            @Valid @RequestBody CreateUserInputDTO payload
    ) {
        var userEntity = userInputMapper.toEntity(payload);
        userEntity = userService.save(userEntity, projectExternalId);

        var userOutputDto = userOutputMapper.toOutputDto(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userOutputDto);
    }

    @Operation(summary = "Retrieve a user by external ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            )
    })
    @GetMapping("/{userExternalId}")
    public ResponseEntity<UserOutputDTO> retrieveByExternalId(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId
    ) {
        var userEntity = userService.getOneUser(userExternalId, projectExternalId);

        var userOutputDto = userOutputMapper.toOutputDto(userEntity);
        return ResponseEntity.ok(userOutputDto);
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            )
    })
    @PutMapping("/{userExternalId}")
    public ResponseEntity<UserOutputDTO> update(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId,
            @Valid @RequestBody UpdateUserInputDTO payload
    ) {
        var userEntity = userInputMapper.toEntity(payload);
        userEntity = userService.update(userExternalId, userEntity, projectExternalId);

        var userOutputDto = userOutputMapper.toOutputDto(userEntity);
        return ResponseEntity.ok(userOutputDto);
    }

    @Operation(summary = "Update user's email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User email updated successfully"
            )
    })
    @PutMapping("/{userExternalId}/email")
    public ResponseEntity<UserOutputDTO> updateEmail(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId,
            @Valid @RequestBody UpdateEmailUserInputDTO payload
    ) {
        var userEntity = userService.updateEmail(userExternalId, payload.email(), projectExternalId);

        var userOutputDto = userOutputMapper.toOutputDto(userEntity);
        return ResponseEntity.ok(userOutputDto);
    }

    @Operation(summary = "Update user's password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User password updated successfully"
            )
    })
    @PutMapping("/{userExternalId}/password")
    public ResponseEntity<UserOutputDTO> updatePassword(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader ("projectExternalId") UUID projectExternalId,
            @Parameter(
                    description = "External ID of the user",
                    example = "aca0597b-c4f8-40c8-9b59-1dc86a4f401c",
                    required = true
            )
            @PathVariable UUID userExternalId,
            @Valid @RequestBody UpdatePasswordUserInputDTO payload
    ) {
        var userEntity = userService.updatePassword(
                userExternalId,
                payload.oldPassword(),
                payload.newPassword(),
                projectExternalId
        );

        var userOutputDto = userOutputMapper.toOutputDto(userEntity);
        return ResponseEntity.ok(userOutputDto);
    }
}
