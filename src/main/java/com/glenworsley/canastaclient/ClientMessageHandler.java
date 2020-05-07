package com.glenworsley.canastaclient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
@RequiredArgsConstructor
@Component
@Data //for testing
public class ClientMessageHandler {

    private final PrintWriter playerMessageWriter;
    private final BufferedReader playerMessageReader;

    //will block waiting for a response
    public String sendMessageToServer(String message) throws IOException {
        log.info("sending message to server");
        playerMessageWriter.println(message);
        String response = playerMessageReader.readLine();
        log.info("response: {}", response);
        return response;
    }

}
