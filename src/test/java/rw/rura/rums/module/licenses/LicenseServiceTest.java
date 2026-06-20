package rw.rura.rums.module.licenses;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import rw.rura.rums.alert.AlertService;
import rw.rura.rums.audit.AuditService;
import rw.rura.rums.enums.*;
import rw.rura.rums.exception.InvalidStatusTransitionException;
import rw.rura.rums.exception.ResourceNotFoundException;
import rw.rura.rums.module.licenses.dto.LicenseCreateRequest;
import rw.rura.rums.module.licenses.dto.LicenseResponse;
import rw.rura.rums.module.licenses.dto.LicenseStatusUpdateRequest;
import rw.rura.rums.module.licenses.entity.License;
import rw.rura.rums.module.licenses.repository.LicenseRepository;
import rw.rura.rums.module.licenses.service.LicenseService;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private LicenseService licenseService;

    // -------------------------------------------------------------------------
    // getAll — returns paginated results
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void getAll_returnsPaginatedResults() {
        License license = buildLicense(LicenseStatus.ACTIVE);
        Page<License> page = new PageImpl<>(List.of(license), PageRequest.of(0, 10), 1);

        when(licenseRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<LicenseResponse> result = licenseService.getAll(null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).licenseNumber()).isEqualTo("RURA-TLC-2026-TEST");
    }

    // -------------------------------------------------------------------------
    // getById — throws ResourceNotFoundException for unknown id
    // -------------------------------------------------------------------------

    @Test
    void getById_withUnknownId_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(licenseRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> licenseService.getById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }

    // -------------------------------------------------------------------------
    // create — saves and returns response
    // -------------------------------------------------------------------------

    @Test
    void create_withValidRequest_savesAndReturnsResponse() {
        LicenseCreateRequest req = new LicenseCreateRequest(
                "RURA-TLC-2026-TEST",
                "Test Operator Ltd",
                "Jane Doe",
                "jane@testoperator.rw",
                LicenseCategory.INTERNET_SERVICE_PROVIDER,
                Sector.TELECOM,
                Province.KIGALI_CITY,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2031, 1, 1),
                25_000_000L
        );

        when(licenseRepository.existsByLicenseNumber("RURA-TLC-2026-TEST")).thenReturn(false);
        when(licenseRepository.save(any(License.class))).thenAnswer(inv -> {
            License l = inv.getArgument(0);
            l = License.builder()
                    .id(UUID.randomUUID())
                    .licenseNumber(l.getLicenseNumber())
                    .operatorName(l.getOperatorName())
                    .contactPerson(l.getContactPerson())
                    .contactEmail(l.getContactEmail())
                    .category(l.getCategory())
                    .sector(l.getSector())
                    .province(l.getProvince())
                    .status(l.getStatus())
                    .issuedAt(l.getIssuedAt())
                    .expiresAt(l.getExpiresAt())
                    .annualFeeRwf(l.getAnnualFeeRwf())
                    .build();
            return l;
        });

        UserEntity actor = buildUser(UserRole.ADMIN);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        LicenseResponse response = licenseService.create(req, actor, httpRequest);

        assertThat(response.licenseNumber()).isEqualTo("RURA-TLC-2026-TEST");
        assertThat(response.operatorName()).isEqualTo("Test Operator Ltd");
        assertThat(response.status()).isEqualTo(LicenseStatus.PENDING);
        verify(auditService).log(eq(actor), eq(AuditAction.CREATE), eq(AuditModule.LICENSES),
                any(), any(), eq(httpRequest), isNull());
    }

    // -------------------------------------------------------------------------
    // updateStatus — throws InvalidStatusTransitionException for revoked license
    // -------------------------------------------------------------------------

    @Test
    void updateStatus_onRevokedLicense_throwsInvalidStatusTransitionException() {
        License revokedLicense = buildLicense(LicenseStatus.REVOKED);
        when(licenseRepository.findById(revokedLicense.getId())).thenReturn(Optional.of(revokedLicense));

        LicenseStatusUpdateRequest req = new LicenseStatusUpdateRequest(LicenseStatus.ACTIVE);
        UserEntity admin = buildUser(UserRole.ADMIN);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        assertThatThrownBy(() -> licenseService.updateStatus(revokedLicense.getId(), req, admin, httpRequest))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("revoked");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private License buildLicense(LicenseStatus status) {
        return License.builder()
                .id(UUID.randomUUID())
                .licenseNumber("RURA-TLC-2026-TEST")
                .operatorName("Test Operator Ltd")
                .contactPerson("Jane Doe")
                .contactEmail("jane@testoperator.rw")
                .category(LicenseCategory.INTERNET_SERVICE_PROVIDER)
                .sector(Sector.TELECOM)
                .province(Province.KIGALI_CITY)
                .status(status)
                .issuedAt(LocalDate.of(2026, 1, 1))
                .expiresAt(LocalDate.of(2031, 1, 1))
                .annualFeeRwf(25_000_000L)
                .build();
    }

    private UserEntity buildUser(UserRole role) {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@rura.rw")
                .passwordHash("$2a$12$placeholder")
                .role(role)
                .status(UserStatus.ACTIVE)
                .department(UserDepartment.ICT)
                .mfaEnabled(false)
                .build();
    }
}
