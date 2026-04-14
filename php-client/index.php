<?php
$backendUrl = getenv('TRANSLATOR_API_URL') ?: 'http://localhost:8080/darija-translator/api/translate';
$username = getenv('TRANSLATOR_USERNAME') ?: 'demo';
$password = getenv('TRANSLATOR_PASSWORD') ?: 'demo123';

$sourceText = $_POST['text'] ?? '';
$translation = '';
$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (trim($sourceText) === '') {
        $error = 'Text must not be empty.';
    } else {
        $payload = json_encode([
            'text' => $sourceText,
            'sourceLanguage' => $_POST['sourceLanguage'] ?? 'auto',
            'targetLanguage' => 'darija'
        ]);

        $ch = curl_init($backendUrl);
        curl_setopt_array($ch, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => true,
            CURLOPT_POSTFIELDS => $payload,
            CURLOPT_HTTPHEADER => [
                'Content-Type: application/json',
                'Authorization: Basic ' . base64_encode($username . ':' . $password)
            ]
        ]);

        $response = curl_exec($ch);
        $statusCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);
        curl_close($ch);

        if ($curlError) {
            $error = 'Request failed: ' . $curlError;
        } else {
            $data = json_decode($response, true);
            if ($statusCode >= 400 || empty($data['success'])) {
                $error = $data['details'] ?? $data['error'] ?? 'Translation failed.';
            } else {
                $translation = $data['translatedText'] ?? '';
            }
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Darija Translator PHP Client</title>
    <style>
        :root {
            --morocco-red: #c1121f;
            --morocco-blue: #0f4c81;
            --paper: #f4f8fd;
            --ink: #17263c;
        }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(180deg, #fff9fa, var(--paper));
            color: var(--ink);
        }
        .container {
            max-width: 860px;
            margin: 40px auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 18px 50px rgba(15, 76, 129, 0.16);
            overflow: hidden;
        }
        .hero {
            padding: 28px;
            color: white;
            background: linear-gradient(135deg, var(--morocco-red), var(--morocco-blue));
        }
        .content {
            padding: 28px;
        }
        textarea, input, button {
            width: 100%;
            margin-top: 10px;
            margin-bottom: 16px;
            padding: 12px;
            border-radius: 12px;
            border: 1px solid #ccd8e8;
            font-size: 15px;
        }
        button {
            background: var(--morocco-red);
            color: white;
            border: none;
            font-weight: bold;
            cursor: pointer;
        }
        .result {
            border-left: 5px solid var(--morocco-blue);
            background: #f8fbff;
            padding: 18px;
            border-radius: 14px;
        }
        .error {
            color: var(--morocco-red);
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="hero">
            <h1>Darija Translator</h1>
            <p>PHP client for the secured Gemini-powered backend.</p>
        </div>
        <div class="content">
            <form method="post">
                <label for="text">Text</label>
                <textarea id="text" name="text" rows="6" placeholder="Enter text to translate..."><?= htmlspecialchars($sourceText) ?></textarea>

                <label for="sourceLanguage">Source Language</label>
                <input id="sourceLanguage" name="sourceLanguage" type="text" value="<?= htmlspecialchars($_POST['sourceLanguage'] ?? 'en') ?>">

                <button type="submit">Translate to Darija</button>
            </form>

            <?php if ($error): ?>
                <p class="error"><?= htmlspecialchars($error) ?></p>
            <?php endif; ?>

            <?php if ($translation): ?>
                <div class="result">
                    <h2>Translation</h2>
                    <p><?= htmlspecialchars($translation) ?></p>
                </div>
            <?php endif; ?>
        </div>
    </div>
</body>
</html>
