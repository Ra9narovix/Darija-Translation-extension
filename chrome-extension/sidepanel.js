const backendUrlInput = document.getElementById("backendUrl");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const sourceTextInput = document.getElementById("sourceText");
const translatedTextInput = document.getElementById("translatedText");
const statusMessage = document.getElementById("statusMessage");
const configStatus = document.getElementById("configStatus");

async function loadConfig() {
  const stored = await chrome.storage.local.get([
    "backendUrl",
    "username",
    "password"
  ]);

  backendUrlInput.value = stored.backendUrl || "http://localhost:8080/darija-translator/api/translate";
  usernameInput.value = stored.username || "demo";
  passwordInput.value = stored.password || "demo123";
}

async function loadSelectedText() {
  const response = await chrome.runtime.sendMessage({ type: "GET_SELECTED_TEXT" });
  if (response?.text) {
    sourceTextInput.value = response.text;
  }
}

async function saveConfig() {
  await chrome.storage.local.set({
    backendUrl: backendUrlInput.value.trim(),
    username: usernameInput.value.trim(),
    password: passwordInput.value
  });
  configStatus.textContent = "Connection settings saved.";
}

function setStatus(message, isError = false) {
  statusMessage.textContent = message;
  statusMessage.className = isError ? "status error" : "status";
}

async function translateText() {
  const text = sourceTextInput.value.trim();
  if (!text) {
    setStatus("Please enter or select some text first.", true);
    return;
  }

  const backendUrl = backendUrlInput.value.trim();
  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  setStatus("Translating...");
  translatedTextInput.value = "";

  try {
    const response = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Basic " + btoa(`${username}:${password}`)
      },
      body: JSON.stringify({
        text,
        sourceLanguage: "auto",
        targetLanguage: "darija"
      })
    });

    const data = await response.json();
    if (!response.ok || !data.success) {
      throw new Error(data.details || data.error || "Translation failed");
    }

    translatedTextInput.value = data.translatedText;
    setStatus("Translation ready.");
  } catch (error) {
    setStatus(error.message, true);
  }
}

function copyResult() {
  const text = translatedTextInput.value.trim();
  if (!text) {
    setStatus("Nothing to copy yet.", true);
    return;
  }
  navigator.clipboard.writeText(text);
  setStatus("Translation copied.");
}

function readAloud() {
  const text = translatedTextInput.value.trim();
  if (!text) {
    setStatus("Translate something first to use read aloud.", true);
    return;
  }

  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = "ar-MA";
  speechSynthesis.speak(utterance);
}

function startVoiceInput() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) {
    setStatus("Speech recognition is not supported in this Chrome build.", true);
    return;
  }

  const recognition = new SpeechRecognition();
  recognition.lang = "en-US";
  recognition.start();
  setStatus("Listening...");

  recognition.onresult = (event) => {
    sourceTextInput.value = event.results[0][0].transcript;
    setStatus("Voice input captured.");
  };

  recognition.onerror = () => setStatus("Voice input failed.", true);
}

document.getElementById("saveConfigBtn").addEventListener("click", saveConfig);
document.getElementById("translateBtn").addEventListener("click", translateText);
document.getElementById("copyBtn").addEventListener("click", copyResult);
document.getElementById("listenBtn").addEventListener("click", readAloud);
document.getElementById("speechBtn").addEventListener("click", startVoiceInput);

loadConfig();
loadSelectedText();
