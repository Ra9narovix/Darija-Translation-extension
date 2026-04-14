package com.university.darija.api.resource;

import com.university.darija.api.dto.TranslationRequest;
import com.university.darija.api.dto.TranslationResponse;
import com.university.darija.service.TranslationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/translate")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TranslatorResource {

    @Inject
    private TranslationService translationService;

    @POST
    @RolesAllowed("USER")
    public Response translate(TranslationRequest request) {
        TranslationResponse response = translationService.translate(request);
        return Response.ok(response).build();
    }
}
