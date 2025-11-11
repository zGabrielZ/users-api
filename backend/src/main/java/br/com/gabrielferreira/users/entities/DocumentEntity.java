package br.com.gabrielferreira.users.entities;

import br.com.gabrielferreira.users.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_DOCUMENT", uniqueConstraints = {
        @UniqueConstraint(name = "UK_TYPE_NUMBER_USER", columnNames = {"TYPE", "NUMBER", "USER_ID"})
})
public class DocumentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1164622133419277402L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private DocumentType type;

    @Column(name = "NUMBER")
    private String number;

    @JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_DOCUMENT_USER_ID"))
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(name = "DOCUMENT_EXTERNAL_ID", nullable = false, unique = true)
    private UUID documentExternalId;

    @PrePersist
    public void generateUserExternalId() {
        this.documentExternalId = UUID.randomUUID();
    }
}
