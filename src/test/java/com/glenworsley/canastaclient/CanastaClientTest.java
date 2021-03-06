package com.glenworsley.canastaclient;

import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This test is very messy and complicated.  It indicates some re-organizing is required.
 * Also think it would be better to go back to single (or small number of) assertions per test.
 */
@ActiveProfiles("ContextTest")
@SpringBootTest
class CanastaClientTest {

    @Mock
    private PrintWriter uiWriter;

    @Mock
    private BufferedReader uiReader;

    @Mock
    private ClientMessageHandler clientMessageHandler;

    @Mock
    private ServerMessageHandler serverMessageHandler;

    @InjectMocks
    private CanastaClient canastaClient;

    @Test
    void testGetGameCode() throws Exception {
        when(uiReader.readLine()).thenReturn("1","3");
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("12345");
        ArgumentCaptor<String> acUiMessages = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acGSMessages = ArgumentCaptor.forClass(String.class);
        canastaClient.run("");
        assertAll("verify messages sent to the console user",
                () -> verify(uiWriter, times(4)).println(acUiMessages.capture()),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(0)),
                () -> assertEquals("12345", acUiMessages.getAllValues().get(1)),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(2)),
                () -> assertEquals("Bye!", acUiMessages.getAllValues().get(3))
        );
        assertAll("game server interactions",
                () -> verify(clientMessageHandler).sendMessageToServer(acGSMessages.capture()),
                () -> assertEquals("{ \"event\": \"request_gameCode\" }", acGSMessages.getValue().toString())
        );
    }

    //For now, once you've joined, the game will "reset".
    @Test
    void testJoinGame() throws Exception {
        when(uiReader.readLine()).thenReturn("2", "12345", "bob", "3");
        ArgumentCaptor<String> acUiMessages = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acGSMessages = ArgumentCaptor.forClass(String.class);
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{ \"success\": true, \"players\": [ \"bob\", \"sam\" ] }");
        canastaClient.run("");
        assertAll("ui interactions",
                () -> verify(uiWriter, times(6)).println(acUiMessages.capture()),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(0)),
                () -> assertEquals("Please enter the gameCode: ", acUiMessages.getAllValues().get(1)),
                () -> assertEquals("Please enter your name: ", acUiMessages.getAllValues().get(2)),
                () -> assertEquals("Hi bob. Players are: bob, sam. Please wait for the game to start.", acUiMessages.getAllValues().get(3)),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(4)),
                () -> assertEquals("Bye!", acUiMessages.getAllValues().get(5))

        );
        assertAll(
                "join message sent to server",
                () -> verify(clientMessageHandler).sendMessageToServer((String) acGSMessages.capture()),
                () -> {
                    JSONObject messageJson = new JSONObject(acGSMessages.getValue().toString());
                    assertAll(
                            "message properties",
                            () -> assertEquals("join_game_request", messageJson.get("event")),
                            () -> assertEquals("12345", messageJson.get("gameCode")),
                            () -> assertEquals("bob", messageJson.get("playerName"))
                    );
                }
        );
    }

    @Test
    void testJoinGameShowsAllPlayers() throws Exception {
        when(uiReader.readLine()).thenReturn("2", "12345", "bob", "3");
        ArgumentCaptor<String> acUiMessages = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acGSMessages = ArgumentCaptor.forClass(String.class);
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{ \"success\": true, \"players\": [ \"bob\", \"sam\" ] }");
        canastaClient.run("");
        assertAll("ui interactions",
                () -> verify(uiWriter, times(6)).println(acUiMessages.capture()),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(0)),
                () -> assertEquals("Please enter the gameCode: ", acUiMessages.getAllValues().get(1)),
                () -> assertEquals("Please enter your name: ", acUiMessages.getAllValues().get(2)),
                () -> assertEquals("Hi bob. Players are: bob, sam. Please wait for the game to start.", acUiMessages.getAllValues().get(3))
        );
        assertAll(
                "join message sent to server",
                () -> verify(clientMessageHandler).sendMessageToServer((String) acGSMessages.capture()),
                () -> {
                    JSONObject messageJson = new JSONObject(acGSMessages.getValue().toString());
                    assertAll(
                            "message properties",
                            () -> assertEquals("12345", messageJson.get("gameCode")),
                            () -> assertEquals("bob", messageJson.get("playerName"))
                    );
                }
        );
    }

    @Test
    public void testQuit() throws Exception {
        when(uiReader.readLine()).thenReturn("3");
        canastaClient.run("");
        verify(uiWriter, times(2)).println(anyString());
    }

    @Test
    void testStartListeningForMessagesFromServerAfterJoiningGame() throws Exception {
        when(uiReader.readLine()).thenReturn("2", "12345", "bob", "3");
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{ \"success\": true, \"players\": [ \"bob\", \"sam\" ] }");
        canastaClient.run("");
        verify(serverMessageHandler).start();
    }



    @Test
    @Disabled //for now
    public void testUserIsNotifiedWhenNewPlayerJoinsTheGame() throws Exception {
        when(uiReader.readLine()).thenReturn("2", "12345", "bob", "3");
        ArgumentCaptor<String> acUiMessages = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> acGSMessages = ArgumentCaptor.forClass(String.class);
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{ \"success\": true, \"players\": [ \"bob\", \"sam\" ] }");
        String msg1 = "{ \"event\": \"player_joined\", \"playerName\": \"bill\" }";
        when(serverMessageHandler.getMessages()).thenReturn(Arrays.asList(new String[] { msg1 }));
        canastaClient.run("");
        verify(uiWriter, times(7)).println(acUiMessages.capture());
        List<String> uiMessages = acUiMessages.getAllValues();
        for (String message: uiMessages) {
            System.out.println(message);
        }
        assertAll("ui interactions",
                () -> verify(uiWriter, times(7)).println(acUiMessages.capture()),
                () -> assertEquals("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ", acUiMessages.getAllValues().get(0)),
                () -> assertEquals("Please enter the gameCode: ", acUiMessages.getAllValues().get(1)),
                () -> assertEquals("Please enter your name: ", acUiMessages.getAllValues().get(2)),
                () -> assertEquals("Hi bob. Players are: bob, sam. Please wait for the game to start.", acUiMessages.getAllValues().get(3)),
                () -> assertEquals("Event: New player joined.", acUiMessages.getAllValues().get(4))
        );
        assertAll(
                "join message sent to server",
                () -> verify(clientMessageHandler).sendMessageToServer((String) acGSMessages.capture()),
                () -> {
                    JSONObject messageJson = new JSONObject(acGSMessages.getValue().toString());
                    assertAll(
                            "message properties",
                            () -> assertEquals("12345", messageJson.get("gameCode")),
                            () -> assertEquals("bob", messageJson.get("playerName"))
                    );
                }
        );
    }

    private String playerJoinedEvent() {
        String json = "{ \"event\": \"player_joined\", \"players\": [ \"bob\", \"sam\", \"bill\" ] }";
        return json;
    }



}