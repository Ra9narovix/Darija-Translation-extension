package com.university.darija.security;

import com.university.darija.config.ConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.BasicAuthenticationCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.Set;

@ApplicationScoped
public class DemoIdentityStore implements IdentityStore {

    @Inject
    private ConfigService configService;

    @Override
    public CredentialValidationResult validate(jakarta.security.enterprise.credential.Credential credential) {
        if (!(credential instanceof BasicAuthenticationCredential basicCredential)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        String configuredUsername = configService.getAuthUsername();
        String configuredPassword = configService.getAuthPassword();

        if (configuredUsername.equals(basicCredential.getCaller()) &&
                configuredPassword.equals(basicCredential.getPasswordAsString())) {
            return new CredentialValidationResult(configuredUsername, Set.of("USER"));
        }

        return CredentialValidationResult.INVALID_RESULT;
    }
}
