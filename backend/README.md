# Backend

Jakarta RESTful Web Service secured with Basic Authentication and powered by the Google Gemini Developer API free tier.

## Selected Gemini configuration

- Model: `gemini-2.5-flash-lite`
- Endpoint: `POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=YOUR_KEY`
- API key source: `GEMINI_API_KEY`

## Configure environment

```powershell
$env:GEMINI_API_KEY="your_google_ai_studio_key"
$env:APP_BASIC_AUTH_USERNAME="demo"
$env:APP_BASIC_AUTH_PASSWORD="demo123"
```

## Run

```bash
mvn clean package
mvn payara-micro:start
```

## Main endpoints

- `GET /darija-translator/api/health`
- `POST /darija-translator/api/translate`
