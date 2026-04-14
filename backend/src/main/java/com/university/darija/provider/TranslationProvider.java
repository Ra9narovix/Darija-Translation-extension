package com.university.darija.provider;

public interface TranslationProvider {

    TranslationProviderResult translate(String text, String sourceLanguage, String targetLanguage);

    String getProviderName();
}
