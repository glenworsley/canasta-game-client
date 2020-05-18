package com.glenworsley.canastaclient;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("ContextTest")
@SpringBootTest
class UICommandInterpreterImplTest {

    @Mock
    private GetGameCodeCommand getGameCodeCommand;

    @Mock
    private JoinGameCommand joinGameCommand;

    @Mock
    private ExitCommand exitCommand;

    @Mock
    private DrawStockCardCommand drawStockCardCommand;

    @Mock
    private GameState gameState;

    @InjectMocks
    UICommandInterpreterImpl uiCommandInterpreter;

    @Test
    void testGetGameCommandRecognized() {

        when(gameState.isMyTurn()).thenReturn(false);
        UICommand uiCommand = uiCommandInterpreter.getCommand("1");
        assertTrue(uiCommand instanceof GetGameCodeCommand);
    }

    @Test
    void testJoinCommandRecognized() {

        UICommand uiCommand = uiCommandInterpreter.getCommand("2");
        assertTrue(uiCommand instanceof JoinGameCommand);
    }

    @Test
    void testQuitCommandRecognized() {

        UICommand uiCommand = uiCommandInterpreter.getCommand("3");
        assertTrue(uiCommand instanceof ExitCommand);
    }

    @Test
    void testDrawStockCardCommandRecognized() {

        when(gameState.isMyTurn()).thenReturn(true);
        UICommand uiCommand = uiCommandInterpreter.getCommand("1");
        assertTrue(uiCommand instanceof DrawStockCardCommand);
    }
}