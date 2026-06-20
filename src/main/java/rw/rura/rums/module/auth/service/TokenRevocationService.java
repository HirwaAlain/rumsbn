package rw.rura.rums.module.auth.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * In-memory store of revoked token JTIs.
 * Sufficient for single-node deployments; swap for a Redis-backed implementation
 * in a multi-node environment.
 */
@Service
public class TokenRevocationService {

    private final Set<String> revokedJtis = Collections.synchronizedSet(new HashSet<>());

    public void revoke(String jti) {
        revokedJtis.add(jti);
    }

    public boolean isRevoked(String jti) {
        return jti != null && revokedJtis.contains(jti);
    }
}
