package com.glenworsley.canastaclient;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("ContextTest")
@SpringBootTest
class GetGameCodeCommandTest {

    @Mock
    private ClientMessageHandler clientMessageHandler;

    @Mock
    private GameState gameState;

    @InjectMocks
    private GetGameCodeCommand getGameCodeCommand;

    @Test
    void testGetGameCodeCommandReturnsCode() throws IOException {

        when(clientMessageHandler.sendMessageToServer("{ \"event\": \"request_gameCode\" }")).thenReturn("XXXXX");
        String response = getGameCodeCommand.execute();
        assertTrue("XXXXX".equals(response));
    }

    @Test
    void testGetGameCodeCommandCallsServer() throws IOException {

        when(clientMessageHandler.sendMessageToServer("{ \"event\": \"request_gameCode\" }")).thenReturn("XXXXX");
        getGameCodeCommand.execute();
        verify(clientMessageHandler).sendMessageToServer(anyString());

    }

}