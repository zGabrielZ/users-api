package br.com.gabrielferreira.users.api.mappers.company.input;

import br.com.gabrielferreira.users.api.dtos.input.company.CreateCompanyInputDTO;
import br.com.gabrielferreira.users.core.utils.Constants;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
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
    @Mapping(target = "address", source = "address")
    @Mapping(target = "contact", source = "contact")
    CompanyEntity toEntity(CreateCompanyInputDTO createCompanyInputDTO);

    @Named("trimIfNotBlank")
    default String trimIfNotBlank(String value) {
        return Constants.trimIfNotBlank(value);
    }

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
}
