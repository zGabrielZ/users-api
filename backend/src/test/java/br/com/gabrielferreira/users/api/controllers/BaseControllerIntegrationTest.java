package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.domain.entities.AddressEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyUserEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyUserId;
import br.com.gabrielferreira.users.domain.entities.ContactEntity;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.RelationshipType;
import br.com.gabrielferreira.users.domain.repositories.CompanyRepository;
import br.com.gabrielferreira.users.domain.repositories.CompanyUserRepository;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.repositories.RoleRepository;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseControllerIntegrationTest {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected CompanyUserRepository companyUserRepository;

    public UserEntity createUser(DocumentEntity documentEntity, UUID roleIdExisting, UUID projectIdExisting) {
        UserEntity userEntity = UserEntity.builder()
                .firstName("Integration")
                .lastName("Test")
                .email("integrationtest@email.com")
                .password("$2a$10$oYelCMPRK3v13DgtQhnGXOJjC2ZTa7bUUBfGv2cSxJbZSIb4gknYm")
                .roles(
                        List.of(
                                roleRepository.findOneByRoleExternalIdAndProjectExternalId(
                                        roleIdExisting, projectIdExisting
                                ).orElseThrow()
                        )
                )
                .project(
                        projectRepository.findOneByProjectExternalId(projectIdExisting)
                                .orElseThrow()
                )
                .document(documentEntity)
                .build();
        return userRepository.saveAndFlush(userEntity);
    }

    public ProjectEntity createProject(String name) {
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name(name)
                .build();
        return projectRepository.saveAndFlush(projectEntity);
    }

    public RoleEntity createRole(String description, String authority, ProjectEntity projectEntity) {
        RoleEntity roleEntity = RoleEntity.builder()
                .description(description)
                .authority(authority)
                .project(projectEntity)
                .build();
        return roleRepository.saveAndFlush(roleEntity);
    }

    public CompanyEntity createCompany(DocumentEntity documentEntity, UUID projectIdExisting) {
        CompanyEntity companyEntity = CompanyEntity.builder()
                .name("integration-test")
                .document(documentEntity)
                .foundationDate(LocalDate.of(2010, 10, 10))
                .site("www.site.com.br")
                .email("integration-test@email.com")
                .address(
                        AddressEntity.builder()
                                .postalCode("03342-900")
                                .street("Avenida Regente Feijó")
                                .number("123")
                                .complement("Apto 123")
                                .neighborhood("Vila Regente Feijó")
                                .city("São Paulo")
                                .state("SP")
                                .build()
                )
                .contact(
                        ContactEntity.builder()
                                .name("integration test contact")
                                .ddiPhone("55")
                                .dddPhone("11")
                                .phoneNumber("123456789")
                                .ddiMobile("55")
                                .dddMobile("11")
                                .mobileNumber("987654321")
                                .build()
                )
                .project(
                        projectRepository.findOneByProjectExternalId(projectIdExisting)
                                .orElseThrow()
                )
                .build();
        return companyRepository.saveAndFlush(companyEntity);
    }

    public CompanyUserEntity createCompanyUserEntity(CompanyEntity companyEntity, UUID userIdExisting, UUID projectIdExisting) {
        UserEntity userEntity = userRepository.findOneByUserExternalIdAndProjectExternalId(userIdExisting, projectIdExisting)
                .orElseThrow();

        CompanyUserEntity companyUserEntity = CompanyUserEntity.builder()
                .id(
                        CompanyUserId.builder()
                                .companyId(companyEntity.getId())
                                .userId(userEntity.getId())
                                .build()
                )
                .company(companyEntity)
                .user(userEntity)
                .type(RelationshipType.OWNER)
                .build();
        return companyUserRepository.saveAndFlush(companyUserEntity);
    }
}
