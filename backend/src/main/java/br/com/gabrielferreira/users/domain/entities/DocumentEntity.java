package br.com.gabrielferreira.users.domain.entities;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
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
@Table(name = "TB_DOCUMENT")
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

    @OneToOne(mappedBy = "document")
    private UserEntity user;

    @Column(name = "DOCUMENT_EXTERNAL_ID", nullable = false, unique = true)
    private UUID documentExternalId;

    @PrePersist
    public void generateUserExternalId() {
        this.documentExternalId = UUID.randomUUID();
    }
}
