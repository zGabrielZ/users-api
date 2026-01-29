package br.com.gabrielferreira.users.stub.project;

import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;

public class ProjectDTOStub {

    private ProjectDTOStub() {}

    public static CreateProjectInputDTO createCreateProjectInputDTO(String name) {
        return CreateProjectInputDTO.builder()
                .name(name)
                .build();
    }
}
