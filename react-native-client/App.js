import React, { useState } from "react";
import { SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";

const DEFAULT_URL = "http://10.0.2.2:8080/darija-translator/api/translate";

function encodeBase64(value) {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  let output = "";
  let index = 0;

  while (index < value.length) {
    const char1 = value.charCodeAt(index++);
    const char2 = value.charCodeAt(index++);
    const char3 = value.charCodeAt(index++);

    const enc1 = char1 >> 2;
    const enc2 = ((char1 & 3) << 4) | (char2 >> 4);
    let enc3 = ((char2 & 15) << 2) | (char3 >> 6);
    let enc4 = char3 & 63;

    if (Number.isNaN(char2)) {
      enc3 = 64;
      enc4 = 64;
    } else if (Number.isNaN(char3)) {
      enc4 = 64;
    }

    output += chars.charAt(enc1);
    output += chars.charAt(enc2);
    output += enc3 === 64 ? "=" : chars.charAt(enc3);
    output += enc4 === 64 ? "=" : chars.charAt(enc4);
  }

  return output;
}

export default function App() {
  const [backendUrl, setBackendUrl] = useState(DEFAULT_URL);
  const [username, setUsername] = useState("demo");
  const [password, setPassword] = useState("demo123");
  const [sourceText, setSourceText] = useState("");
  const [result, setResult] = useState("");
  const [status, setStatus] = useState("");

  const translate = async () => {
    if (!sourceText.trim()) {
      setStatus("Please enter some text first.");
      return;
    }

    setStatus("Translating...");
    setResult("");

    try {
      const response = await fetch(backendUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Basic " + encodeBase64(`${username}:${password}`)
        },
        body: JSON.stringify({
          text: sourceText,
          sourceLanguage: "auto",
          targetLanguage: "darija"
        })
      });

      const data = await response.json();
      if (!response.ok || !data.success) {
        throw new Error(data.details || data.error || "Translation failed");
      }

      setResult(data.translatedText);
      setStatus("Done.");
    } catch (error) {
      setStatus(error.message);
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" />
      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.hero}>
          <Text style={styles.eyebrow}>React Native Client</Text>
          <Text style={styles.title}>Darija Translator</Text>
          <Text style={styles.subtitle}>Red-and-blue mobile demo for the same secured backend API.</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.label}>Backend URL</Text>
          <TextInput style={styles.input} value={backendUrl} onChangeText={setBackendUrl} />

          <Text style={styles.label}>Username</Text>
          <TextInput style={styles.input} value={username} onChangeText={setUsername} />

          <Text style={styles.label}>Password</Text>
          <TextInput style={styles.input} value={password} onChangeText={setPassword} secureTextEntry />

          <Text style={styles.label}>Source Text</Text>
          <TextInput
            style={[styles.input, styles.textarea]}
            value={sourceText}
            onChangeText={setSourceText}
            multiline
            placeholder="Type text to translate..."
          />

          <TouchableOpacity style={styles.button} onPress={translate}>
            <Text style={styles.buttonText}>Translate to Darija</Text>
          </TouchableOpacity>

          <Text style={styles.status}>{status}</Text>

          <Text style={styles.label}>Translation</Text>
          <TextInput style={[styles.input, styles.textarea]} value={result} editable={false} multiline />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#0f4c81"
  },
  container: {
    padding: 20,
    backgroundColor: "#f5f8fc"
  },
  hero: {
    backgroundColor: "#c1121f",
    borderRadius: 24,
    padding: 22,
    shadowColor: "#0f4c81",
    shadowOpacity: 0.25,
    shadowRadius: 12,
    marginBottom: 18
  },
  eyebrow: {
    color: "#ffd6db",
    textTransform: "uppercase",
    letterSpacing: 1.5,
    marginBottom: 8
  },
  title: {
    color: "#ffffff",
    fontSize: 28,
    fontWeight: "700"
  },
  subtitle: {
    color: "#ffffff",
    marginTop: 6
  },
  card: {
    backgroundColor: "#ffffff",
    borderRadius: 20,
    padding: 18
  },
  label: {
    fontWeight: "600",
    color: "#17324f",
    marginTop: 8,
    marginBottom: 6
  },
  input: {
    borderWidth: 1,
    borderColor: "#cfd8e6",
    borderRadius: 14,
    padding: 12,
    backgroundColor: "#fbfdff"
  },
  textarea: {
    minHeight: 120,
    textAlignVertical: "top"
  },
  button: {
    marginTop: 18,
    backgroundColor: "#0f4c81",
    padding: 14,
    borderRadius: 14,
    alignItems: "center"
  },
  buttonText: {
    color: "#ffffff",
    fontWeight: "700"
  },
  status: {
    marginTop: 14,
    color: "#c1121f",
    minHeight: 20
  }
});
