-- =============================================================================
-- V4__seed_data.sql — Development seed data for RUMS
--
-- All passwords are BCrypt(12) of 'Admin@1234!'
-- To regenerate the hash: new BCryptPasswordEncoder(12).encode("Admin@1234!")
-- =============================================================================

-- Fixed UUIDs used as anchors for FK references
-- Users:        a0000000-0000-0000-0000-00000000000{1-9}
-- Licenses:     b0000000-0000-0000-0000-00000000000{1-c}
-- Complaints:   c0000000-0000-0000-0000-00000000000{1-a}
-- Compliance:   d0000000-0000-0000-0000-00000000000{1-a}
-- Fraud:        e0000000-0000-0000-0000-00000000000{1-a}
-- CLMS:         f0000000-0000-0000-0000-00000000000{1-a}
-- Workflows:    10000000-0000-0000-0000-00000000000{1-8}
-- Wf Steps:     20000000-0000-0000-0000-00000000000{1-...}
-- Alerts:       30000000-0000-0000-0000-00000000000{1-a}
-- Reports:      40000000-0000-0000-0000-00000000000{1-a}

-- =============================================================================
-- 1. USERS
-- =============================================================================
INSERT INTO users (id, name, email, phone, password_hash, role, status, department, mfa_enabled, last_login, created_at)
VALUES
  ('a0000000-0000-0000-0000-000000000001',
   'System Administrator',     'admin@rura.rw',           '+250788000001',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'admin', 'active', 'ICT', FALSE,
   NOW() - INTERVAL '2 hours',  NOW() - INTERVAL '180 days'),

  ('a0000000-0000-0000-0000-000000000002',
   'Jean-Claude Ndayisaba',    'j.ndayisaba@rura.rw',     '+250788000002',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'supervisor', 'active', 'Licensing', FALSE,
   NOW() - INTERVAL '1 day',    NOW() - INTERVAL '150 days'),

  ('a0000000-0000-0000-0000-000000000003',
   'Alice Mugisha',            'a.mugisha@rura.rw',        '+250788000003',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'analyst', 'active', 'Compliance', FALSE,
   NOW() - INTERVAL '3 hours',  NOW() - INTERVAL '120 days'),

  ('a0000000-0000-0000-0000-000000000004',
   'Kagiso Mutabazi',          'k.mutabazi@rura.rw',       '+250788000004',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'analyst', 'active', 'Complaints', FALSE,
   NOW() - INTERVAL '5 hours',  NOW() - INTERVAL '90 days'),

  ('a0000000-0000-0000-0000-000000000005',
   'Marie Uwimana',            'm.uwimana@rura.rw',        '+250788000005',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'auditor', 'active', 'Fraud & Investigations', FALSE,
   NOW() - INTERVAL '2 days',   NOW() - INTERVAL '100 days'),

  ('a0000000-0000-0000-0000-000000000006',
   'Patrick Habimana',         'p.habimana@rura.rw',       '+250788000006',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'viewer', 'active', 'ICT', FALSE,
   NULL,                         NOW() - INTERVAL '60 days'),

  ('a0000000-0000-0000-0000-000000000007',
   'Eric Nzeyimana',           'e.nzeyimana@rura.rw',      '+250788000007',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'analyst', 'active', 'Licensing', FALSE,
   NOW() - INTERVAL '6 hours',  NOW() - INTERVAL '75 days'),

  ('a0000000-0000-0000-0000-000000000008',
   'Sarah Ingabire',           's.ingabire@rura.rw',       '+250788000008',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'supervisor', 'active', 'Finance', FALSE,
   NOW() - INTERVAL '1 day',    NOW() - INTERVAL '50 days'),

  ('a0000000-0000-0000-0000-000000000009',
   'Robert Kamanzi',           'r.kamanzi@rura.rw',        '+250788000009',
   '$2a$12$K4GYNtAmHSJLb.EonXzYZO2nWFaOFDJ1W7rCBtmHHtW84cJMlEnAm',
   'auditor', 'suspended', 'Legal', FALSE,
   NOW() - INTERVAL '30 days',  NOW() - INTERVAL '200 days');

-- =============================================================================
-- 2. LICENSES
-- =============================================================================
INSERT INTO licenses (id, license_number, operator_name, contact_person, contact_email,
                      category, sector, status, province,
                      issued_at, expires_at, annual_fee_rwf, last_renewal_at, created_at, updated_at)
