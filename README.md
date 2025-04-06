🎴 **Crazy Eights - Multiplayer Java Game**
A full-featured multiplayer implementation of the **Crazy Eights** card game using Java and Swing GUI, designed with MVC architecture and network support (client/server). Built as part of CST8116 Java Application Programming coursework at **Algonquin College**.

---

## 📸 Screenshots

### 🔹 Single Player View  
![Single Player](Single%20Player%20interface.png)

### 🔹 4-Player Game View  
![Multiplayer](4%20Players%20interface.png)

---

## 🚀 Features

- 🖥️ Clean Swing-based graphical interface
- 🎴 52-card deck with smooth visuals
- 🌐 Host or join multiplayer games via local network
- 💬 Real-time chat between players
- 🌍 Language support: English 🇬🇧 and French 🇫🇷
- 🗂️ Organized MVC structure with full Javadoc documentation
- ✅ Batch script to compile, document, and package into an executable JAR

---

## 🗂️ Project Structure

```bash
.
├── Assets/                # Card images & background
├── bin/                  # Compiled classes & .jar
├── doc/                  # Javadoc HTML output
├── src/                  # Java source files
├── F24_JAP_CompileScript.bat  # Build/run script
├── README.md
└── CrazyEights.jar       # Executable JAR
```

🧠 How It Works
- StartPage.java: Launches GUI with Host/Join options.
- GameController.java: Handles game logic and syncs between model/view/network.
- GameView.java: Displays cards, players, draw pile, and chat area.
- NetworkHandler.java: Handles TCP socket communication between host and clients.
- Game.java: Maintains overall state (deck, turns, player hands).
- Card.java / Deck.java: Represents individual cards and card deck logic.

🔧 Requirements
Java 17+ (compile with --release 17)

No external libraries required

Windows (batch file tested), but Java is cross-platform

🛠️ Building & Running
📦 Option 1: Use the Batch File
        F24_JAP_CompileScript.bat
- Compiles source → bin/
- Generates Javadoc → doc/
- Creates executable → bin/CrazyEights.jar
- Launches the game!

🖱️ Option 2: Run JAR directly
        java -jar bin/CrazyEights.jar
        
📄 License
This project was built for academic purposes and is free to use for learning or portfolio work.

🙋‍♂️ Author
Mustafa Jawish
📘 GitHub: Mustafa22J
📧 Contact: ender201619@gmail.com
