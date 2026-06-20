CREATE INDEX idx_licenses_status  ON licenses(status);
CREATE INDEX idx_licenses_sector  ON licenses(sector);
CREATE INDEX idx_licenses_expires ON licenses(expires_at);

CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_sector ON complaints(sector);
CREATE INDEX idx_complaints_filed  ON complaints(filed_at DESC);

CREATE INDEX idx_compliance_status ON compliance_records(status);

CREATE INDEX idx_fraud_status ON fraud_cases(status);
CREATE INDEX idx_fraud_risk   ON fraud_cases(risk_level);

CREATE INDEX idx_workflows_status ON workflows(status);

CREATE INDEX idx_alerts_status   ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);

CREATE INDEX idx_audit_user      ON audit_log(user_id);
CREATE INDEX idx_audit_module    ON audit_log(module);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp DESC);

CREATE INDEX idx_clms_status ON clms_cases(status);
