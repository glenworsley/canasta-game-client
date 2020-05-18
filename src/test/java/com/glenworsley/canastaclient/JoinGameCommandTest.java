package com.glenworsley.canastaclient;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("ContextTest")
@SpringBootTest
class JoinGameCommandTest {

    @Mock
    private PrintWriter uiOutputWriter;

    @Mock
    private BufferedReader uiInputReader;

    @Mock
    private ClientMessageHandler clientMessageHandler;

    @Mock
    private ServerMessageHandler serverMessageHandler;

    @Mock
    private GameState gameState;

    @InjectMocks
    private JoinGameCommand joinGameCommand;

    @Test
    void testJoinCommandReturnsSuccessResponse() throws IOException {
        String response = joinGameCommand.execute();
        assertEquals("Joined successfully", response);
    }

    @Test
    void testJoinCommandUpdatesState() throws IOException {
        when(uiInputReader.readLine()).thenReturn("XXXX","bob");
        joinGameCommand.execute();
        verify(gameState).setGameCode("XXXX");

    }

    @Test
    void testJoinCommandPromptsForGameCode() throws IOException {
        joinGameCommand.execute();
        verify(uiOutputWriter).println("Please enter the gameCode: ");
    }

    @Test
    void testJoinCommandSendsGameCodeAndPlayerNameToServer() throws IOException {
        when(uiInputReader.readLine()).thenReturn("XXXX","bob");
        joinGameCommand.execute();
        ArgumentCaptor<String> acMessageToServer = ArgumentCaptor.forClass(String.class);
        verify(clientMessageHandler).sendMessageToServer(acMessageToServer.capture());
        String messageToServer = acMessageToServer.getValue();
        assertThat(messageToServer.contains("XXXX"));
        assertThat(messageToServer.contains("bob"));
    }

    @Test
    void testJoinCommandTellsServerMessageHandlerToStart() throws IOException {
        joinGameCommand.execute();
        verify(serverMessageHandler).start();
    }
}