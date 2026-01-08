package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.company.CreateCompanyInputDTO;
import br.com.gabrielferreira.users.api.dtos.output.company.CompanyOutputDTO;
import br.com.gabrielferreira.users.api.mappers.company.input.CompanyInputMapper;
import br.com.gabrielferreira.users.api.mappers.company.output.CompanyOutputMapper;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.services.CompanyService;
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

@Tag(name = "Companies", description = "Company management endpoints")
@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CompanyInputMapper companyInputMapper;

    private final CompanyOutputMapper companyOutputMapper;

    @Operation(summary = "Create a new company")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Company created successfully"
            )
    })
    @PostMapping
    public ResponseEntity<CompanyOutputDTO> create(
            @Parameter(
                    description = "Project external identifier",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    required = true
            )
            @RequestHeader("projectExternalId") UUID projectExternalId,
            @Valid @RequestBody CreateCompanyInputDTO payload
    ) {
        CompanyEntity companyEntity = companyInputMapper.toEntity(payload);
        companyEntity = companyService.save(companyEntity, projectExternalId);

        CompanyOutputDTO companyOutputDTO = companyOutputMapper.toOutputDto(companyEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyOutputDTO);
    }
}
