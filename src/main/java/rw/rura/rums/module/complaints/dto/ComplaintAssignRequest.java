package rw.rura.rums.module.complaints.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ComplaintAssignRequest(@NotNull UUID userId) {}
