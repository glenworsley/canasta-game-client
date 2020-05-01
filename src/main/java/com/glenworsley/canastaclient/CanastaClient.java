package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;

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
        uiWriter.write("Please enter the gamecode for the game you wish to join: ");
        uiWriter.flush();
        String gameCode = uiReader.readLine();
        uiWriter.write("\nPlease enter your name: ");
        uiWriter.flush();
        String playerName = uiReader.readLine();
        String joinGameMessage = buildJoinGameRequestJson(gameCode, playerName);
        String response = gameServerMessageHandler.sendMessageToServer(joinGameMessage);
        log.info("response from server: {}", response);
    }


    private String buildJoinGameRequestJson(String gamecode, String playerName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameCode", gamecode);
        jsonObject.put("playerName", playerName);
        return jsonObject.toString();
    }

}
