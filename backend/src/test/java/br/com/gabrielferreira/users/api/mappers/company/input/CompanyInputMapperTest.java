package br.com.gabrielferreira.users.api.mappers.company.input;

import br.com.gabrielferreira.users.api.dtos.input.address.AddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.address.UpdateAddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.company.CreateCompanyInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.company.UpdateCompanyInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.ContactInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.UpdateContactInputDTO;
import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.AddressEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.ContactEntity;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.utils.GenerateCNPJUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Tests for CompanyInputMapper")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyInputMapperTest {

    private final CompanyInputMapper mapper = CompanyInputMapper.INSTANCE;

    @Test
    @Order(1)
    void givenCreateCompanyInputDTOWhenToEntityThenReturnCompanyEntity() {
        CreateCompanyInputDTO createCompanyInputDTO = CreateCompanyInputDTO.builder()
                .name("Acme Corporation")
                .documentNumber(GenerateCNPJUtils.generateCNPJ())
                .foundationDate(LocalDate.of(2020, 5, 20))
                .site("www.site.com.br")
                .email("test@email.com")
                .address(
                        AddressInputDTO.builder()
                                .postalCode("12345-678")
                                .street("Street Name")
                                .number("123")
                                .complement("Apt 456")
                                .neighborhood("Neighborhood")
                                .city("City")
                                .state("State")
                                .build()
                )
                .contact(
                        ContactInputDTO.builder()
                                .name("Contact Name")
                                .ddiPhone("+55")
                                .dddPhone("11")
                                .phoneNumber("12345678")
                                .ddiMobile("+55")
                                .dddMobile("11")
                                .mobileNumber("987654321")
                                .build()
                )
                .build();
        CompanyEntity result = mapper.toEntity(createCompanyInputDTO);
        assertNotNull(result);
        assertEquals(createCompanyInputDTO.name(), result.getName());
        assertEquals(createCompanyInputDTO.foundationDate(), result.getFoundationDate());
        assertEquals(createCompanyInputDTO.site(), result.getSite());
        assertEquals(createCompanyInputDTO.email(), result.getEmail());
        assertAddress(createCompanyInputDTO.address(), result.getAddress());
        assertContact(createCompanyInputDTO.contact(), result.getContact());
        assertDocument(createCompanyInputDTO.documentNumber(), result.getDocument());
    }

    @Test
    @Order(2)
    void givenCreateCompanyInputDTOWithNullWhenToDocumentEntityThenReturnDocumentEntity() {
        DocumentEntity result = mapper.toDocumentEntity(null);
        assertNull(result);
    }

    @Test
    @Order(3)
    void givenCreateCompanyInputDTOWhenToDocumentEntityThenReturnDocumentEntity() {
        CreateCompanyInputDTO createCompanyInputDTO = CreateCompanyInputDTO.builder()
                .documentNumber(GenerateCNPJUtils.generateCNPJ())
                .build();

        DocumentEntity result = mapper.toDocumentEntity(createCompanyInputDTO);
        assertNotNull(result);
        assertEquals(createCompanyInputDTO.getType(), result.getType());
        assertEquals(createCompanyInputDTO.documentNumber(), Mask.documentWithoutMask(
                result.getType(),
                result.getNumber()
        ));
    }

    @Test
    @Order(4)
    void givenAddressInputDTOWhenToAddressEntityThenReturnAddressEntity() {
        AddressInputDTO addressInputDTO = AddressInputDTO.builder()
                .postalCode("12345-678")
                .street("Street Name")
                .number("123")
                .complement("Apt 456")
                .neighborhood("Neighborhood")
                .city("City")
                .state("State")
                .build();

        AddressEntity result = mapper.toAddressEntity(addressInputDTO);
        assertAddress(addressInputDTO, result);
    }

    @Test
    @Order(5)
    void givenContactInputDTOWhenToContactEntityThenReturnContactEntity() {
        ContactInputDTO contactInputDTO = ContactInputDTO.builder()
                .name("Contact Name")
                .ddiPhone("+55")
                .dddPhone("11")
                .phoneNumber("12345678")
                .ddiMobile("+55")
                .dddMobile("11")
                .mobileNumber("987654321")
                .build();

        ContactEntity result = mapper.toContactEntity(contactInputDTO);
        assertContact(contactInputDTO, result);
    }

    @Test
    @Order(6)
    void givenUpdateCompanyInputDTOWhenToEntityThenReturnCompanyEntity() {
        UpdateCompanyInputDTO updateCompanyInputDTO = UpdateCompanyInputDTO.builder()
                .name("Acme Corporation")
                .foundationDate(LocalDate.of(2020, 5, 20))
                .site("www.site.com.br")
                .email("test@email.com")
                .address(
                        UpdateAddressInputDTO.builder()
                                .postalCode("12345-678")
                                .street("Street Name")
                                .number("123")
                                .complement("Apt 456")
                                .neighborhood("Neighborhood")
                                .city("City")
                                .state("State")
                                .build()
                )
                .contact(
                        UpdateContactInputDTO.builder()
                                .name("Contact Name")
                                .ddiPhone("+55")
                                .dddPhone("11")
                                .phoneNumber("12345678")
                                .ddiMobile("+55")
                                .dddMobile("11")
                                .mobileNumber("987654321")
                                .build()
                )
                .build();
        CompanyEntity result = mapper.toEntity(updateCompanyInputDTO);
        assertNotNull(result);
        assertEquals(updateCompanyInputDTO.name(), result.getName());
        assertEquals(updateCompanyInputDTO.foundationDate(), result.getFoundationDate());
        assertEquals(updateCompanyInputDTO.site(), result.getSite());
        assertEquals(updateCompanyInputDTO.email(), result.getEmail());
        assertAddress(updateCompanyInputDTO.address(), result.getAddress());
        assertContact(updateCompanyInputDTO.contact(), result.getContact());
    }

    @Test
    @Order(7)
    void givenUpdateAddressInputDTOWhenToAddressEntityThenReturnAddressEntity() {
        UpdateAddressInputDTO addressInputDTO = UpdateAddressInputDTO.builder()
                .postalCode("12345-678")
                .street("Street Name")
                .number("123")
                .complement("Apt 456")
                .neighborhood("Neighborhood")
                .city("City")
                .state("State")
                .build();

        AddressEntity result = mapper.toUpdateAddressEntity(addressInputDTO);
        assertAddress(addressInputDTO, result);
    }

    @Test
    @Order(8)
    void givenUpdateContactInputDTOWhenToContactEntityThenReturnContactEntity() {
        UpdateContactInputDTO contactInputDTO = UpdateContactInputDTO.builder()
                .name("Contact Name")
                .ddiPhone("+55")
                .dddPhone("11")
                .phoneNumber("12345678")
                .ddiMobile("+55")
                .dddMobile("11")
                .mobileNumber("987654321")
                .build();

        ContactEntity result = mapper.toUpdateContactEntity(contactInputDTO);
        assertContact(contactInputDTO, result);
    }

    private void assertDocument(String documentNumber, DocumentEntity expectedDocument) {
        assertNotNull(expectedDocument);
        assertEquals(DocumentType.CNPJ, expectedDocument.getType());
        assertEquals(documentNumber, Mask.documentWithoutMask(
                expectedDocument.getType(),
                expectedDocument.getNumber()
        ));
    }

    private void assertContact(ContactInputDTO contactInputDTO, ContactEntity expectedContact) {
        assertNotNull(expectedContact);
        assertEquals(contactInputDTO.name(), expectedContact.getName());
        assertEquals(contactInputDTO.ddiPhone(), expectedContact.getDdiPhone());
        assertEquals(contactInputDTO.dddPhone(), expectedContact.getDddPhone());
        assertEquals(contactInputDTO.phoneNumber(), expectedContact.getPhoneNumber());
        assertEquals(contactInputDTO.ddiMobile(), expectedContact.getDdiMobile());
        assertEquals(contactInputDTO.dddMobile(), expectedContact.getDddMobile());
        assertEquals(contactInputDTO.mobileNumber(), expectedContact.getMobileNumber());
    }

    private void assertAddress(AddressInputDTO addressInputDTO, AddressEntity expectedAddress) {
        assertNotNull(expectedAddress);
        assertEquals(addressInputDTO.postalCode(),  expectedAddress.getPostalCode());
        assertEquals(addressInputDTO.street(),  expectedAddress.getStreet());
        assertEquals(addressInputDTO.number(),  expectedAddress.getNumber());
        assertEquals(addressInputDTO.complement(),  expectedAddress.getComplement());
        assertEquals(addressInputDTO.neighborhood(),  expectedAddress.getNeighborhood());
        assertEquals(addressInputDTO.city(),  expectedAddress.getCity());
        assertEquals(addressInputDTO.state(),  expectedAddress.getState());
    }

    private void assertAddress(UpdateAddressInputDTO addressInputDTO, AddressEntity expectedAddress) {
        assertNotNull(expectedAddress);
        assertEquals(addressInputDTO.postalCode(),  expectedAddress.getPostalCode());
        assertEquals(addressInputDTO.street(),  expectedAddress.getStreet());
        assertEquals(addressInputDTO.number(),  expectedAddress.getNumber());
        assertEquals(addressInputDTO.complement(),  expectedAddress.getComplement());
        assertEquals(addressInputDTO.neighborhood(),  expectedAddress.getNeighborhood());
        assertEquals(addressInputDTO.city(),  expectedAddress.getCity());
        assertEquals(addressInputDTO.state(),  expectedAddress.getState());
    }

    private void assertContact(UpdateContactInputDTO contactInputDTO, ContactEntity expectedContact) {
        assertNotNull(expectedContact);
        assertEquals(contactInputDTO.name(), expectedContact.getName());
        assertEquals(contactInputDTO.ddiPhone(), expectedContact.getDdiPhone());
        assertEquals(contactInputDTO.dddPhone(), expectedContact.getDddPhone());
        assertEquals(contactInputDTO.phoneNumber(), expectedContact.getPhoneNumber());
        assertEquals(contactInputDTO.ddiMobile(), expectedContact.getDdiMobile());
        assertEquals(contactInputDTO.dddMobile(), expectedContact.getDddMobile());
        assertEquals(contactInputDTO.mobileNumber(), expectedContact.getMobileNumber());
    }

}