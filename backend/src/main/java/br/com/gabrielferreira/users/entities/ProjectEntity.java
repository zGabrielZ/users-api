package br.com.gabrielferreira.users.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
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

    @PrePersist
    public void generateProjectExternalId() {
        this.projectExternalId = UUID.randomUUID();
    }
}
