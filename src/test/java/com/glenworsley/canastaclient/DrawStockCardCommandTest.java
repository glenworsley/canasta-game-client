package com.glenworsley.canastaclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("ContextTest")
@SpringBootTest
class DrawStockCardCommandTest {

    @Mock
    private GameState gameState;

    @Mock
    private ClientMessageHandler clientMessageHandler;

    @InjectMocks
    private DrawStockCardCommand drawStockCardCommand;

    @Test
    void testSendsDrawStockCardMessageToServer() throws IOException, JSONException {

        when(gameState.getGameCode()).thenReturn("XXXX");
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{\"drawnCard\": \"QS\"}");

        drawStockCardCommand.execute();
        ArgumentCaptor<String> acMessageToServer = ArgumentCaptor.forClass(String.class);
        verify(clientMessageHandler).sendMessageToServer(acMessageToServer.capture());
        String messageToServer = acMessageToServer.getValue();
        JSONObject jsonMessage = new JSONObject(messageToServer);
        assertEquals("draw_stock_card", jsonMessage.getString("event"));
        assertEquals("XXXX", jsonMessage.getString("gameCode"));
    }

    @Test
    void testDrawnCardIsAddedToPlayersHand() throws IOException, JSONException {

        when(gameState.getGameCode()).thenReturn("XXXX");
        when(clientMessageHandler.sendMessageToServer(anyString())).thenReturn("{\"drawnCard\": \"QS\"}");
        drawStockCardCommand.execute();
        verify(gameState).addCardToHand("QS");
    }
}