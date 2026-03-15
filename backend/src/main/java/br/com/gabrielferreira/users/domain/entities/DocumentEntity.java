package br.com.gabrielferreira.users.domain.entities;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