VALUES
  ('b0000000-0000-0000-0000-000000000001',
   'RURA-TLC-2021-001', 'MTN Rwanda Ltd', 'David Munezero', 'licensing@mtn.rw',
   'Mobile Network Operator', 'Telecom', 'active', 'Kigali City',
   '2021-03-15', '2026-03-14', 120000000, '2024-03-15',
   NOW() - INTERVAL '5 years', NOW() - INTERVAL '5 years'),

  ('b0000000-0000-0000-0000-000000000002',
   'RURA-TLC-2021-002', 'Airtel Rwanda Ltd', 'Anne Kabalisa', 'licensing@airtel.rw',
   'Mobile Network Operator', 'Telecom', 'active', 'Kigali City',
   '2021-06-01', '2026-05-31', 120000000, '2024-06-01',
   NOW() - INTERVAL '4 years 9 months', NOW() - INTERVAL '4 years 9 months'),

  ('b0000000-0000-0000-0000-000000000003',
   'RURA-TLC-2022-001', 'Kigali Online Ltd', 'Brian Mugabo', 'legal@kigalionline.rw',
   'Internet Service Provider', 'Telecom', 'active', 'Kigali City',
   '2022-01-10', '2027-01-09', 25000000, NULL,
   NOW() - INTERVAL '4 years', NOW() - INTERVAL '4 years'),

  ('b0000000-0000-0000-0000-000000000004',
   'RURA-TLC-2022-002', 'Smile Communications Rwanda', 'Claire Uwera', 'info@smile.rw',
   'Internet Service Provider', 'Telecom', 'pending', 'Northern Province',
   '2022-09-20', '2027-09-19', 18000000, NULL,
   NOW() - INTERVAL '3 years 6 months', NOW() - INTERVAL '1 month'),

  ('b0000000-0000-0000-0000-000000000005',
   'RURA-ENG-2020-001', 'Rwanda Energy Group', 'Felix Habineza', 'legal@reg.rw',
   'Electricity Distribution', 'Energy', 'active', 'Kigali City',
   '2020-07-01', '2025-06-30', 350000000, '2023-07-01',
   NOW() - INTERVAL '6 years', NOW() - INTERVAL '6 years'),

  ('b0000000-0000-0000-0000-000000000006',
   'RURA-ENG-2020-002', 'BRALIRWA Power Ltd', 'Gloria Nkusi', 'power@bralirwa.rw',
   'Power Generation', 'Energy', 'active', 'Southern Province',
   '2020-11-15', '2025-11-14', 85000000, '2023-11-15',
   NOW() - INTERVAL '5 years 4 months', NOW() - INTERVAL '5 years 4 months'),

  ('b0000000-0000-0000-0000-000000000007',
   'RURA-WAT-2021-001', 'WASAC — Kigali', 'Henry Ruranga', 'licensing@wasac.rw',
   'Water Supply', 'Water', 'active', 'Kigali City',
   '2021-04-01', '2026-03-31', 45000000, '2024-04-01',
   NOW() - INTERVAL '5 years', NOW() - INTERVAL '5 years'),

  ('b0000000-0000-0000-0000-000000000008',
   'RURA-WAT-2022-001', 'Sanitation Services Rwanda Ltd', 'Immaculee Giraneza', 'ops@ssr.rw',
   'Sanitation Services', 'Water', 'suspended', 'Eastern Province',
   '2022-02-14', '2027-02-13', 22000000, NULL,
   NOW() - INTERVAL '4 years 1 month', NOW() - INTERVAL '2 months'),

  ('b0000000-0000-0000-0000-000000000009',
   'RURA-TRP-2021-001', 'Rwanda Bus Services Ltd', 'James Nkurunziza', 'legal@rbs.rw',
   'Road Transport Operator', 'Transport', 'active', 'Kigali City',
   '2021-08-20', '2026-08-19', 15000000, '2024-08-20',
   NOW() - INTERVAL '4 years 7 months', NOW() - INTERVAL '4 years 7 months'),

  ('b0000000-0000-0000-0000-000000000010',
   'RURA-TRP-2022-001', 'KBS Freight Ltd', 'Kevin Ntare', 'ops@kbs.rw',
   'Freight & Logistics', 'Transport', 'revoked', 'Western Province',
   '2022-05-01', '2027-04-30', 12000000, NULL,
   NOW() - INTERVAL '3 years 11 months', NOW() - INTERVAL '4 months'),

  ('b0000000-0000-0000-0000-000000000011',
   'RURA-TLC-2019-001', 'TeleRwanda PSTN', 'Leon Gasigwa', 'reg@telerwanda.rw',
   'Public Switched Telephone Network', 'Telecom', 'expired', 'Kigali City',
   '2019-01-01', '2024-12-31', 60000000, '2022-01-01',
   NOW() - INTERVAL '7 years', NOW() - INTERVAL '3 months'),

  ('b0000000-0000-0000-0000-000000000012',
   'RURA-TLC-2024-001', 'TeleCom Solutions Ltd', 'Monica Byukusenge', 'm.byukusenge@tcsl.rw',
   'Virtual Network Operator', 'Telecom', 'pending', 'Kigali City',
   '2024-11-01', '2029-10-31', 30000000, NULL,
   NOW() - INTERVAL '5 months', NOW() - INTERVAL '5 months');

-- =============================================================================
-- 3. COMPLAINTS
-- =============================================================================
INSERT INTO complaints (id, reference_number, subject, category,
                        complainant_name, complainant_phone, respondent_operator,
                        sector, province, status, severity, description,
                        assigned_to_id, filed_at, updated_at, resolved_at)
