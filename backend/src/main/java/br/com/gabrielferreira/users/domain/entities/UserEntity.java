package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"project", "document"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_USER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_EMAIL_PROJECT", columnNames = {"EMAIL", "PROJECT_ID"}),
        @UniqueConstraint(name = "UK_DOCUMENT_PROJECT", columnNames = {"DOCUMENT_ID", "PROJECT_ID"})
})
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3382588111235172319L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_EXTERNAL_ID", nullable = false, unique = true)
    private UUID userExternalId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TB_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID", table = "TB_USER"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", table = "TB_ROLE"))
    private List<RoleEntity> roles = new ArrayList<>();

    @JoinColumn(name = "PROJECT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PROJECT_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectEntity project;

    @JoinColumn(name = "DOCUMENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_DOCUMENT_ID"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentEntity document;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CompanyUserEntity> companyUsers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false, columnDefinition = "datetime")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "datetime")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void generateUserExternalId() {
        this.userExternalId = UUID.randomUUID();
    }
}
