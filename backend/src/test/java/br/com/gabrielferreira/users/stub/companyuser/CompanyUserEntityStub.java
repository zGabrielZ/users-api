package br.com.gabrielferreira.users.stub.companyuser;

import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyUserEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyUserId;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.RelationshipType;

import java.util.List;
import java.util.UUID;

public class CompanyUserEntityStub {

    private static final UUID FIXED_UUID = UUID.fromString("c7933394-ceca-4a52-b346-6b5e58b17e77");

    private CompanyUserEntityStub() {}

    public static List<CompanyUserEntity> createCompanyUserEntityList(
            CompanyEntity companyEntity,
            UserEntity userEntity,
            RelationshipType relationshipType
    ) {
        return List.of(
                CompanyUserEntity.builder()
                        .id(
                                CompanyUserId.builder()
                                        .userId(userEntity.getId())
                                        .companyId(companyEntity.getId())
                                        .build()
                        )
                        .company(companyEntity)
                        .user(userEntity)
                        .type(relationshipType)
                        .companyExternalId(FIXED_UUID)
                        .build()
        );
    }
}
