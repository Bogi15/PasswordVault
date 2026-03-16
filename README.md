# Password Vault

A secure **client-server password manager** built in Java. The server handles all password storage and validation logic while clients interact through a simple command-line interface.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Commands](#commands)
- [Security](#security)
- [API Integration](#api-integration)
- [Example Usage](#example-usage)

---

## Overview

Password Vault solves one of the most common cybersecurity mistakes — reusing weak passwords across multiple websites. It allows users to securely store, retrieve, and generate strong passwords, all protected behind their own account.

The application follows a **client-server architecture**:
- The **server** handles all business logic, storage, and security
- The **client** provides a command-line interface to interact with the server
- The server can handle **multiple clients simultaneously**

---

## Features

- ✅ User registration and authentication
- ✅ Secure password storage (hashed user passwords, encrypted site passwords)
- ✅ Strong password generation
- ✅ Add, retrieve and remove passwords per website
- ✅ Compromised password detection via [Enzoic API](https://www.enzoic.com/)
- ✅ Multi-client server support

---

## Project Structure

```
rePasswordVault/
├── src/
│   ├── client/         # Client-side logic and CLI
│   ├── server/         # Server logic, command handling
│   ├── commands/       # Command implementations
│   ├── crypto/         # Hashing and encryption utilities
│   └── storage/        # File-based persistence layer
├── test/               # Unit tests
├── storage/            # Runtime data (gitignored)
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 17+
- IntelliJ IDEA (recommended)
- An [Enzoic API key](https://www.enzoic.com/docs-password-security-api/) for compromised password checking

### Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/rePasswordVault.git
cd rePasswordVault
```

2. Open the project in IntelliJ IDEA

3. Add your Enzoic API credentials — create a `config.properties` file in the root:
```properties
enzoic.api_key=your_api_key
enzoic.api_secret=your_api_secret
```
> ⚠️ `config.properties` is gitignored — never commit your API keys

4. Run the **Server** first, then run the **Client**

---

## Commands

| Command | Description |
|--------|-------------|
| `register <user> <password> <password-repeat>` | Register a new account |
| `login <user> <password>` | Log into your account |
| `logout` | Log out of your account |
| `add-password <website> <user> <password>` | Save a password for a website |
| `generate-password <website> <user>` | Generate and save a strong password for a website |
| `retrieve-credentials <website> <user>` | Retrieve saved credentials for a website |
| `remove-password <website> <user>` | Remove saved credentials for a website |
| `disconnect` | Disconnect from the server |

---

## Security

| Data | Protection |
|------|-----------|
| User account passwords | Stored **hashed** on the server |
| Site passwords | Stored **encrypted** (reversible for retrieval) |
| Compromised password check | Checked via **Enzoic REST API** before saving |

> Passwords are never stored or transmitted in plain text.

---

## API Integration

Compromised password detection uses the [Enzoic Passwords API](https://www.enzoic.com/docs-password-security-api/).

When a user runs `add-password`, the server checks the password against known breach databases using SHA1, MD5, and SHA256 hashes before storing it.

**Example API request:**
```
POST /v1/passwords HTTP/1.1
Host: api.enzoic.com
Authorization: Basic username:password
Content-Type: application/json
Accept: */*
Content-Length: 30

{
  "partialSHA256": "9f86d08188"
}
```
**Example APR response:**
```
{
  "candidates": [
    {
      "sha256": "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
      "revealedInExposure": true,
      "exposureCount": 10
    }
  ]
}
```

If the password is found in a breach, the server rejects it and notifies the client.

---

## Example Usage

```
> register pesho peshopass peshopass
> Registration successful

> login pesho peshopass
> Login successful

> generate-password facebook.com pesho@gmail.com
> _rrR~S>k$[8+Ps/x2WyaFv

> retrieve-credentials facebook.com pesho@gmail.com
> _rrR~S>k$[8+Ps/x2WyaFv

> remove-password facebook.com pesho@gmail.com
> Password successfully removed

> logout
> Logout successful

> disconnect
> Disconnected
```

---

## 📌 Notes

- `storage/` and `users.dat` are **gitignored** — each instance maintains its own local data
- This project was built as a university assignment and is intended for educational purposes
