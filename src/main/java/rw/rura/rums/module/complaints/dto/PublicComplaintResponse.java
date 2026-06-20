package rw.rura.rums.module.complaints.dto;

import java.util.UUID;

public record PublicComplaintResponse(UUID id, String referenceNumber, String message) {}
