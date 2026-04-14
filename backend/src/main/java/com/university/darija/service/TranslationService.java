package com.university.darija.service;

import com.university.darija.api.dto.TranslationRequest;
import com.university.darija.api.dto.TranslationResponse;
import com.university.darija.exception.AppException;
import com.university.darija.provider.TranslationProvider;
import com.university.darija.provider.TranslationProviderResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TranslationService {

    @Inject
    private TranslationProvider translationProvider;

    public TranslationResponse translate(TranslationRequest request) {
        validateRequest(request);

        String normalizedSource = normalizeLanguage(request.getSourceLanguage(), "auto");
        String normalizedTarget = normalizeTarget(request.getTargetLanguage());

        TranslationProviderResult providerResult = translationProvider.translate(
                request.getText().trim(),
                normalizedSource,
                normalizedTarget
        );

        return new TranslationResponse(
                true,
                request.getText().trim(),
                providerResult.getTranslatedText(),
                normalizedSource,
                normalizedTarget,
                providerResult.getProvider()
        );
    }

    private void validateRequest(TranslationRequest request) {
        if (request == null) {
            throw new AppException(400, "Invalid request", "Request body is required");
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            throw new AppException(400, "Invalid request", "Text must not be empty");
        }
    }

    private String normalizeLanguage(String language, String fallback) {
        return language == null || language.isBlank() ? fallback : language.trim().toLowerCase();
    }

    private String normalizeTarget(String targetLanguage) {
        String normalized = normalizeLanguage(targetLanguage, "darija");
        if (!"darija".equals(normalized) && !"moroccan-darija".equals(normalized)) {
            throw new AppException(400, "Invalid request", "Only Darija target translation is supported");
        }
        return "darija";
    }
}
