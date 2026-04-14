package com.university.darija.provider;

public class TranslationProviderResult {
    private final String translatedText;
    private final String provider;

    public TranslationProviderResult(String translatedText, String provider) {
        this.translatedText = translatedText;
        this.provider = provider;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public String getProvider() {
        return provider;
    }
}
