package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyUserId implements Serializable {

    @Serial
    private static final long serialVersionUID = 8898411700391207125L;

    @EqualsAndHashCode.Include
    @Column(name = "COMPANY_ID", nullable = false)
    private Long companyId;

    @EqualsAndHashCode.Include
    @Column(name = "USER_ID", nullable = false)
    private Long userId;
}
