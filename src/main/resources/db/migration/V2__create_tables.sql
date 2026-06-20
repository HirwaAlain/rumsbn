-- Users (no FK dependencies)
CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name          VARCHAR(120)  NOT NULL,
  email         VARCHAR(200)  NOT NULL UNIQUE,
  phone         VARCHAR(20),
  password_hash VARCHAR(255)  NOT NULL,
  role          user_role     NOT NULL DEFAULT 'viewer',
  status        user_status   NOT NULL DEFAULT 'active',
  department    user_dept     NOT NULL,
  mfa_enabled   BOOLEAN       NOT NULL DEFAULT FALSE,
  last_login    TIMESTAMPTZ,
  created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  deleted_at    TIMESTAMPTZ                             -- soft delete
);

-- Licenses (no FK dependencies)
CREATE TABLE licenses (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  license_number  VARCHAR(50)    NOT NULL UNIQUE,
  operator_name   VARCHAR(200)   NOT NULL,
  contact_person  VARCHAR(120),
  contact_email   VARCHAR(200),
  category        license_cat    NOT NULL,
  sector          sector         NOT NULL,
  status          license_status NOT NULL DEFAULT 'pending',
  province        province       NOT NULL,
  issued_at       DATE           NOT NULL,
  expires_at      DATE           NOT NULL,
  annual_fee_rwf  BIGINT         NOT NULL DEFAULT 0,
  last_renewal_at DATE,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- Consumer Complaints (FK → users)
CREATE TABLE complaints (
  id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  reference_number     VARCHAR(50)        NOT NULL UNIQUE,
  subject              VARCHAR(300)       NOT NULL,
  category             complaint_category NOT NULL,
  complainant_name     VARCHAR(120)       NOT NULL,
  complainant_phone    VARCHAR(20),
  respondent_operator  VARCHAR(200)       NOT NULL,
  sector               sector             NOT NULL,
  province             province           NOT NULL,
  status               complaint_status   NOT NULL DEFAULT 'open',
  severity             complaint_severity NOT NULL DEFAULT 'medium',
  description          TEXT               NOT NULL,
  assigned_to_id       UUID REFERENCES users(id) ON DELETE SET NULL,
  filed_at             TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
  resolved_at          TIMESTAMPTZ
);

-- Compliance Records (FK → licenses, users)
CREATE TABLE compliance_records (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  operator_name   VARCHAR(200)      NOT NULL,
  license_id      UUID REFERENCES licenses(id) ON DELETE SET NULL,
  sector          sector            NOT NULL,
  check_type      compliance_check  NOT NULL,
  status          compliance_status NOT NULL DEFAULT 'under_review',
  due_date        DATE              NOT NULL,
  last_audit_date DATE,
  score           SMALLINT          CHECK (score BETWEEN 0 AND 100),
  auditor_id      UUID REFERENCES users(id) ON DELETE SET NULL,
  findings        TEXT,
  created_at      TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

-- Fraud Cases (FK → users)
CREATE TABLE fraud_cases (
  id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_number              VARCHAR(50)     NOT NULL UNIQUE,
  description              TEXT            NOT NULL,
  indicator_type           fraud_indicator NOT NULL,
  reported_by              VARCHAR(200)    NOT NULL,
  operator_involved        VARCHAR(200)    NOT NULL,
  sector                   sector          NOT NULL,
  risk_level               fraud_risk      NOT NULL DEFAULT 'medium',
  status                   fraud_status    NOT NULL DEFAULT 'open',
  reported_at              DATE            NOT NULL,
  estimated_loss_rwf       BIGINT          NOT NULL DEFAULT 0,
  investigating_officer_id UUID REFERENCES users(id) ON DELETE SET NULL,
  created_at               TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
  updated_at               TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- CLMS Cases (FK → users)
CREATE TABLE clms_cases (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_number      VARCHAR(50)   NOT NULL UNIQUE,
  title            VARCHAR(400)  NOT NULL,
  type             clms_type     NOT NULL,
  status           clms_status   NOT NULL DEFAULT 'draft',
  applicant_name   VARCHAR(200)  NOT NULL,
  applicant_email  VARCHAR(200),
  sector           sector        NOT NULL,
  province         province      NOT NULL,
  assigned_to_id   UUID REFERENCES users(id) ON DELETE SET NULL,
  notes            TEXT,
  submitted_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- CLMS Documents (FK → clms_cases, users)
CREATE TABLE clms_documents (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id        UUID          NOT NULL REFERENCES clms_cases(id) ON DELETE CASCADE,
  name           VARCHAR(300)  NOT NULL,
  stored_path    VARCHAR(500)  NOT NULL,
  size_kb        INTEGER       NOT NULL,
  uploaded_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
  uploaded_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Workflows (FK → users)
CREATE TABLE workflows (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name              VARCHAR(300)     NOT NULL,
  description       TEXT,
  trigger           workflow_trigger NOT NULL,
  status            workflow_status  NOT NULL DEFAULT 'draft',
  sector            VARCHAR(30)      NOT NULL,
  created_by_id     UUID REFERENCES users(id) ON DELETE SET NULL,
  related_entity_id VARCHAR(100),
  created_at        TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
  started_at        TIMESTAMPTZ,
  completed_at      TIMESTAMPTZ
);

-- Workflow Steps (FK → workflows, users)
CREATE TABLE workflow_steps (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  workflow_id     UUID                 NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
  step_order      SMALLINT             NOT NULL,
  name            VARCHAR(200)         NOT NULL,
  description     TEXT,
  assigned_role   user_role            NOT NULL,
  status          workflow_step_status NOT NULL DEFAULT 'pending',
  due_in_days     SMALLINT             NOT NULL,
  completed_at    TIMESTAMPTZ,
  completed_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
  UNIQUE (workflow_id, step_order)
);

-- Alerts (FK → users)
CREATE TABLE alerts (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  type              alert_type     NOT NULL,
  title             VARCHAR(300)   NOT NULL,
  message           TEXT           NOT NULL,
  severity          alert_severity NOT NULL DEFAULT 'info',
  status            alert_status   NOT NULL DEFAULT 'unread',
  related_module    audit_module   NOT NULL,
  related_entity_id VARCHAR(100),
  actioned_by_id    UUID REFERENCES users(id) ON DELETE SET NULL,
  created_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  read_at           TIMESTAMPTZ
);

-- Audit Log (append-only — FK → users via nullable reference)
CREATE TABLE audit_log (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id      UUID          REFERENCES users(id) ON DELETE SET NULL,
  user_name    VARCHAR(120)  NOT NULL,
  action       audit_action  NOT NULL,
  module       audit_module  NOT NULL,
  entity_id    VARCHAR(100)  NOT NULL,
  entity_label VARCHAR(400)  NOT NULL,
  ip_address   VARCHAR(45),
  user_agent   VARCHAR(500),
  changes      JSONB,
  timestamp    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Reports (FK → users)
CREATE TABLE reports (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title         VARCHAR(400)  NOT NULL,
  type          report_type   NOT NULL,
  sector        VARCHAR(50)   NOT NULL,
  status        report_status NOT NULL DEFAULT 'draft',
  format        report_format NOT NULL DEFAULT 'pdf',
  created_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
  period        VARCHAR(100)  NOT NULL,
  stored_path   VARCHAR(500),
  size_kb       INTEGER,
  created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  published_at  TIMESTAMPTZ
);
