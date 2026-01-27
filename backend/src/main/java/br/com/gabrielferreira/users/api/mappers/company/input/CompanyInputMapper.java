package br.com.gabrielferreira.users.api.mappers.company.input;

import br.com.gabrielferreira.users.api.dtos.input.address.AddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.address.UpdateAddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.company.CreateCompanyInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.company.UpdateCompanyInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.ContactInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.UpdateContactInputDTO;
import br.com.gabrielferreira.users.core.utils.Constants;
import br.com.gabrielferreira.users.domain.entities.AddressEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.ContactEntity;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CompanyInputMapper {

    @Mapping(source = "name", target = "name", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "document", expression = "java(toDocumentEntity(createCompanyInputDTO))")
    @Mapping(target = "foundationDate", source = "foundationDate")
    @Mapping(target = "site", source = "site", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "address", expression = "java(toAddressEntity(createCompanyInputDTO.address()))")
    @Mapping(target = "contact", expression = "java(toContactEntity(createCompanyInputDTO.contact()))")
    CompanyEntity toEntity(CreateCompanyInputDTO createCompanyInputDTO);

    @Mapping(source = "name", target = "name", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "foundationDate", source = "foundationDate")
    @Mapping(target = "site", source = "site", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "address", expression = "java(toUpdateAddressEntity(updateCompanyInputDTO.address()))")
    @Mapping(target = "contact", expression = "java(toUpdateContactEntity(updateCompanyInputDTO.contact()))")
    CompanyEntity toEntity(UpdateCompanyInputDTO updateCompanyInputDTO);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }

    @Named("toAddressEntity")
    @Mapping(target = "street", source = "street", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "number", source = "number", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "complement", source = "complement", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "neighborhood", source = "neighborhood", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "city", source = "city", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "state", source = "state", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "postalCode", source = "postalCode", qualifiedByName = "trimIfNotBlank")
    AddressEntity toAddressEntity(AddressInputDTO addressInputDTO);

    @Named("toContactEntity")
    @Mapping(target = "name", source = "name", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "ddiPhone", source = "ddiPhone", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "dddPhone", source = "dddPhone", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "ddiMobile", source = "ddiMobile", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "dddMobile", source = "dddMobile", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "mobileNumber", source = "mobileNumber", qualifiedByName = "trimIfNotBlank")
    ContactEntity toContactEntity(ContactInputDTO contactInputDTO);

    @Named("toDocumentEntity")
    default DocumentEntity toDocumentEntity(CreateCompanyInputDTO createCompanyInputDTO) {
        if(createCompanyInputDTO == null) {
            return null;
        }
        return DocumentEntity.builder()
                .number(trimIfNotBlank(createCompanyInputDTO.documentNumber()))
                .type(DocumentType.CNPJ)
                .build();
    }

    @Named("toUpdateAddressEntity")
    @Mapping(target = "street", source = "street", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "number", source = "number", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "complement", source = "complement", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "neighborhood", source = "neighborhood", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "city", source = "city", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "state", source = "state", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "postalCode", source = "postalCode", qualifiedByName = "trimIfNotBlank")
    AddressEntity toUpdateAddressEntity(UpdateAddressInputDTO updateAddressInputDTO);

    @Named("toUpdateContactEntity")
    @Mapping(target = "name", source = "name", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "ddiPhone", source = "ddiPhone", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "dddPhone", source = "dddPhone", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "ddiMobile", source = "ddiMobile", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "dddMobile", source = "dddMobile", qualifiedByName = "trimIfNotBlank")
    @Mapping(target = "mobileNumber", source = "mobileNumber", qualifiedByName = "trimIfNotBlank")
    ContactEntity toUpdateContactEntity(UpdateContactInputDTO updateContactInputDTO);
}
