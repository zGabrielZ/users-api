package br.com.gabrielferreira.users.api.mappers.company.output;

import br.com.gabrielferreira.users.api.dtos.output.company.CompanyOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.document.DocumentOutputDTO;
import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyOutputMapper {

    @Mapping(target = "companyExternalId", source = "companyExternalId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "foundationDate", source = "foundationDate")
    @Mapping(target = "site", source = "site")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "contact", source = "contact")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "document", expression = "java(toDocumentOutputDTO(companyEntity.getDocument()))")
    CompanyOutputDTO toOutputDto(CompanyEntity companyEntity);

    default DocumentOutputDTO toDocumentOutputDTO(DocumentEntity documentEntity) {
        if (documentEntity == null) {
            return null;
        }
        return DocumentOutputDTO.builder()
                .documentExternalId(documentEntity.getDocumentExternalId())
                .number(documentEntity.getNumber())
                .type(documentEntity.getType())
                .build();
    }
}
