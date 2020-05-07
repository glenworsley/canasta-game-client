package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Component
/**
 * When started, will listen for messages from the server and any messages received are kept in the serverMessages list.
 * When these messages are retrieved for processing, the list is cleared.
 */
public class ServerMessageHandler extends Thread {

    private final BufferedReader serverMessageReader;

    private List<String> serverMessages = new ArrayList<>();

    //TODO: handle exception properly
    @SneakyThrows
    public void run() {
        log.info("listening for messages from game server");
        String messageFromServer;
        while ((messageFromServer = serverMessageReader.readLine()) != null) {
            log.info("received message from server: {}", messageFromServer);
            serverMessages.add(messageFromServer);
        }
     }


    //return a copy of the messages in the buffer & clear the buffer
    public List<String> getMessages() {
        List<String> messages = new ArrayList<>(serverMessages);
        serverMessages.clear();
        return messages;
    }
}
