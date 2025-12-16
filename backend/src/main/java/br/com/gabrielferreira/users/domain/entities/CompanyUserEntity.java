package br.com.gabrielferreira.users.domain.entities;

import br.com.gabrielferreira.users.domain.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "company, user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_COMPANY_USER")
public class CompanyUserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4066211991464913691L;

    @EmbeddedId
    @EqualsAndHashCode.Include
    private CompanyUserId id;

    @JoinColumn(name = "COMPANY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_USER_COMPANY_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("companyId")
    private CompanyEntity company;

    @JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_USER_USER_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private RelationshipType type;

    @Column(name = "COMPANY_USER_EXTERNAL_ID", nullable = false, unique = true)
    private UUID companyExternalId;

    @PrePersist
    public void generateCompanyExternalId() {
        this.companyExternalId = UUID.randomUUID();
    }
}
