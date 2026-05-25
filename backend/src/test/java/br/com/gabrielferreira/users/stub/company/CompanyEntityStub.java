package br.com.gabrielferreira.users.stub.company;

import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.RelationshipType;
import br.com.gabrielferreira.users.stub.address.AddressEntityStub;
import br.com.gabrielferreira.users.stub.companyuser.CompanyUserEntityStub;
import br.com.gabrielferreira.users.stub.contact.ContactEntityStub;
import br.com.gabrielferreira.users.stub.document.DocumentEntityStub;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CompanyEntityStub {

    private static final UUID FIXED_UUID = UUID.fromString("349f1ddc-45b1-424a-a860-f33f6cb04a2b");
    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");

    private CompanyEntityStub() {}

    public static CompanyEntity createCompanyEntity(UserEntity userEntity) {
        CompanyEntity companyEntity = CompanyEntity.builder()
                .id(1L)
                .name("Company Name")
                .document(DocumentEntityStub.documentCnpjEntityCreated())
                .foundationDate(LocalDate.of(2020, 5, 20))
                .site("www.site.com.br")
                .email("company@email.com")
                .address(AddressEntityStub.createAddressEntity())
                .contact(ContactEntityStub.createContactEntity())
                .companyExternalId(FIXED_UUID)
                .createdAt(FIXED_DATE)
                .build();

        companyEntity.setCompanyUsers(CompanyUserEntityStub.createCompanyUserEntityList(
                companyEntity,
                userEntity,
                RelationshipType.PARTNER
        ));

        return companyEntity;
    }
}
