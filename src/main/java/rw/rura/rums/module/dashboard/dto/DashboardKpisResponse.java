package rw.rura.rums.module.dashboard.dto;

public record DashboardKpisResponse(
        long activeLicenses,
        long activeComplaints,
        long complianceRate,
        long openFraudCases
) {}
