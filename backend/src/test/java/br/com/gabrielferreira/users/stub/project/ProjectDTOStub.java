package br.com.gabrielferreira.users.stub.project;

import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.UpdateProjectInputDTO;

public class ProjectDTOStub {

    private ProjectDTOStub() {}

    public static CreateProjectInputDTO createCreateProjectInputDTO(String name) {
        return CreateProjectInputDTO.builder()
                .name(name)
                .build();
    }

    public static UpdateProjectInputDTO updateProjectInputDTO(String name) {
        return UpdateProjectInputDTO.builder()
                .name(name)
                .build();
    }
}
