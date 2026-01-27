package br.com.gabrielferreira.users.domain.mappers;

import br.com.gabrielferreira.users.domain.entities.AddressEntity;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.ContactEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "document", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "companyExternalId", ignore = true)
    @Mapping(target = "companyUsers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address", qualifiedByName = "updateAddressEntity")
    @Mapping(target = "contact", qualifiedByName = "updateContactEntity")
    @Mapping(target = "name", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "site", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "email", conditionQualifiedByName = "isNotBlank")
    void updateCompanyEntity(CompanyEntity companyEntity, @MappingTarget CompanyEntity companyEntityTarget);

    @Named("updateAddressEntity")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postalCode", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "street", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "number", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "complement", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "neighborhood", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "city", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "state", conditionQualifiedByName = "isNotBlank")
    void updateAddressEntity(AddressEntity andressEntity, @MappingTarget AddressEntity addressEntityTarget);

    @Named("updateContactEntity")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "ddiPhone", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "dddPhone", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "phoneNumber", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "ddiMobile", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "dddMobile", conditionQualifiedByName = "isNotBlank")
    @Mapping(target = "mobileNumber", conditionQualifiedByName = "isNotBlank")
    void updateContactEntity(ContactEntity contactEntity, @MappingTarget ContactEntity contactEntityTarget);

    @Named("isNotBlank")
    @Condition
    default boolean isNotBlankCondition(String value) {
        return value != null && !value.isBlank();
    }
}
