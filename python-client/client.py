import argparse
import base64
import os
import sys

import requests


def build_headers(username: str, password: str) -> dict:
    token = base64.b64encode(f"{username}:{password}".encode("utf-8")).decode("utf-8")
    return {
        "Authorization": f"Basic {token}",
        "Content-Type": "application/json",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Translate text to Moroccan Darija.")
    parser.add_argument("text", nargs="?", help="Text to translate")
    parser.add_argument("--source-language", default="auto", help="Source language code or name")
    parser.add_argument("--url", default=os.getenv("TRANSLATOR_API_URL", "http://localhost:8080/darija-translator/api/translate"))
    parser.add_argument("--username", default=os.getenv("TRANSLATOR_USERNAME", "demo"))
    parser.add_argument("--password", default=os.getenv("TRANSLATOR_PASSWORD", "demo123"))
    args = parser.parse_args()

    text = args.text or input("Enter text: ").strip()
    if not text:
        print("Text must not be empty.", file=sys.stderr)
        return 1

    payload = {
        "text": text,
        "sourceLanguage": args.source_language,
        "targetLanguage": "darija",
    }

    try:
        response = requests.post(args.url, json=payload, headers=build_headers(args.username, args.password), timeout=30)
        data = response.json()
    except requests.RequestException as exc:
        print(f"Request failed: {exc}", file=sys.stderr)
        return 1
    except ValueError:
        print("Backend returned non-JSON response.", file=sys.stderr)
        return 1

    if not response.ok or not data.get("success"):
        print(data.get("details") or data.get("error") or "Translation failed.", file=sys.stderr)
        return 1

    print("Source:", data["sourceText"])
    print("Darija:", data["translatedText"])
    print("Provider:", data["provider"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
