package com.booking.system.identity.application;

import com.booking.system.shared.config.RsaKeyHelper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Profile({"dev", "test"})
public class DevJwtService {

    private final RsaKeyHelper rsaKeyHelper;

    public DevJwtService(RsaKeyHelper rsaKeyHelper) {
        this.rsaKeyHelper = rsaKeyHelper;
    }

    public String generateToken(String username, String role) {
        try {
            JWSSigner signer = new RSASSASigner(rsaKeyHelper.getPrivateKey());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiration
                    .claim("roles", List.of(role))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate and sign dev JWT", e);
        }
    }
}
