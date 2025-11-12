package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
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

    @PrePersist
    public void generateRoleExternalId() {
        this.roleExternalId = UUID.randomUUID();
    }
}
