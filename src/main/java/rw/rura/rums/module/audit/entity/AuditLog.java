package rw.rura.rums.module.audit.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.rura.rums.audit.ChangeDto;
import rw.rura.rums.enums.AuditAction;
import rw.rura.rums.enums.AuditModule;
import rw.rura.rums.module.audit.converter.ChangesConverter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "user_name", nullable = false, length = 120)
    private String userName;

    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private AuditModule module;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "entity_label", nullable = false, length = 400)
    private String entityLabel;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Convert(converter = ChangesConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, ChangeDto> changes;

    @Column(nullable = false)
    private Instant timestamp;
}
