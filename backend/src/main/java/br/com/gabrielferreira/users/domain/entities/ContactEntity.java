package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"company"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TB_CONTACT")
public class ContactEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4352772671359697751L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DDI")
    private String ddi;

    @Column(name = "DDD")
    private String ddd;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "MOBILE_NUMBER", nullable = false)
    private String mobileNumber;

    @OneToOne(mappedBy = "contact")
    private CompanyEntity company;
}
