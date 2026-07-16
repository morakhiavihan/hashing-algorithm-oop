# HashVault 🔐

HashVault is a Java-based desktop application demonstrating object-oriented programming (OOP) principles and secure password hashing concepts. It features a custom-built, dark-themed Java Swing UI that wraps a core authentication backend.

## ✨ Features

- **Secure Hashing:** Implements the `djb2` hashing algorithm for password encryption.
- **Salted Passwords:** Uses `SecureRandom` to generate unique cryptographic salts for each user, preventing rainbow table attacks.
- **Interactive UI:** A modern, glassmorphism-inspired dark theme built entirely from scratch using Java Swing custom painting (no external libraries).
- **Live Hash Visualizer:** As you type, the UI visualizes the `djb2` hash computation in real-time, complete with hex output and an entropy bit-bar.
- **File-Based Storage:** Simulates a database by securely storing the hashed password and salt in local `<username>.txt` files.

## 🛠️ Architecture (OOP Design)

The project cleanly separates the core logic from the user interface:

- `User.java`: Abstract base class defining common user properties and overloaded `hashPassword()` methods.
- `Authentication.java`: Interface defining the contract for `signIn()` and `signUp()` operations.
- `AuthenticatedUser.java`: Concrete implementation that handles the file I/O, salt generation, and hash comparison.
- `HashUI.java`: The interactive front-end that seamlessly wraps the backend logic without modifying its core structure.

## 🚀 How to Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/morakhiavihan/hashing-algorithm-oop.git
   cd "Hashing Algorithm OOP Innovative Assignment"
   ```
2. **Compile the Java files:**
   ```bash
   javac *.java
   ```
3. **Run the application:**
   ```bash
   java HashUI
   ```

## 📸 Screenshots

*(You can add screenshots of the UI here to make your repository stand out!)*

## 👨‍💻 Author

**Vihan Morakhia**
