CREATE TYPE sector AS ENUM ('Telecom','Energy','Water','Transport');

CREATE TYPE province AS ENUM ('Kigali City','Northern Province','Southern Province','Eastern Province','Western Province');

CREATE TYPE user_role AS ENUM ('admin','analyst','auditor','supervisor','viewer');

CREATE TYPE user_status AS ENUM ('active','inactive','suspended');

CREATE TYPE user_dept AS ENUM ('Executive','Licensing','Compliance','Complaints','Fraud & Investigations','Legal','ICT','Finance','Human Resources','Communications');

CREATE TYPE license_status AS ENUM ('active','pending','suspended','revoked','expired');

CREATE TYPE license_cat AS ENUM ('Mobile Network Operator','Fixed Network Operator','Internet Service Provider','Public Switched Telephone Network','Virtual Network Operator','Spectrum License','Electricity Distribution','Electricity Transmission','Power Generation','Water Supply','Sanitation Services','Road Transport Operator','Freight & Logistics','Broadcasting');

CREATE TYPE complaint_status AS ENUM ('open','under_review','resolved','closed','escalated');

CREATE TYPE complaint_severity AS ENUM ('low','medium','high','critical');

CREATE TYPE complaint_category AS ENUM ('Billing Dispute','Service Interruption','Poor Quality of Service','Unauthorized Charges','Contract Violation','Customer Service Failure','Data Privacy Breach','Tariff Overcharge','Connection Delay','Other');

CREATE TYPE compliance_status AS ENUM ('compliant','non_compliant','under_review','remediation');

CREATE TYPE compliance_check AS ENUM ('Annual Return Filing','Quality of Service (QoS) Audit','Universal Access Obligation','Spectrum Usage Compliance','Tariff Filing','Consumer Protection Audit','Network Rollout Target','Environmental Compliance','Financial Reporting','Security & Data Protection Audit');

CREATE TYPE fraud_risk AS ENUM ('low','medium','high','critical');

CREATE TYPE fraud_status AS ENUM ('open','investigating','confirmed','dismissed','referred');

CREATE TYPE fraud_indicator AS ENUM ('Unusual Billing Pattern','Duplicate Applications','Identity Misrepresentation','Revenue Underreporting','Spectrum Interference','Unlicensed Operation','Tariff Manipulation','Meter Tampering','SIM Box Fraud','Ghost Customer Registrations');

CREATE TYPE clms_status AS ENUM ('draft','submitted','under_review','approved','rejected','appealed','closed');

CREATE TYPE clms_type AS ENUM ('new_license','license_renewal','license_amendment','license_revocation','tariff_review','spectrum_assignment','type_approval','dispute_resolution');

CREATE TYPE workflow_status AS ENUM ('draft','active','paused','completed','failed');

CREATE TYPE workflow_trigger AS ENUM ('license_application','complaint_filed','compliance_due','fraud_alert','renewal_due','manual');

CREATE TYPE workflow_step_status AS ENUM ('pending','in_progress','completed','skipped','failed');

CREATE TYPE alert_severity AS ENUM ('info','warning','critical');

CREATE TYPE alert_status AS ENUM ('unread','read','dismissed','actioned');

CREATE TYPE alert_type AS ENUM ('license_expiry','compliance_breach','fraud_detected','complaint_sla_breach','workflow_stalled','system_error','report_ready','user_suspended','threshold_exceeded');

CREATE TYPE audit_action AS ENUM ('create','update','delete','approve','reject','suspend','reinstate','export','login','logout','password_reset','permission_change');

CREATE TYPE audit_module AS ENUM ('Licenses','Complaints','Compliance','Fraud','Reports','Users','Workflows','Alerts','CLMS','System');

CREATE TYPE report_status AS ENUM ('draft','published','archived');

CREATE TYPE report_type AS ENUM ('monthly','quarterly','annual','ad_hoc');

CREATE TYPE report_format AS ENUM ('pdf','xlsx','csv');
