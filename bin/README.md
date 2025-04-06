🎮 Crazy Eights Multiplayer Game (Java Swing)
A complete multiplayer implementation of the Crazy Eights card game using Java, Java Swing, and custom networking.

✅ Built with MVC architecture
✅ Play as host or join as a client
✅ Clean, interactive GUI with card visuals
✅ Chat system and localization (English & French)


📁 Project Structure
/src                -> Java source files (model, view, controller, network)
   ├── model        -> Game logic (Card, Deck, Game, Player)
   ├── view         -> UI classes (GameView, StartPage)
   ├── controller   -> GameController, Menu
   ├── network      -> Client, Server, Protocol, NetworkHandler, CustomDialog

/bin                -> Compiled classes and generated JAR  
/doc                -> Javadoc documentation  
/Assets             -> Game card images, background, splash screen  


🧠 How It Works
1. StartPage (Main Window)
   Shows a splash screen and prompts the user to host or join a game.

2. Networking Setup

	Host: Runs a server that waits for up to 4 clients.

	Client: Connects to a host using IP and port.

	Clients send their name to the server (JOIN#Name).

3. GameView (UI)
   Displays the playing area with:

	Player hands (graphical cards)

	Discard/draw piles

	Chat panel

	Status area (e.g., “Your Turn”)

4. GameController
   Handles:

	Turns and game logic (drawing, playing, changing suit)

	Receiving messages (chat, sync, win)

	Sending updates to server/other players

5. Protocol
   Handles message formatting between clients and server (e.g. CHAT#Player:Hello, SYNC#...).

⚙ How to Build and Run
✅ Option 1: Use the .bat Script (Recommended)
	File: F24_JAP_CompileScript.bat
		Double-click the .bat file
The script will:

1. Compile all .java files into /bin

2. Copy image assets and language files

3. Create an executable CrazyEights.jar

4. Run the game

5. Generate Javadoc in /doc

✅ Option 2: Run from Eclipse (Main Class)
Main Class: view.StartPage
Right-click → Run As → Java Application


🗂️ Requirements
	Java 17 or later for compiling

	Images must remain under Assets/ for the game to work correctly

	Up to 4 players can connect over LAN


🧪 Test Steps
	Run game, choose to host.

	On other clients, run and join using host’s IP and port.

	Game begins automatically once 4 players join.

	Each player:

		Plays valid cards or draws.

		Can chat using the right-side panel.

		Wins if they discard all cards.


🌐 Language Support
    Change language via menu:

	Game → Language → English/French

	Prompts user to restart app for full effect

Localized messages are stored in:
Messages_en.properties
Messages_fr.properties


🧾 License & Credits
This project was created for CST8116 – Java Applications Programming
Instructor: [Mustafa Jawish]
Semester: Fall 2025 – Algonquin College