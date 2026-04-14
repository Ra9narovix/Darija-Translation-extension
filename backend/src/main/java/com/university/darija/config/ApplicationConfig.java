package com.university.darija.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({"USER"})
@BasicAuthenticationMechanismDefinition(realmName = "DarijaTranslatorRealm")
public class ApplicationConfig extends Application {
}
