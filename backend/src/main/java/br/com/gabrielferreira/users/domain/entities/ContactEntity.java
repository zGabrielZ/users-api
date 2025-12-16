package br.com.gabrielferreira.users.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
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

    @Column(name = "DDI_PHONE")
    private String ddiPhone;

    @Column(name = "DDD_PHONE")
    private String dddPhone;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "DDI_MOBILE")
    private String ddiMobile;

    @Column(name = "DDD_MOBILE")
    private String dddMobile;

    @Column(name = "MOBILE_NUMBER", nullable = false)
    private String mobileNumber;
}
