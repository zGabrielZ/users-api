package br.com.gabrielferreira.users.api.mappers.company.output;

import br.com.gabrielferreira.users.api.dtos.output.address.AddressOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.company.CompanyOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.contact.ContactOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.document.DocumentOutputDTO;
import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.stub.company.CompanyEntityStub;
import br.com.gabrielferreira.users.stub.document.DocumentEntityStub;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Tests for CompanyOutputMapper")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyOutputMapperTest {

    private final CompanyOutputMapper mapper = CompanyOutputMapper.INSTANCE;

    private CompanyEntity companyEntity;

    @BeforeEach
    void setUp() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity(null, null);
        UserEntity userEntity = UserEntityStub.userEntityCreated(
                projectEntity,
                "encodedPassword",
                DocumentEntityStub.documentCpfEntityCreated()
        );
        companyEntity = CompanyEntityStub.createCompanyEntity(userEntity);
    }

    @Test
    @Order(1)
    void givenCompanyEntityWhenToOutputDtoThenReturnCompanyOutputDTO() {
        CompanyOutputDTO result = mapper.toOutputDto(companyEntity);
        assertNotNull(result);
        assertEquals(companyEntity.getCompanyExternalId(), result.companyExternalId());
        assertEquals(companyEntity.getName(), result.name());
        assertEquals(companyEntity.getFoundationDate(), result.foundationDate());
        assertEquals(companyEntity.getSite(), result.site());
        assertEquals(companyEntity.getEmail(), result.email());
        assertEquals(companyEntity.getCreatedAt(), result.createdAt());
        assertAddress(result.address());
        assertContact(result.contact());
        assertDocument(result.document());
    }

    @Test
    @Order(2)
    void givenDocumentEntityNullWhenToDocumentDtoThenReturnDocumentOutputDTO() {
        DocumentOutputDTO result = mapper.toDocumentOutputDTO(null);
        assertNull(result);
    }

    @Test
    @Order(3)
    void givenDocumentEntityWhenToDocumentDtoThenReturnDocumentOutputDTO() {
        DocumentEntity documentEntity = DocumentEntityStub.documentCnpjEntityCreated();

        DocumentOutputDTO result = mapper.toDocumentOutputDTO(documentEntity);
        assertNotNull(result);
        assertEquals(documentEntity.getDocumentExternalId(), result.documentExternalId());
        assertEquals(documentEntity.getType(), result.type());
        assertEquals(documentEntity.getNumber(), Mask.documentWithoutMask(
                result.type(),
                result.number()
        ));
    }

    private void assertAddress(AddressOutputDTO expectedAddress) {
        assertNotNull(expectedAddress);
        assertEquals(companyEntity.getAddress().getPostalCode(),  expectedAddress.postalCode());
        assertEquals(companyEntity.getAddress().getStreet(),  expectedAddress.street());
        assertEquals(companyEntity.getAddress().getNumber(),  expectedAddress.number());
        assertEquals(companyEntity.getAddress().getComplement(),  expectedAddress.complement());
        assertEquals(companyEntity.getAddress().getNeighborhood(),  expectedAddress.neighborhood());
        assertEquals(companyEntity.getAddress().getCity(),  expectedAddress.city());
        assertEquals(companyEntity.getAddress().getState(),  expectedAddress.state());
    }

    private void assertContact(ContactOutputDTO expectedContact) {
        assertNotNull(expectedContact);
        assertEquals(companyEntity.getContact().getName(), expectedContact.name());
        assertEquals(companyEntity.getContact().getDdiPhone(), expectedContact.ddiPhone());
        assertEquals(companyEntity.getContact().getDddPhone(), expectedContact.dddPhone());
        assertEquals(companyEntity.getContact().getPhoneNumber(), expectedContact.phoneNumber());
        assertEquals(companyEntity.getContact().getDdiMobile(), expectedContact.ddiMobile());
        assertEquals(companyEntity.getContact().getDddMobile(), expectedContact.dddMobile());
        assertEquals(companyEntity.getContact().getMobileNumber(), expectedContact.mobileNumber());
    }

    private void assertDocument(DocumentOutputDTO expectedDocument) {
        assertNotNull(expectedDocument);
        assertEquals(companyEntity.getDocument().getDocumentExternalId(), expectedDocument.documentExternalId());
        assertEquals(companyEntity.getDocument().getType(), expectedDocument.type());
        assertEquals(companyEntity.getDocument().getNumber(), Mask.documentWithoutMask(
                expectedDocument.type(),
                expectedDocument.number()
        ));
    }
}