VALUES
  ('c0000000-0000-0000-0000-000000000001',
   'RURA-CMP-2026-0001',
   'Excessive billing — three times normal amount charged in January',
   'Billing Dispute',
   'Thierry Hakizimana', '+250789100001', 'MTN Rwanda Ltd',
   'Telecom', 'Kigali City', 'open', 'high',
   'My February bill was three times the agreed monthly rate. No service changes were made.',
   'a0000000-0000-0000-0000-000000000004',
   NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days', NULL),

  ('c0000000-0000-0000-0000-000000000002',
   'RURA-CMP-2026-0002',
   'Internet service down for 48 hours without prior notice',
   'Service Interruption',
   'Claudine Mukamana', '+250789100002', 'Airtel Rwanda Ltd',
   'Telecom', 'Northern Province', 'under_review', 'critical',
   'No internet connectivity from 4 March to 6 March 2026. No communication from the operator.',
   'a0000000-0000-0000-0000-000000000007',
   NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NULL),

  ('c0000000-0000-0000-0000-000000000003',
   'RURA-CMP-2026-0003',
   'Poor call quality on mobile network — dropped calls',
   'Poor Quality of Service',
   'Emmanuel Nkurunziza', '+250789100003', 'MTN Rwanda Ltd',
   'Telecom', 'Eastern Province', 'resolved', 'medium',
   'Consistent call drops and low audio quality over three consecutive weeks.',
   'a0000000-0000-0000-0000-000000000004',
   NOW() - INTERVAL '45 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),

  ('c0000000-0000-0000-0000-000000000004',
   'RURA-CMP-2026-0004',
   'Unauthorized data charges applied without subscription',
   'Unauthorized Charges',
   'Francoise Nyiraneza', '+250789100004', 'Airtel Rwanda Ltd',
   'Telecom', 'Southern Province', 'escalated', 'high',
   'Data bundles renewed automatically without my consent for three months.',
   'a0000000-0000-0000-0000-000000000002',
   NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days', NULL),

  ('c0000000-0000-0000-0000-000000000005',
   'RURA-CMP-2026-0005',
   'Electricity disconnection without 30-day written notice',
   'Contract Violation',
   'Gilbert Rugamba', '+250789100005', 'Rwanda Energy Group',
   'Energy', 'Kigali City', 'open', 'medium',
   'Power was cut on 1 March with no prior written notice as required by the contract.',
   NULL,
   NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NULL),

  ('c0000000-0000-0000-0000-000000000006',
   'RURA-CMP-2026-0006',
   'Customer service centre refused to process refund request',
   'Customer Service Failure',
   'Helene Mukamurara', '+250789100006', 'Rwanda Energy Group',
   'Energy', 'Western Province', 'closed', 'low',
   'Refund for overpaid tariff denied at branch level with no formal justification.',
   'a0000000-0000-0000-0000-000000000004',
   NOW() - INTERVAL '60 days', NOW() - INTERVAL '40 days', NOW() - INTERVAL '40 days'),

  ('c0000000-0000-0000-0000-000000000007',
   'RURA-CMP-2026-0007',
   'Suspected data breach — personal details disclosed to third party',
   'Data Privacy Breach',
   'Isabelle Umubyeyi', '+250789100007', 'Kigali Online Ltd',
   'Telecom', 'Kigali City', 'under_review', 'critical',
   'Received unsolicited marketing calls from third parties after registering with the ISP.',
   'a0000000-0000-0000-0000-000000000003',
   NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days', NULL),

  ('c0000000-0000-0000-0000-000000000008',
   'RURA-CMP-2026-0008',
   'Water tariff overcharge — billed at industrial rate for residential use',
   'Tariff Overcharge',
   'Joseph Muhire', '+250789100008', 'WASAC — Kigali',
   'Water', 'Kigali City', 'open', 'medium',
   'Residential account billed at industrial water tariff since October 2025.',
   'a0000000-0000-0000-0000-000000000004',
   NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', NULL),

  ('c0000000-0000-0000-0000-000000000009',
   'RURA-CMP-2026-0009',
   'New connection delayed beyond contractual 30-day period',
   'Connection Delay',
   'Karine Uwimana', '+250789100009', 'Rwanda Bus Services Ltd',
   'Transport', 'Southern Province', 'resolved', 'low',
   'Route licensing connection pending for 45 days, exceeding the agreed SLA.',
   'a0000000-0000-0000-0000-000000000007',
   NOW() - INTERVAL '50 days', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),

  ('c0000000-0000-0000-0000-000000000010',
   'RURA-CMP-2026-0010',
   'Spectrum interference causing broadcast signal degradation',
   'Other',
   'Luc Niyomugabo', '+250789100010', 'TeleRwanda PSTN',
   'Telecom', 'Northern Province', 'open', 'high',
   'FM broadcast signal repeatedly disrupted by unknown spectrum interference since February.',
   NULL,
   NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', NULL);

-- =============================================================================
-- 4. COMPLIANCE RECORDS
-- =============================================================================
INSERT INTO compliance_records (id, operator_name, license_id, sector,
                                 check_type, status, due_date, last_audit_date,
                                 score, auditor_id, findings, created_at, updated_at)
VALUES
  ('d0000000-0000-0000-0000-000000000001',
   'MTN Rwanda Ltd', 'b0000000-0000-0000-0000-000000000001', 'Telecom',
   'Quality of Service (QoS) Audit', 'compliant', '2026-03-31', '2026-02-15',
   88, 'a0000000-0000-0000-0000-000000000003',
   'QoS metrics meet minimum thresholds. Minor congestion noted in Musanze.',
   NOW() - INTERVAL '60 days', NOW() - INTERVAL '20 days'),

  ('d0000000-0000-0000-0000-000000000002',
   'Airtel Rwanda Ltd', 'b0000000-0000-0000-0000-000000000002', 'Telecom',
   'Annual Return Filing', 'compliant', '2026-01-31', '2026-01-20',
   95, 'a0000000-0000-0000-0000-000000000003',
   'Annual return submitted on time with all required financial disclosures.',
   NOW() - INTERVAL '90 days', NOW() - INTERVAL '50 days'),

  ('d0000000-0000-0000-0000-000000000003',
   'Kigali Online Ltd', 'b0000000-0000-0000-0000-000000000003', 'Telecom',
   'Consumer Protection Audit', 'under_review', '2026-04-30', NULL,
   NULL, 'a0000000-0000-0000-0000-000000000005',
   NULL,
   NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'),

  ('d0000000-0000-0000-0000-000000000004',
   'Rwanda Energy Group', 'b0000000-0000-0000-0000-000000000005', 'Energy',
   'Environmental Compliance', 'non_compliant', '2026-02-28', '2026-02-10',
   42, 'a0000000-0000-0000-0000-000000000005',
   'Carbon emission reporting incomplete. Three substations missing environmental monitoring data.',
   NOW() - INTERVAL '60 days', NOW() - INTERVAL '25 days'),

  ('d0000000-0000-0000-0000-000000000005',
   'Rwanda Energy Group', 'b0000000-0000-0000-0000-000000000005', 'Energy',
   'Financial Reporting', 'compliant', '2026-03-31', '2026-03-20',
   91, 'a0000000-0000-0000-0000-000000000009',
   'Quarterly financial statements accurate. Tariff revenue reconciled.',
   NOW() - INTERVAL '45 days', NOW() - INTERVAL '15 days'),

  ('d0000000-0000-0000-0000-000000000006',
   'WASAC — Kigali', 'b0000000-0000-0000-0000-000000000007', 'Water',
   'Universal Access Obligation', 'remediation', '2026-03-15', '2026-03-01',
   58, 'a0000000-0000-0000-0000-000000000003',
   'Rural coverage targets not met in Bugesera and Rulindo districts. Remediation plan submitted.',
   NOW() - INTERVAL '40 days', NOW() - INTERVAL '5 days'),

  ('d0000000-0000-0000-0000-000000000007',
   'Airtel Rwanda Ltd', 'b0000000-0000-0000-0000-000000000002', 'Telecom',
   'Spectrum Usage Compliance', 'compliant', '2025-12-31', '2025-12-15',
   83, 'a0000000-0000-0000-0000-000000000009',
   'Spectrum usage within licensed bands. No interference reported.',
   NOW() - INTERVAL '120 days', NOW() - INTERVAL '80 days'),

  ('d0000000-0000-0000-0000-000000000008',
   'Rwanda Bus Services Ltd', 'b0000000-0000-0000-0000-000000000009', 'Transport',
   'Network Rollout Target', 'under_review', '2026-06-30', NULL,
   NULL, 'a0000000-0000-0000-0000-000000000003',
   NULL,
   NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),

  ('d0000000-0000-0000-0000-000000000009',
   'MTN Rwanda Ltd', 'b0000000-0000-0000-0000-000000000001', 'Telecom',
   'Security & Data Protection Audit', 'non_compliant', '2026-03-01', '2026-02-28',
   35, 'a0000000-0000-0000-0000-000000000005',
   'Critical: customer PII stored without adequate encryption. Immediate remediation required.',
   NOW() - INTERVAL '35 days', NOW() - INTERVAL '5 days'),

  ('d0000000-0000-0000-0000-000000000010',
   'BRALIRWA Power Ltd', 'b0000000-0000-0000-0000-000000000006', 'Energy',
   'Tariff Filing', 'compliant', '2026-02-15', '2026-02-12',
   76, 'a0000000-0000-0000-0000-000000000003',
   'Tariff schedule filed on time. Minor discrepancies in peak-hour rates corrected.',
   NOW() - INTERVAL '55 days', NOW() - INTERVAL '30 days');

-- =============================================================================
-- 5. FRAUD CASES
-- =============================================================================
INSERT INTO fraud_cases (id, case_number, description, indicator_type,
                          reported_by, operator_involved, sector,
                          risk_level, status, reported_at,
                          estimated_loss_rwf, investigating_officer_id, created_at, updated_at)
VALUES
  ('e0000000-0000-0000-0000-000000000001',
   'RURA-FRD-2026-0001',
   'SIM Box devices detected at two residential addresses routing international calls as local.',
   'SIM Box Fraud',
   'MTN Rwanda Fraud Unit', 'MTN Rwanda Ltd', 'Telecom',
   'critical', 'investigating', '2026-01-15',
   85000000, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '80 days', NOW() - INTERVAL '10 days'),

  ('e0000000-0000-0000-0000-000000000002',
   'RURA-FRD-2026-0002',
   'Duplicate license applications submitted using identical business registration numbers.',
   'Duplicate Applications',
   'RURA Internal Audit', 'Unknown Applicant', 'Telecom',
   'high', 'open', '2026-02-03',
   0, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '62 days', NOW() - INTERVAL '62 days'),

  ('e0000000-0000-0000-0000-000000000003',
   'RURA-FRD-2026-0003',
   'Operator revenue self-declaration 40% below estimated market share — suspected underreporting.',
   'Revenue Underreporting',
   'Finance Department RURA', 'Smile Communications Rwanda', 'Telecom',
   'high', 'investigating', '2026-01-22',
   200000000, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '74 days', NOW() - INTERVAL '20 days'),

  ('e0000000-0000-0000-0000-000000000004',
   'RURA-FRD-2026-0004',
   'Ghost residential water accounts created to inflate subsidy claim.',
   'Ghost Customer Registrations',
   'WASAC Internal Controls', 'WASAC — Kigali', 'Water',
   'medium', 'confirmed', '2025-11-10',
   12000000, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '147 days', NOW() - INTERVAL '30 days'),

  ('e0000000-0000-0000-0000-000000000005',
   'RURA-FRD-2026-0005',
   'Unusual spike in roaming billing charges for accounts with no international travel history.',
   'Unusual Billing Pattern',
   'Consumer Tip-off', 'Airtel Rwanda Ltd', 'Telecom',
   'medium', 'open', '2026-03-01',
   5000000, NULL,
   NOW() - INTERVAL '36 days', NOW() - INTERVAL '36 days'),

  ('e0000000-0000-0000-0000-000000000006',
   'RURA-FRD-2026-0006',
   'Electricity meter tampering discovered during scheduled maintenance inspection.',
   'Meter Tampering',
   'Rwanda Energy Group Field Team', 'Rwanda Energy Group', 'Energy',
   'critical', 'confirmed', '2026-02-14',
   45000000, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '51 days', NOW() - INTERVAL '15 days'),

  ('e0000000-0000-0000-0000-000000000007',
   'RURA-FRD-2026-0007',
   'Unlicensed VSAT terminal operating without type approval in Musanze district.',
   'Unlicensed Operation',
   'RURA Spectrum Monitoring Unit', 'Unknown Operator', 'Telecom',
   'high', 'referred', '2026-03-10',
   0, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '27 days', NOW() - INTERVAL '5 days'),

  ('e0000000-0000-0000-0000-000000000008',
   'RURA-FRD-2026-0008',
   'Identity documents forged to obtain road transport operating permit.',
   'Identity Misrepresentation',
   'Rwanda Investigation Bureau tip-off', 'KBS Freight Ltd', 'Transport',
   'high', 'dismissed', '2025-12-05',
   0, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '123 days', NOW() - INTERVAL '60 days'),

  ('e0000000-0000-0000-0000-000000000009',
   'RURA-FRD-2026-0009',
   'Spectrum interference pattern consistent with deliberate jamming of competitor frequencies.',
   'Spectrum Interference',
   'RURA Spectrum Monitoring', 'MTN Rwanda Ltd', 'Telecom',
   'critical', 'investigating', '2026-03-20',
   0, 'a0000000-0000-0000-0000-000000000005',
   NOW() - INTERVAL '17 days', NOW() - INTERVAL '5 days'),

  ('e0000000-0000-0000-0000-000000000010',
   'RURA-FRD-2026-0010',
   'Transport tariff manipulation: passenger fares charged above RURA-approved maximums.',
   'Tariff Manipulation',
   'Passenger Complaint Cluster', 'Rwanda Bus Services Ltd', 'Transport',
   'low', 'open', '2026-03-25',
   0, NULL,
   NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days');

-- =============================================================================
-- 6. CLMS CASES
-- =============================================================================
INSERT INTO clms_cases (id, case_number, title, type, status,
                         applicant_name, applicant_email, sector, province,
                         assigned_to_id, notes, submitted_at, updated_at)
VALUES
  ('f0000000-0000-0000-0000-000000000001',
   'CLMS-2026-0001',
   'New Mobile Network Operator Licence — TeleCom Solutions Ltd',
   'new_license', 'under_review',
   'TeleCom Solutions Ltd', 'legal@telecomsolutions.rw', 'Telecom', 'Kigali City',
   'a0000000-0000-0000-0000-000000000007',
   'Full documentation received. Technical assessment in progress.',
   NOW() - INTERVAL '45 days', NOW() - INTERVAL '10 days'),

  ('f0000000-0000-0000-0000-000000000002',
   'CLMS-2026-0002',
   'Licence Renewal — MTN Rwanda Ltd (Mobile Network Operator)',
   'license_renewal', 'approved',
   'MTN Rwanda Ltd', 'licensing@mtn.rw', 'Telecom', 'Kigali City',
   'a0000000-0000-0000-0000-000000000002',
   'Renewal approved for 5 years. Annual fee increased by 5%.',
   NOW() - INTERVAL '90 days', NOW() - INTERVAL '20 days'),

  ('f0000000-0000-0000-0000-000000000003',
   'CLMS-2026-0003',
   'Spectrum Assignment — 5G Pilot Spectrum Band 3.5 GHz',
   'spectrum_assignment', 'submitted',
   'Airtel Rwanda Ltd', 'spectrum@airtel.rw', 'Telecom', 'Kigali City',
   'a0000000-0000-0000-0000-000000000007',
   'Pilot for urban Kigali only. Interference study required.',
   NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days'),

  ('f0000000-0000-0000-0000-000000000004',
   'CLMS-2026-0004',
   'Tariff Review — Electricity Distribution Residential Rates 2026',
   'tariff_review', 'under_review',
   'Rwanda Energy Group', 'tariffs@reg.rw', 'Energy', 'Kigali City',
   'a0000000-0000-0000-0000-000000000002',
   'Consumer impact assessment underway. Public consultation scheduled for April.',
   NOW() - INTERVAL '35 days', NOW() - INTERVAL '8 days'),

  ('f0000000-0000-0000-0000-000000000005',
   'CLMS-2026-0005',
   'Licence Amendment — Add Water Supply to Sanitation Services Rwanda Ltd Scope',
   'license_amendment', 'draft',
   'Sanitation Services Rwanda Ltd', 'reg@ssr.rw', 'Water', 'Eastern Province',
   NULL,
   'Preliminary review shows geographic overlap concern with WASAC.',
   NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),

  ('f0000000-0000-0000-0000-000000000006',
   'CLMS-2026-0006',
   'Licence Revocation — KBS Freight Ltd (Non-compliance)',
   'license_revocation', 'approved',
   'RURA Enforcement Unit', 'enforcement@rura.rw', 'Transport', 'Western Province',
   'a0000000-0000-0000-0000-000000000001',
   'Revocation approved following confirmed fraud case RURA-FRD-2026-0008.',
   NOW() - INTERVAL '55 days', NOW() - INTERVAL '15 days'),

  ('f0000000-0000-0000-0000-000000000007',
   'CLMS-2026-0007',
   'Type Approval — LTE-Advanced Radio Equipment (Vendor: Huawei)',
   'type_approval', 'submitted',
   'Huawei Technologies Rwanda', 'regulatory@huawei.rw', 'Telecom', 'Kigali City',
   'a0000000-0000-0000-0000-000000000007',
   'EMC and safety test reports attached. Awaiting lab confirmation.',
   NOW() - INTERVAL '12 days', NOW() - INTERVAL '11 days'),

  ('f0000000-0000-0000-0000-000000000008',
   'CLMS-2026-0008',
   'Dispute Resolution — Billing Overcharge Dispute: MTN Rwanda vs. RURA Complainant',
   'dispute_resolution', 'closed',
   'Jean-Pierre Hakizimana', 'jp.hakizimana@gmail.com', 'Telecom', 'Kigali City',
   'a0000000-0000-0000-0000-000000000004',
   'Dispute resolved: MTN issued refund. Case closed.',
   NOW() - INTERVAL '75 days', NOW() - INTERVAL '30 days'),

  ('f0000000-0000-0000-0000-000000000009',
   'CLMS-2026-0009',
   'New Licence Application — ISP for Northern Province Rural Areas',
   'new_license', 'rejected',
   'Norrnet Rwanda Ltd', 'apply@norrnet.rw', 'Telecom', 'Northern Province',
   'a0000000-0000-0000-0000-000000000007',
   'Application rejected: minimum capital requirements not met. Re-application permitted after 6 months.',
   NOW() - INTERVAL '100 days', NOW() - INTERVAL '40 days'),

  ('f0000000-0000-0000-0000-000000000010',
   'CLMS-2026-0010',
   'Licence Renewal — Rwanda Bus Services Ltd (Road Transport)',
   'license_renewal', 'appealed',
   'Rwanda Bus Services Ltd', 'legal@rbs.rw', 'Transport', 'Kigali City',
   'a0000000-0000-0000-0000-000000000002',
   'Appeal filed against renewal conditions. Hearing scheduled 20 April 2026.',
   NOW() - INTERVAL '28 days', NOW() - INTERVAL '3 days');

-- =============================================================================
-- 7. WORKFLOWS
-- =============================================================================
INSERT INTO workflows (id, name, description, trigger, status, sector,
                        created_by_id, related_entity_id, created_at, started_at, completed_at)
VALUES
  ('10000000-0000-0000-0000-000000000001',
   'New Licence Application — Telecom',
   'Standard workflow for processing new telecommunications licence applications.',
   'license_application', 'active', 'Telecom',
   'a0000000-0000-0000-0000-000000000001',
   'f0000000-0000-0000-0000-000000000001',
   NOW() - INTERVAL '45 days', NOW() - INTERVAL '44 days', NULL),

  ('10000000-0000-0000-0000-000000000002',
   'Licence Renewal — MTN Rwanda',
   'Renewal review workflow for MTN Rwanda mobile network operator licence.',
   'renewal_due', 'completed', 'Telecom',
   'a0000000-0000-0000-0000-000000000002',
   'f0000000-0000-0000-0000-000000000002',
   NOW() - INTERVAL '90 days', NOW() - INTERVAL '89 days', NOW() - INTERVAL '20 days'),

  ('10000000-0000-0000-0000-000000000003',
   'Consumer Complaint Escalation — Data Privacy',
   'Escalation workflow triggered by critical data privacy complaint.',
   'complaint_filed', 'active', 'Telecom',
   'a0000000-0000-0000-0000-000000000001',
   'c0000000-0000-0000-0000-000000000007',
   NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days', NULL),

  ('10000000-0000-0000-0000-000000000004',
   'Compliance Remediation — MTN Data Security',
   'Remediation follow-up for critical data security non-compliance finding.',
   'compliance_due', 'active', 'Telecom',
   'a0000000-0000-0000-0000-000000000001',
   'd0000000-0000-0000-0000-000000000009',
   NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days', NULL),

  ('10000000-0000-0000-0000-000000000005',
   'Fraud Investigation — SIM Box (MTN)',
   'Fraud case investigation workflow for SIM Box fraud at MTN Rwanda.',
   'fraud_alert', 'active', 'Telecom',
   'a0000000-0000-0000-0000-000000000001',
   'e0000000-0000-0000-0000-000000000001',
   NOW() - INTERVAL '79 days', NOW() - INTERVAL '78 days', NULL),

  ('10000000-0000-0000-0000-000000000006',
   'Tariff Review — Energy Residential 2026',
   'Workflow for public consultation and approval of new residential electricity tariffs.',
   'manual', 'active', 'Energy',
   'a0000000-0000-0000-0000-000000000002',
   'f0000000-0000-0000-0000-000000000004',
   NOW() - INTERVAL '35 days', NOW() - INTERVAL '34 days', NULL),

  ('10000000-0000-0000-0000-000000000007',
   'Spectrum Assignment Review — 5G Pilot',
   'Technical and regulatory review for 5G spectrum assignment request.',
   'license_application', 'draft', 'Telecom',
   'a0000000-0000-0000-0000-000000000002',
   'f0000000-0000-0000-0000-000000000003',
   NOW() - INTERVAL '15 days', NULL, NULL),

  ('10000000-0000-0000-0000-000000000008',
   'Licence Revocation — KBS Freight',
   'Formal revocation process for KBS Freight Ltd licence following fraud confirmation.',
   'fraud_alert', 'completed', 'Transport',
   'a0000000-0000-0000-0000-000000000001',
   'f0000000-0000-0000-0000-000000000006',
   NOW() - INTERVAL '55 days', NOW() - INTERVAL '54 days', NOW() - INTERVAL '15 days');

-- =============================================================================
-- 7a. WORKFLOW STEPS
-- =============================================================================
INSERT INTO workflow_steps (id, workflow_id, step_order, name, description,
                             assigned_role, status, due_in_days, completed_at, completed_by_id)
VALUES
  -- Workflow 1: New Licence Application (active)
  ('20000000-0000-0000-0000-000000000001',
   '10000000-0000-0000-0000-000000000001', 1,
   'Document Verification', 'Check completeness of submitted documents.',
   'analyst', 'completed', 3, NOW() - INTERVAL '40 days', 'a0000000-0000-0000-0000-000000000007'),
  ('20000000-0000-0000-0000-000000000002',
   '10000000-0000-0000-0000-000000000001', 2,
   'Technical Assessment', 'Evaluate technical capability and spectrum requirements.',
   'analyst', 'in_progress', 10, NULL, NULL),
  ('20000000-0000-0000-0000-000000000003',
   '10000000-0000-0000-0000-000000000001', 3,
   'Legal Review', 'Review legal compliance and corporate structure.',
   'auditor', 'pending', 7, NULL, NULL),
  ('20000000-0000-0000-0000-000000000004',
   '10000000-0000-0000-0000-000000000001', 4,
   'Supervisor Approval', 'Final sign-off before licence issuance.',
   'supervisor', 'pending', 3, NULL, NULL),

  -- Workflow 2: Licence Renewal MTN (completed)
  ('20000000-0000-0000-0000-000000000005',
   '10000000-0000-0000-0000-000000000002', 1,
   'Renewal Application Review', 'Verify renewal application and supporting documents.',
   'analyst', 'completed', 5, NOW() - INTERVAL '75 days', 'a0000000-0000-0000-0000-000000000007'),
  ('20000000-0000-0000-0000-000000000006',
   '10000000-0000-0000-0000-000000000002', 2,
   'Compliance Check', 'Confirm no outstanding compliance issues.',
   'auditor', 'completed', 7, NOW() - INTERVAL '55 days', 'a0000000-0000-0000-0000-000000000003'),
  ('20000000-0000-0000-0000-000000000007',
   '10000000-0000-0000-0000-000000000002', 3,
   'Approval and Issuance', 'Approve renewal and update licence record.',
   'supervisor', 'completed', 3, NOW() - INTERVAL '20 days', 'a0000000-0000-0000-0000-000000000002'),

  -- Workflow 3: Complaint Escalation (active)
  ('20000000-0000-0000-0000-000000000008',
   '10000000-0000-0000-0000-000000000003', 1,
   'Initial Assessment', 'Assess severity and assign lead investigator.',
   'analyst', 'completed', 1, NOW() - INTERVAL '5 days', 'a0000000-0000-0000-0000-000000000004'),
  ('20000000-0000-0000-0000-000000000009',
   '10000000-0000-0000-0000-000000000003', 2,
   'Operator Response Collection', 'Request formal response from the operator.',
   'analyst', 'in_progress', 7, NULL, NULL),
  ('20000000-0000-0000-0000-000000000010',
   '10000000-0000-0000-0000-000000000003', 3,
   'Resolution and Decision', 'Issue binding decision and notify parties.',
   'supervisor', 'pending', 5, NULL, NULL),

  -- Workflow 5: Fraud Investigation (active)
  ('20000000-0000-0000-0000-000000000011',
   '10000000-0000-0000-0000-000000000005', 1,
   'Evidence Gathering', 'Collect technical evidence from operator and field.',
   'auditor', 'completed', 14, NOW() - INTERVAL '50 days', 'a0000000-0000-0000-0000-000000000005'),
  ('20000000-0000-0000-0000-000000000012',
   '10000000-0000-0000-0000-000000000005', 2,
   'Legal Analysis', 'Assess criminal liability and regulatory violations.',
   'auditor', 'in_progress', 14, NULL, NULL),
  ('20000000-0000-0000-0000-000000000013',
   '10000000-0000-0000-0000-000000000005', 3,
   'Referral to Prosecution', 'Prepare case file for prosecution referral.',
   'supervisor', 'pending', 7, NULL, NULL),

  -- Workflow 8: Licence Revocation KBS (completed)
  ('20000000-0000-0000-0000-000000000014',
   '10000000-0000-0000-0000-000000000008', 1,
   'Evidence Review', 'Review fraud investigation findings.',
   'auditor', 'completed', 5, NOW() - INTERVAL '45 days', 'a0000000-0000-0000-0000-000000000005'),
  ('20000000-0000-0000-0000-000000000015',
   '10000000-0000-0000-0000-000000000008', 2,
   'Legal Notice to Operator', 'Issue formal revocation notice.',
   'supervisor', 'completed', 7, NOW() - INTERVAL '30 days', 'a0000000-0000-0000-0000-000000000002'),
  ('20000000-0000-0000-0000-000000000016',
   '10000000-0000-0000-0000-000000000008', 3,
   'Licence Cancellation', 'Update licence status and publish public notice.',
   'admin', 'completed', 3, NOW() - INTERVAL '15 days', 'a0000000-0000-0000-0000-000000000001');

-- =============================================================================
-- 8. ALERTS
-- =============================================================================
INSERT INTO alerts (id, type, title, message, severity, status,
                    related_module, related_entity_id, actioned_by_id, created_at, read_at)
VALUES
  ('30000000-0000-0000-0000-000000000001',
   'license_expiry',
   'Licence Expiry in 30 Days — Rwanda Energy Group',
   'Licence RURA-ENG-2020-001 for Rwanda Energy Group expires on 2025-06-30. Renewal action required.',
   'warning', 'unread', 'Licenses', 'b0000000-0000-0000-0000-000000000005',
   NULL, NOW() - INTERVAL '5 days', NULL),

  ('30000000-0000-0000-0000-000000000002',
   'license_expiry',
   'Licence Expired — TeleRwanda PSTN',
   'Licence RURA-TLC-2019-001 for TeleRwanda PSTN expired on 2024-12-31.',
   'critical', 'actioned', 'Licenses', 'b0000000-0000-0000-0000-000000000011',
   'a0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '95 days', NOW() - INTERVAL '90 days'),

  ('30000000-0000-0000-0000-000000000003',
   'compliance_breach',
   'Critical Non-Compliance — MTN Rwanda Data Security',
   'MTN Rwanda Ltd failed the Security & Data Protection Audit with a score of 35/100. Immediate remediation required.',
   'critical', 'unread', 'Compliance', 'd0000000-0000-0000-0000-000000000009',
   NULL, NOW() - INTERVAL '5 days', NULL),

  ('30000000-0000-0000-0000-000000000004',
   'compliance_breach',
   'Non-Compliance — Rwanda Energy Group Environmental Reporting',
   'Rwanda Energy Group failed the Environmental Compliance check. Score: 42/100.',
   'warning', 'read', 'Compliance', 'd0000000-0000-0000-0000-000000000004',
   NULL, NOW() - INTERVAL '24 days', NOW() - INTERVAL '20 days'),

  ('30000000-0000-0000-0000-000000000005',
   'fraud_detected',
   'Critical Fraud Alert — SIM Box Operation (MTN Rwanda)',
   'Critical fraud case RURA-FRD-2026-0001 confirmed: SIM Box devices detected routing international calls illegally.',
   'critical', 'actioned', 'Fraud', 'e0000000-0000-0000-0000-000000000001',
   'a0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '78 days', NOW() - INTERVAL '75 days'),

  ('30000000-0000-0000-0000-000000000006',
   'complaint_sla_breach',
   'SLA Breach — Complaint RURA-CMP-2026-0002 (> 14 days)',
   'Complaint RURA-CMP-2026-0002 has exceeded the 14-day SLA. Current status: under_review.',
   'warning', 'unread', 'Complaints', 'c0000000-0000-0000-0000-000000000002',
   NULL, NOW() - INTERVAL '1 day', NULL),

  ('30000000-0000-0000-0000-000000000007',
   'workflow_stalled',
   'Workflow Stalled — Fraud Investigation SIM Box',
   'Workflow "Fraud Investigation — SIM Box (MTN)" has been active for more than 5 days with no recent step progress.',
   'warning', 'dismissed', 'Workflows', '10000000-0000-0000-0000-000000000005',
   NULL, NOW() - INTERVAL '40 days', NULL),

  ('30000000-0000-0000-0000-000000000008',
   'user_suspended',
   'User Account Suspended — Robert Kamanzi',
   'User account r.kamanzi@rura.rw (Legal) has been suspended by an administrator.',
   'info', 'read', 'Users', 'a0000000-0000-0000-0000-000000000009',
   NULL, NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days'),

  ('30000000-0000-0000-0000-000000000009',
   'report_ready',
   'Report Published — Q4 2025 Telecom Regulatory Report',
   'Quarterly report "Q4 2025 Telecom Regulatory Report" has been published and is available for download.',
   'info', 'read', 'Reports', '40000000-0000-0000-0000-000000000003',
   NULL, NOW() - INTERVAL '15 days', NOW() - INTERVAL '14 days'),

  ('30000000-0000-0000-0000-000000000010',
   'threshold_exceeded',
   'Fraud Loss Threshold Exceeded — Q1 2026',
   'Total estimated fraud losses for Q1 2026 have exceeded RWF 200,000,000. Immediate board escalation recommended.',
   'critical', 'unread', 'Fraud', NULL,
   NULL, NOW() - INTERVAL '3 days', NULL);

-- =============================================================================
-- 9. REPORTS
-- =============================================================================
INSERT INTO reports (id, title, type, sector, status, format,
                     created_by_id, period, stored_path, size_kb, created_at, published_at)
VALUES
  ('40000000-0000-0000-0000-000000000001',
   'Q1 2026 Telecom Regulatory Report',
   'quarterly', 'Telecom', 'draft', 'pdf',
   'a0000000-0000-0000-0000-000000000008',
   'Jan – Mar 2026', NULL, NULL,
   NOW() - INTERVAL '5 days', NULL),

  ('40000000-0000-0000-0000-000000000002',
   'Annual 2025 Energy Sector Report',
   'annual', 'Energy', 'published', 'pdf',
   'a0000000-0000-0000-0000-000000000001',
   'Jan – Dec 2025',
   './uploads/reports/40000000-0000-0000-0000-000000000002_annual-energy-2025.pdf', 4820,
   NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days'),

  ('40000000-0000-0000-0000-000000000003',
   'Q4 2025 Telecom Regulatory Report',
   'quarterly', 'Telecom', 'published', 'pdf',
   'a0000000-0000-0000-0000-000000000008',
   'Oct – Dec 2025',
   './uploads/reports/40000000-0000-0000-0000-000000000003_q4-telecom-2025.pdf', 3210,
   NOW() - INTERVAL '90 days', NOW() - INTERVAL '15 days'),

  ('40000000-0000-0000-0000-000000000004',
   'Q3 2025 Water Sector Compliance Report',
   'quarterly', 'Water', 'archived', 'xlsx',
   'a0000000-0000-0000-0000-000000000003',
   'Jul – Sep 2025',
   './uploads/reports/40000000-0000-0000-0000-000000000004_q3-water-2025.xlsx', 1540,
   NOW() - INTERVAL '180 days', NOW() - INTERVAL '120 days'),

  ('40000000-0000-0000-0000-000000000005',
   'Fraud & Anomaly Detection — Q1 2026 Summary',
   'quarterly', 'All Sectors', 'draft', 'pdf',
   'a0000000-0000-0000-0000-000000000005',
   'Jan – Mar 2026', NULL, NULL,
   NOW() - INTERVAL '3 days', NULL),

  ('40000000-0000-0000-0000-000000000006',
   'Monthly Complaints Overview — March 2026',
   'monthly', 'All Sectors', 'published', 'csv',
   'a0000000-0000-0000-0000-000000000004',
   'March 2026',
   './uploads/reports/40000000-0000-0000-0000-000000000006_complaints-march-2026.csv', 320,
   NOW() - INTERVAL '7 days', NOW() - INTERVAL '5 days'),

  ('40000000-0000-0000-0000-000000000007',
   'Annual 2025 Transport Sector Report',
   'annual', 'Transport', 'published', 'pdf',
   'a0000000-0000-0000-0000-000000000002',
   'Jan – Dec 2025',
   './uploads/reports/40000000-0000-0000-0000-000000000007_annual-transport-2025.pdf', 2870,
   NOW() - INTERVAL '45 days', NOW() - INTERVAL '20 days'),

  ('40000000-0000-0000-0000-000000000008',
   'Ad Hoc Report — Spectrum Monitoring Q1 2026',
   'ad_hoc', 'Telecom', 'published', 'pdf',
   'a0000000-0000-0000-0000-000000000007',
   'Q1 2026',
   './uploads/reports/40000000-0000-0000-0000-000000000008_spectrum-q1-2026.pdf', 1120,
   NOW() - INTERVAL '10 days', NOW() - INTERVAL '7 days'),

  ('40000000-0000-0000-0000-000000000009',
   'Q2 2025 Energy Compliance Report',
   'quarterly', 'Energy', 'archived', 'pdf',
   'a0000000-0000-0000-0000-000000000003',
   'Apr – Jun 2025',
   './uploads/reports/40000000-0000-0000-0000-000000000009_q2-energy-2025.pdf', 2340,
   NOW() - INTERVAL '270 days', NOW() - INTERVAL '210 days'),

  ('40000000-0000-0000-0000-000000000010',
   'Monthly Licensing Activity — February 2026',
   'monthly', 'All Sectors', 'published', 'xlsx',
   'a0000000-0000-0000-0000-000000000007',
   'February 2026',
   './uploads/reports/40000000-0000-0000-0000-000000000010_licensing-feb-2026.xlsx', 890,
   NOW() - INTERVAL '35 days', NOW() - INTERVAL '28 days');
