package rw.rura.rums.module.licenses.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rw.rura.rums.enums.LicenseCategory;
import rw.rura.rums.enums.LicenseStatus;
import rw.rura.rums.enums.Province;
import rw.rura.rums.enums.Sector;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "licenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "operator_name", nullable = false, length = 200)
    private String operatorName;

    @Column(name = "contact_person", length = 120)
    private String contactPerson;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Column(nullable = false)
    private LicenseCategory category;

    @Column(nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private LicenseStatus status;

    @Column(nullable = false)
    private Province province;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "annual_fee_rwf", nullable = false)
    private long annualFeeRwf;

    @Column(name = "last_renewal_at")
    private LocalDate lastRenewalAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
