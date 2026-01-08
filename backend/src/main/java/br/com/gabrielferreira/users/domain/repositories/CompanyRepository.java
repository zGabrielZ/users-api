package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.CompanyEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.repositories.projection.company.SummaryCompanyDocumentProjection;
import br.com.gabrielferreira.users.domain.repositories.projection.company.SummaryCompanyEmailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    @Query(
            "SELECT c.companyExternalId as companyExternalId, c.email as email FROM CompanyEntity c " +
                    "JOIN c.project p " +
                    "WHERE c.email = :email " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<SummaryCompanyEmailProjection> findByEmailAndProjectExternalId(@Param("email") String email, @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT c.companyExternalId as companyExternalId, d.documentExternalId as documentExternalId, d.number as documentNumber, d.type as documentType FROM CompanyEntity c " +
                    "JOIN c.document d " +
                    "JOIN c.project p " +
                    "WHERE d.type = :type " +
                    "AND d.number = :number " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<SummaryCompanyDocumentProjection> findByDocumentAndProjectExternalId(@Param("type") DocumentType type, @Param("number") String number,
                                                                                                                                                      @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT c FROM CompanyEntity c " +
                    "JOIN FETCH c.document d " +
                    "JOIN FETCH c.project p " +
                    "JOIN FETCH c.address a " +
                    "JOIN FETCH c.contact ct " +
                    "WHERE c.companyExternalId = :companyExternalId " +
                    "AND c.project.projectExternalId = :projectExternalId"
    )
    Optional<CompanyEntity> findOneCompanyExternalIdAndProjectExternalId(@Param("companyExternalId") UUID companyExternalId, @Param("projectExternalId") UUID projectExternalId);
}

