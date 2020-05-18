package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Log4j2
@Component
@RequiredArgsConstructor
public class GetGameCodeCommand implements UICommand {

    private final ClientMessageHandler clientMessageHandler;

    @SneakyThrows
    @Override
    public String execute() {
        log.info("received request to get a new gameCode");
        String gameCode = clientMessageHandler.sendMessageToServer("{ \"event\": \"request_gameCode\" }");
        return gameCode;
    }
}
