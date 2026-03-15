package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"project", "document", "address", "contact"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_COMPANY", uniqueConstraints = {
        @UniqueConstraint(name = "UK_COMPANY_EMAIL_PROJECT", columnNames = {"EMAIL", "PROJECT_ID"}),
        @UniqueConstraint(name = "UK_COMPANY_DOCUMENT_PROJECT", columnNames = {"DOCUMENT_ID", "PROJECT_ID"})
})
public class CompanyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1982499274784862902L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @JoinColumn(name = "DOCUMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_DOCUMENT_ID"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentEntity document;

    @Column(name = "FOUNDATION_DATE", nullable = false)
    private LocalDate foundationDate;

    @Column(name = "SITE")
    private String site;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @JoinColumn(name = "ADDRESS_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_ADDRESS_ID"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private AddressEntity address;

    @JoinColumn(name = "CONTACT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_CONTACT_ID"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ContactEntity contact;

    @JoinColumn(name = "PROJECT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_COMPANY_PROJECT_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectEntity project;

    @Column(name = "COMPANY_EXTERNAL_ID", nullable = false, unique = true)
    private UUID companyExternalId;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<CompanyUserEntity> companyUsers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false, columnDefinition = "datetime")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "datetime")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void generateCompanyExternalId() {
        this.companyExternalId = UUID.randomUUID();
    }
}
