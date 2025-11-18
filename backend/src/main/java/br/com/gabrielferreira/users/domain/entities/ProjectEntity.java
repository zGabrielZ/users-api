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
@ToString(exclude = "roles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_PROJECT")
public class ProjectEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5818954769587702218L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "PROJECT_EXTERNAL_ID", nullable = false, unique = true)
    private UUID projectExternalId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private List<RoleEntity> roles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false, columnDefinition = "datetime")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "datetime")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void generateProjectExternalId() {
        this.projectExternalId = UUID.randomUUID();
    }
}
