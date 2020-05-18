package com.glenworsley.canastaclient;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Component
@Data
/**
 * When started, will listen for messages from the server and any messages received are kept in the serverMessages list.
 * When these messages are retrieved for processing, the list is cleared.
 */
public class ServerMessageHandler extends Thread {

    private final BufferedReader serverMessageReader;
    private final PrintWriter uiOutputWriter;
    private final GameState gameState;

    private List<String> serverMessages = new ArrayList<>();

    @SneakyThrows
    public void run() {
        log.info("listening for messages from game server");
        String messageFromServer;
        while ((messageFromServer = serverMessageReader.readLine()) != null) {
            log.info("received message from server: {}", messageFromServer);
            serverMessages.add(messageFromServer);
        }
    }

    //TODO: may need some synchronization to ensure no messages are lost?
    public void actionServerMessages() {
        for (String message : serverMessages) {
            log.info("message = " + message);
            JSONObject serverMessage = new JSONObject(message);
            if ("your_turn".equals(serverMessage.get("event"))) {
                uiOutputWriter.println("Your turn!");
                uiOutputWriter.println("Game State: " + serverMessage.get("gameState") + ", players: " + serverMessage.get("players"));
                uiOutputWriter.println("Enter 1 to draw a card from the stock pile or 2 to draw from the discard pile:");
                //change state
                gameState.setMyTurn(true);
            }
            else {
                uiOutputWriter.println("Event: " + serverMessage.get("event"));
                uiOutputWriter.println("Game State: " + serverMessage.get("gameState") + ", players: " + serverMessage.get("players"));
            }
            log.info(serverMessage.get("gameState"));
        }
        serverMessages.clear();
    }
}
