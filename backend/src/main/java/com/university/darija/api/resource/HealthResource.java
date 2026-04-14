package com.university.darija.api.resource;

import com.university.darija.api.dto.HealthResponse;
import com.university.darija.config.ConfigService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @Inject
    private ConfigService configService;

    @GET
    public Response health() {
        HealthResponse response = new HealthResponse(
                true,
                "UP",
                "gemini",
                configService.getGeminiModel()
        );
        return Response.ok(response).build();
    }
}
