package rw.rura.rums.module.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rw.rura.rums.dto.ApiResponse;
import rw.rura.rums.module.dashboard.service.DashboardService;

@Tag(name = "Dashboard", description = "Aggregated KPIs and summary data for the home screen")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get top-level KPI counts (active licenses, open complaints, compliance rate, open fraud cases)")
    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<?>> getKpis() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getKpis()));
    }

    @Operation(summary = "Get monthly licence issuance / revocation / expiry trend for the last 6 months")
    @GetMapping("/license-trend")
    public ResponseEntity<ApiResponse<?>> getLicenseTrend() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getLicenseTrend()));
    }

    @Operation(summary = "Get complaint counts grouped by sector")
    @GetMapping("/complaints-by-sector")
    public ResponseEntity<ApiResponse<?>> getComplaintsBySector() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getComplaintsBySector()));
    }

    @Operation(summary = "Get compliance status distribution (compliant / under review / non-compliant / remediation)")
    @GetMapping("/compliance-overview")
    public ResponseEntity<ApiResponse<?>> getComplianceOverview() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getComplianceOverview()));
    }

    @Operation(summary = "Get the N most recent audit log entries as an activity feed")
    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<?>> getActivity(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getActivity(limit)));
    }
}
