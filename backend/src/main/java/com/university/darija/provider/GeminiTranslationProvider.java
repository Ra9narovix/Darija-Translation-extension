package com.university.darija.provider;

import com.university.darija.config.ConfigService;
import com.university.darija.exception.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ApplicationScoped
public class GeminiTranslationProvider implements TranslationProvider {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Inject
    private ConfigService configService;

    @Override
    public TranslationProviderResult translate(String text, String sourceLanguage, String targetLanguage) {
        try {
            String endpoint = buildEndpoint();
            String prompt = buildPrompt(text, sourceLanguage, targetLanguage);
            String requestBody = Json.createObjectBuilder()
                    .add("contents", Json.createArrayBuilder()
                            .add(Json.createObjectBuilder()
                                    .add("parts", Json.createArrayBuilder()
                                            .add(Json.createObjectBuilder().add("text", prompt)))))
                    .add("generationConfig", Json.createObjectBuilder()
                            .add("temperature", 0.3)
                            .add("maxOutputTokens", 120))
                    .build()
                    .toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                throw new AppException(502, "Gemini authentication failed",
                        "Check whether GEMINI_API_KEY is valid and enabled in Google AI Studio");
            }

            if (response.statusCode() == 429) {
                throw new AppException(503, "Gemini quota limit reached",
                        "Free-tier request limit exceeded. Retry later or check Google AI Studio limits");
            }

            if (response.statusCode() >= 400) {
                throw new AppException(502, "Gemini API error",
                        "Gemini returned HTTP " + response.statusCode() + ": " + response.body());
            }

            return new TranslationProviderResult(extractTranslatedText(response.body()), getProviderName());
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AppException(502, "Translation provider failure",
                    "Unable to contact Gemini free-tier endpoint: " + ex.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    private String buildEndpoint() {
        String baseUrl = configService.getGeminiBaseUrl();
        String model = configService.getGeminiModel();
        String apiKey = URLEncoder.encode(configService.getGeminiApiKey(), StandardCharsets.UTF_8);
        return baseUrl + "/models/" + model + ":generateContent?key=" + apiKey;
    }

    private String buildPrompt(String text, String sourceLanguage, String targetLanguage) {
        String detectedSource = sourceLanguage == null || sourceLanguage.isBlank() ? "auto-detect" : sourceLanguage;
        return """
                You are a translation engine.
                Translate the user text from %s to %s.
                Target must be natural Moroccan Darija used in daily conversation.
                Avoid Modern Standard Arabic unless the original text requires a formal expression.
                Return only the translated text with no explanation, no quotes, and no bullet points.

                Text:
                %s
                """.formatted(detectedSource, targetLanguage, text);
    }

    private String extractTranslatedText(String responseBody) {
        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            JsonObject root = reader.readObject();
            JsonArray candidates = root.getJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new AppException(502, "Gemini returned no translation", responseBody);
            }

            JsonObject firstCandidate = candidates.getJsonObject(0);
            JsonObject content = firstCandidate.getJsonObject("content");
            JsonArray parts = content.getJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                throw new AppException(502, "Gemini returned an empty translation", responseBody);
            }

            String translated = parts.getJsonObject(0).getString("text", "").trim();
            if (translated.isEmpty()) {
                throw new AppException(502, "Gemini returned blank translation", responseBody);
            }
            return translated;
        }
    }
}
