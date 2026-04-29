package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
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
}
