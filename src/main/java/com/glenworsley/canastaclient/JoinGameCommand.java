package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
@Component
@RequiredArgsConstructor
public class JoinGameCommand implements UICommand {

    private final PrintWriter uiOutputWriter;
    private final BufferedReader uiInputReader;
    private final ClientMessageHandler clientMessageHandler;
    private final ServerMessageHandler serverMessageHandler;
    private final GameState gameState;


    @SneakyThrows
    @Override
    public String execute() {
        log.info("received request to join a game");
        String gameCode = getGameCodeFromUser();
        String playerName = getNameFromUser();
        String joinGameMessage = buildJoinGameRequestMessage(gameCode, playerName);
        String response = clientMessageHandler.sendMessageToServer(joinGameMessage);
        log.info("response from server: {}", response);
        gameState.setGameCode(gameCode);
        gameState.setPlayerName(playerName);
        startListeningForServerMessages();
        return "Joined successfully";
    }

    private String getNameFromUser() throws IOException {
        uiOutputWriter.println("Please enter your name: ");
        return uiInputReader.readLine();
    }

    private String getGameCodeFromUser() throws IOException {
        uiOutputWriter.println("Please enter the gameCode: ");
        return uiInputReader.readLine();
    }

    private void startListeningForServerMessages() {
        serverMessageHandler.start();
    }

    private String buildJoinGameRequestMessage(String gamecode, String playerName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "join_game_request");
        jsonObject.put("gameCode", gamecode);
        jsonObject.put("playerName", playerName);
        return jsonObject.toString();
    }
}
