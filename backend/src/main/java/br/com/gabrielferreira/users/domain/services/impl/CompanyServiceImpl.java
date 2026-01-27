package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.*;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.CompanyNotFoundException;
import br.com.gabrielferreira.users.domain.mappers.CompanyMapper;
import br.com.gabrielferreira.users.domain.repositories.CompanyRepository;
import br.com.gabrielferreira.users.domain.repositories.projection.company.SummaryCompanyDocumentProjection;
import br.com.gabrielferreira.users.domain.repositories.projection.company.SummaryCompanyEmailProjection;
import br.com.gabrielferreira.users.domain.services.CompanyService;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final ProjectService projectService;

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    @Transactional
    @Override
    public CompanyEntity save(CompanyEntity companyEntity, UUID externalProjectId) {
        ProjectEntity projectFound = projectService.getOneProject(externalProjectId);
        companyEntity.setProject(projectFound);

        validateExistingCompanyWithEmailAndProject(companyEntity.getEmail(), externalProjectId);

        DocumentEntity document = companyEntity.getDocument();
        companyEntity.getDocument().setNumber(Mask.documentWithoutMask(
                document.getType(),
                document.getNumber()
        ));
        validateExistingCompanyWithDocumentAndProject(
                companyEntity.getDocument().getType(),
                companyEntity.getDocument().getNumber(),
                externalProjectId
        );
        return companyRepository.save(companyEntity);
    }

    @Override
    public CompanyEntity getOneCompany(UUID companyExternalId, UUID projectExternalId) {
        return companyRepository.findOneCompanyExternalIdAndProjectExternalId(companyExternalId, projectExternalId)
                .orElseThrow(() -> new CompanyNotFoundException(companyExternalId, projectExternalId));
    }

    @Transactional
    @Override
    public CompanyEntity update(CompanyEntity companyEntity, UUID companyExternalId, UUID projectExternalId) {
        CompanyEntity companyFound = getOneCompany(companyExternalId, projectExternalId);
        if (StringUtils.isNotBlank(companyEntity.getEmail())) {
            Optional<SummaryCompanyEmailProjection> existingCompanyWithEmailAndProject = companyRepository.findByEmailAndProjectExternalId(
                    companyEntity.getEmail(),
                    projectExternalId
            );

            if (existingCompanyWithEmailAndProject.isPresent()
                    && !Objects.equals(companyFound.getCompanyExternalId(),  existingCompanyWithEmailAndProject.get().getCompanyExternalId())) {
                throw new BusinessRuleException("Already exists a company with email in the project");
            }
        }

        companyMapper.updateCompanyEntity(companyEntity, companyFound);
        return companyRepository.save(companyFound);
    }

    private void validateExistingCompanyWithDocumentAndProject(DocumentType type, String number, UUID externalProjectId) {
        Optional<SummaryCompanyDocumentProjection> companyFound = companyRepository.findByDocumentAndProjectExternalId(
                type, number, externalProjectId
        );
        if (companyFound.isPresent()) {
            throw new BusinessRuleException("Already exists a company with document in the project");
        }
    }

    private void validateExistingCompanyWithEmailAndProject(String email, UUID externalProjectId) {
        Optional<SummaryCompanyEmailProjection> companyFound = companyRepository.findByEmailAndProjectExternalId(email, externalProjectId);
        if (companyFound.isPresent()) {
            throw new BusinessRuleException("Already exists a company with email in the project");
        }
    }
}
