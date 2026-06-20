package rw.rura.rums.module.clms.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.rura.rums.module.users.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clms_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClmsDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClmsCase clmsCase;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(name = "stored_path", nullable = false, length = 500)
    private String storedPath;

    @Column(name = "size_kb", nullable = false)
    private int sizeKb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private UserEntity uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
}
