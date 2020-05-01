package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Profile("!ContextTest")
@Component
@Log4j2
@RequiredArgsConstructor
public class CanastaClient implements CommandLineRunner {

    private final GameServerMessageHandler gameServerMessageHandler;
    private final BufferedReader uiReader;
    private final BufferedWriter uiWriter;

    @Override
    public void run(String... args) throws Exception {
        log.info("starting client");
        gameServerMessageHandler.start();

        boolean quit = false;
        while (handleNextCommand()) {
            //
        }
    }

    private boolean handleNextCommand() throws IOException {
        writeMessageToUI("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ");
        String command = uiReader.readLine();
        if ("1".equals(command)) {
            gameCodeRequest();
        } else {
            if ("2".equals(command)) {
                joinGameRequest();
            } else {
                writeMessageToUI("Bye!");
                uiWriter.close();
                uiReader.close();
                return false;
            }
        }
        return true;
    }

    private void joinGameRequest() throws IOException {
        log.info("received request to join a game");
        writeMessageToUI("Please enter the gameCode: ");
        String gameCode = uiReader.readLine();
        writeMessageToUI("Please enter your name: ");
        String playerName = uiReader.readLine();
        String joinGameMessage = buildJoinGameRequestJson(gameCode, playerName);
        String response = gameServerMessageHandler.sendMessageToServer(joinGameMessage);
        log.info("response from server: {}", response);
        String playerList = getPlayerListFrom(response);
        uiWriter.write("Hi " + playerName + ". Players are: " + playerList + ". Please wait for the game to start.");
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
        String gameCode = gameServerMessageHandler.sendMessageToServer("{ \"event\": \"request_gameCode\" }");
        writeMessageToUI(gameCode);
    }

    private void writeMessageToUI(String gameCode) throws IOException {
        uiWriter.write(gameCode);
        uiWriter.flush();
    }


    private String buildJoinGameRequestJson(String gamecode, String playerName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameCode", gamecode);
        jsonObject.put("playerName", playerName);
        return jsonObject.toString();
    }

}
