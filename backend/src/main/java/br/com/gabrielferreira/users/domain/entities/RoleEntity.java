package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "project")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_ROLE", uniqueConstraints = {
        @UniqueConstraint(name = "UK_AUTHORITY_PROJECT", columnNames = {"AUTHORITY", "PROJECT_ID"})
})
public class RoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -3072516514224229925L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "AUTHORITY", nullable = false)
    private String authority;

    @Column(name = "ROLE_EXTERNAL_ID", nullable = false, unique = true)
    private UUID roleExternalId;

    @JoinColumn(name = "PROJECT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ROLE_PROJECT_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectEntity project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TB_USER_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", table = "TB_ROLE"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID", table = "TB_USER"))
    private List<UserEntity> users = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false, columnDefinition = "datetime")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "datetime")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void generateRoleExternalId() {
        this.roleExternalId = UUID.randomUUID();
    }
}
