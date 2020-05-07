package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Profile("!ContextTest")
@Component
@RequiredArgsConstructor
@Log4j2
public class CanastaClient implements CommandLineRunner {

    private final ClientMessageHandler clientMessageHandler;
    private final ServerMessageHandler serverMessageHandler;
    private final BufferedReader uiInputReader;
    private final PrintWriter uiOutputWriter;

    private boolean listeningForServerMessages = false;

    @Override
    public void run(String... args) throws Exception {
        uiOutputWriter.println("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ");
        String uiCommand;
        while ((uiCommand = uiInputReader.readLine()) != null) {
            actionUICommand(uiCommand);
            if ("3".equals(uiCommand)) break; //quit
            if (listeningForServerMessages)
                actionServerCommands();
            //prompt user for next command
            uiOutputWriter.println("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ");
        }
    }

    private void actionServerCommands() {
        List<String> messages = serverMessageHandler.getMessages();
        for (String message : messages) {
            //action the message
            log.info("actioning server message: {}", message);
            JSONObject jsonObject = new JSONObject(message);
            if ("player_joined".equals(jsonObject.get("event"))) {
                String playerName = jsonObject.getString("playerName");
                uiOutputWriter.println("Event: New player joined.");
                //uiWriter.println("Players are: " + getPlayerNames());
                JSONObject gameState = jsonObject.getJSONObject("gameState");
                uiOutputWriter.println("Game state = " + gameState);
            }
        }
    }

    private void actionUICommand(String uiCommand) throws IOException {
        log.info("actioning command: {}", uiCommand);
        if ("1".equals(uiCommand)) {
            log.info("in gamecode request branch");
            gameCodeRequest();
        } else {
            if ("2".equals(uiCommand)) {
                joinGameRequest();
            } else {
                uiOutputWriter.println("Bye!");
                uiOutputWriter.close();
                uiInputReader.close();
            }
        }
    }

    private void joinGameRequest() throws IOException {
        log.info("received request to join a game");
        uiOutputWriter.println("Please enter the gameCode: ");
        String gameCode = uiInputReader.readLine();
        uiOutputWriter.println("Please enter your name: ");
        String playerName = uiInputReader.readLine();
        String joinGameMessage = buildJoinGameRequestJson(gameCode, playerName);
        String response = clientMessageHandler.sendMessageToServer(joinGameMessage);
        log.info("response from server: {}", response);
        String playerList = getPlayerListFrom(response);
        uiOutputWriter.println("Hi " + playerName + ". Players are: " + playerList + ". Please wait for the game to start.");
        startListeningForServerMessages();
    }

    private void startListeningForServerMessages() {
        serverMessageHandler.start();
        listeningForServerMessages = true;
    }

    private String getPlayerListFrom(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        StringBuilder sb = new StringBuilder(jsonArray.getString(0)); //assume at least current player returned
        for (int i = 1; i < jsonArray.length(); i++) {
            sb.append(", ").append(jsonArray.get(i));
        }
        return sb.toString();
    }

    private void gameCodeRequest() throws IOException {
        log.info("received request to get a new gameCode");
        String gameCode = clientMessageHandler.sendMessageToServer("{ \"event\": \"request_gameCode\" }");
        uiOutputWriter.println(gameCode);
    }


    private String buildJoinGameRequestJson(String gamecode, String playerName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "join_game_request");
        jsonObject.put("gameCode", gamecode);
        jsonObject.put("playerName", playerName);
        return jsonObject.toString();
    }

}
