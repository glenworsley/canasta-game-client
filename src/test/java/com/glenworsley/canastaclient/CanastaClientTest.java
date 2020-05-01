package com.glenworsley.canastaclient;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("ContextTest")
@SpringBootTest
class CanastaClientTest {

    @Mock
    private BufferedWriter uiWriter;

    @Mock
    private BufferedReader uiReader;

    @Mock
    private GameServerMessageHandler gameServerMessageHandler;

    @InjectMocks
    private CanastaClient canastaClient;

    @Test
    void testJoinRequestSentAfterQuestionsAnswered() throws Exception {

        when(uiReader.readLine()).thenReturn("12345","bob");
        ArgumentCaptor acMessage = ArgumentCaptor.forClass(String.class);
        canastaClient.run("");
        verify(uiWriter, times(2)).write(anyString());
        assertAll(
                "join message",
                () -> verify(gameServerMessageHandler).sendMessageToServer((String) acMessage.capture()),
                () -> {
                    JSONObject messageJson = new JSONObject(acMessage.getValue().toString());
                    assertAll(
                            "message properties",
                            () -> assertEquals("12345", messageJson.get("gameCode")),
                            () -> assertEquals("bob", messageJson.get("playerName"))
                    );
                }
        );
    }
}