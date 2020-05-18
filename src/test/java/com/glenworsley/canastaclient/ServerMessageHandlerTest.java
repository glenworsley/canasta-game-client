package com.glenworsley.canastaclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("ContextTest")
@SpringBootTest
class ServerMessageHandlerTest {

    @Mock
    private BufferedReader serverMessageReader;

    @Mock
    private PrintWriter uiOutputWriter;

    @Mock
    private GameState gameState;

    @InjectMocks
    private ServerMessageHandler serverMessageHandler;

    @Test
    void testStoresMessagesFromServer() throws IOException {
        when(serverMessageReader.readLine()).thenReturn("x", null);
        serverMessageHandler.run();
        assertTrue(!serverMessageHandler.getServerMessages().isEmpty());
    }

    @Test
    void testActionServerMessageWritesEventAndGameStateToUI() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();

        verify(uiOutputWriter, times(2)).println(anyString());
    }

    @Test
    void testActionServerMessagesClearsBuffer() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();

        assertTrue(serverMessageHandler.getServerMessages().isEmpty());
    }

    private String buildServerMessage() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "player_joined");
        jsonObject.put("gameState", "waiting_for_players");
        JSONArray playersArray = new JSONArray();
        playersArray.put("john");
        playersArray.put("sam");
        jsonObject.put("players", playersArray);
        return jsonObject.toString();
    }

    @Test
    void testYourTurnServerMessageUpdatesUI() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildYourTurnServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();

        verify(uiOutputWriter, times(3)).println(anyString());
    }

    @Test
    void testYourTurnServerMessagePromptsUserCorrectly() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildYourTurnServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();

        ArgumentCaptor<String> acUiOutput = ArgumentCaptor.forClass(String.class);
        verify(uiOutputWriter, times(3)).println(acUiOutput.capture());
        List<String> uiOutputLines = acUiOutput.getAllValues();
        assertEquals("Enter 1 to draw a card from the stock pile or 2 to draw from the discard pile:",
                uiOutputLines.get(2));
    }

    @Test
    void testYourTurnServerMessageSetsGameState() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildYourTurnServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();
        verify(gameState).setMyTurn(true);

    }

    @Test
    void testServerMessageSetsGameState() throws JSONException {
        List<String> messages = new ArrayList<>();
        messages.add(buildPlayerJoinedServerMessage());
        serverMessageHandler.setServerMessages(messages);

        serverMessageHandler.actionServerMessages();
        verify(gameState).setMyTurn(true);

    }

    private String buildPlayerJoinedServerMessage() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "player_joined");
        jsonObject.put("gameState", "waiting_for_players");
        JSONArray playersArray = new JSONArray();
        playersArray.put("john");
        playersArray.put("sam");
        playersArray.put("bill");
        jsonObject.put("players", playersArray);
        jsonObject.put("joinedPlayer", "bill");
        return jsonObject.toString();
    }

    private String buildYourTurnServerMessage() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "your_turn");
        jsonObject.put("gameState", "game_started");
        JSONArray playersArray = new JSONArray();
        playersArray.put("john");
        playersArray.put("sam");
        playersArray.put("bill");
        playersArray.put("bob");
        jsonObject.put("players", playersArray);
        return jsonObject.toString();
    }
}