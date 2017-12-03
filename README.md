# BlackJacks

## Abstract

This project is an exercise of P2P structure to implement a running, prototyped, online-capable, "BlackJack" game.

## Authors

- Louis Sullivan
- Javier Ramirez-Moyano
- Matthew Schuch
- Brendon Murthum

## Project Description

The following is a description of the main features of this application (with a focus on showing the features of applied-P2P).

### Introduction

In this project, the user is guided through steps to be able to play a game of Blackjack with a friend. It takes the user through the process of connecting to the online-server that sends the client information of which games are available to connect to. The user then waits in a displayed general "Lobby" window that is central to the P2P function of the game. The user can then choose to (1) "host" a game that another player can connect to, or to (2) "join" a game of another player; either of these options call open a new "Game" window for playing the core game. After playing a game, the user is taken back to the "Lobby" window to play another game with another possible player.

### Notable Features

The following are features that either relate purposefully to the P2P-model, or otherwise notable.

- The central-server, our ```LobbyServer.java```, holds only information that allows for the viewing of other players names and validation of unique users. The rest of the application, the games' hosting and connections to running games is handled by peer-to-peer relationships.
- The client's initial typing-of-username is dynamically checked on client-side for character-limit and correct characters.
- The "Connect" button, within ```ConnectionGUI.java```, is only clickable upon (1) a communication-connection with the server, to only allow "connect-submission" when the server is running, and (2) a valid username is typed in.
- On disconnection of a single user, the lobby-display is updated dynamically.
- On disconnection or shutdown of the central-server, each user's lobby-display is reset to a "disconnected" state. The new ability to reconnect is available when the server is restarted.

### Structure

The following is a list of the used classes and how they relate to the other classes.

---

**Class Diagram**

![Class Diagram Image](https://github.com/bmmurthum/BlackJacks/blob/master/src/ClassDiagram.jpg)

--- 

**ConnectionGUI** - [Link](https://github.com/bmmurthum/BlackJacks/blob/master/src/ConnectionGUI.java) - ```ConnectionGUI.java```

This class is the initialized client-side application that calls to other classes for the capacity to further initialize games and connection to the central-server.

Uses Classes:
```ConnectionManager.java```
```popUpWindow.java```

---

**popUpWindow** - [Link](https://github.com/bmmurthum/BlackJacks/blob/master/src/popUpWindow.java) - ```popUpWindow.java```

This class is called when an error should be shown to the user. An example of this would be when the user types in an invalid username.

---

**ConnectionManager** - [Like](https://github.com/bmmurthum/BlackJacks/blob/master/src/ConnectionManager.java) - ```ConnectionManager.java```

This class handles variables related to current connection to the main server. It gives functions that return to the GUI. The functions include establishing initial communication with the central-server, sending a username to be validated as unique, initializing this client as waiting as a host-player to a game, grabbing and returning dynamic updates to the GUI, and smoothly disconnecting the user from the system.

Sends Information Over Socket To:
```LobbyHandler.java```

---

**LobbyServer** - [Link](https://github.com/bmmurthum/BlackJacks/blob/master/src/LobbyServer.java) - ```LobbyServer.java```

This class is the initialized server-side application that holds and manages the data that needs to be shared between all the active users.

Uses Classes:
```LobbyHandler.java```
```LobbyOnlinePlayers.java```

---

**LobbyHandler** - [Link](https://github.com/bmmurthum/BlackJacks/blob/master/src/LobbyHandler.java) - ```LobbyHandler.java```

This class is the middle-man between the client and the main, shared, data on the central-server. This class is threaded on the central-server and there is one instance running for each connected user. This class calls methods on the object of type "LobbyOnlinePlayers" to return a string over the connection to its particular client. It actively listens for commands from the running client to respond accordingly: For example, "GETUPDATE" cues "LobbyHandler" to shoot a string to the client that contains the current number of connected users, waiting users, and each hosting-players' username and open port.

Uses Classes:
```LobbyOnlinePlayers.java```

Sends Information Over Socket To:
```ConnectionManager.java```

---

**LobbyOnlinePlayers** - Link - ```LobbyOnlinePlayers.java```

This class allows for one single object that contains all the information on the central-server that each threaded ```LobbyHandler.java``` can call on for unified information. It has methods to (1) validate a username as unique, (2) to return the list of current hosting-users, (3) to display on the central-server terminal the current connected users, and (4) to manage adding and removing users from it's various lists dynamically.

The a pointer to the object "playerList" of this type, within ```LobbyServer.java```, is shared to each ```LobbyHandler.java``` at the handler's construction. There is one "playerList" that exists, it is created and exists within ```Lobbyserver.java```. 


