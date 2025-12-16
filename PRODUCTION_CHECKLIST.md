
# Production Readiness Checklist

Owner: Julian Parodi
Release version/tag: <v1.0.0>  
Verified by: Julian Parodi
Date: <2025-12-16>

---

## 1. Code Quality & Tests

- [ ] Unit tests executed and passing (CI)
- [ ] Integration tests executed and passing
- [ ] Code coverage ≥ 80%
- [ ] Linting and static analysis checks pass

---

## 2. Security & Dependencies

- [ ] Dependency scan completed (no CRITICAL vulnerabilities)
- [ ] SAST completed with no blocking issues
- [ ] Container image scanned (Trivy or equivalent)
- [ ] No secrets committed to the repository
- [ ] Security review completed for auth/authorization logic
- [ ] SSL verification with certificates completed

---

## 3. CI/CD & Releases

- [ ] CI pipeline green
- [ ] Docker image built and tagged immutably
- [ ] Release notes prepared
- [ ] Rollback strategy documented

---

## 4. Infrastructure & Configuration

- [ ] Configuration separated per environment
- [ ] Secrets stored in secure secret manager
- [ ] Database migrations backward-compatible
- [ ] Migration tested in staging-like environment

---

## 5. Observability & Monitoring

- [ ] Structured logs enabled
- [ ] Metrics collected (latency, error rate, throughput)
- [ ] Tracing enabled and sampled
- [ ] Dashboards defined

---

## 6. Performance & Capacity

- [ ] Load test executed (baseline + buffer)
- [ ] Performance targets met
- [ ] Capacity assumptions documented

---

## 7. Operational Readiness

- [ ] Runbooks created for common incidents
- [ ] Backup and restore procedure tested
- [ ] RTO / RPO validated

---

## 8. Compliance & Legal

- [ ] License compatibility verified
- [ ] Data handling complies with GDPR (if applicable)
- [ ] Audit logs enabled if required

---

## 9. Release Approval

- [ ] Product owner sign-off
- [ ] Security sign-off (if applicable)
- [ ] Release window scheduled

---

## 10. Post-deploy Verification

- [ ] Smoke tests executed
- [ ] Key dashboards verified
- [ ] Post-deploy review scheduled

---

## Commands & Tools (examples)

- Run unit tests:
  mvn -B -DskipTests=false test

- Run coverage:
  mvn jacoco:report

- Dependency scan:
  Dependabot / Snyk

- Container scan:
  trivy image <image>

- Load test:
  k6 run --vus 100 --duration 5m script.js

---

## Notes

- Any waived item must include justification and compensating controls.
- Prefer automation over manual verification wherever possible.
