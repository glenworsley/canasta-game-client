package com.glenworsley.canastaclient;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log4j2
@Component
public class GameServerMessageHandler extends Thread {

    private PrintWriter out;
    private BufferedReader in;

    //TODO: handle exception properly
    @SneakyThrows
    public void run() {
        log.info("connecting to game server");
        //TODO: move this to configuration/properties
        Socket socket = new Socket("127.0.0.1", 4001);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        log.info("connected to server");
    }

    //will block waiting for a response
    public String sendMessageToServer(String message) throws IOException {
        log.info("sending message to server");
        out.println(message);
        String response = in.readLine();
        return response;
    }

}
