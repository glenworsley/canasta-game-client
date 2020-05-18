package com.glenworsley.canastaclient;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Profile("!ContextTest")
@Component
@RequiredArgsConstructor
@Log4j2
@Getter
public class CanastaClient implements CommandLineRunner {

    private final ServerMessageHandler serverMessageHandler;
    private final BufferedReader uiInputReader;
    private final PrintWriter uiOutputWriter;
    private final UICommandInterpreter uiCommandInterpreter;

    @Override
    public void run(String... args) throws Exception {
        uiOutputWriter.println("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ");
        String rawUiCommand;
        while ((rawUiCommand = uiInputReader.readLine()) != null) {
            UICommand command = uiCommandInterpreter.getCommand(rawUiCommand);
            if (command != null) {
                String response = command.execute();
                uiOutputWriter.println(response);
                if (command instanceof ExitCommand)
                    break;
            }
            serverMessageHandler.actionServerMessages();
            //prompt user for next command
            uiOutputWriter.println("Please enter 1 to get a new gameCode, 2 to join a game or 3 to quit: ");
        }
        uiInputReader.close();
        uiOutputWriter.close();
    }

//    private void actionServerCommands() {
//        List<String> messages = serverMessageHandler.getMessages();
//        for (String message : messages) {
//            //action the message
//            log.info("actioning server message: {}", message);
//        }
//        JSONObject jsonObject = new JSONObject(message);
//            if ("player_joined".equals(jsonObject.get("event"))) {
//                String playerName = jsonObject.getString("playerName");
//                uiOutputWriter.println("Event: New player joined.");
//                //uiWriter.println("Players are: " + getPlayerNames());
//                JSONObject gameState = jsonObject.getJSONObject("gameState");
//                uiOutputWriter.println("Game state = " + gameState);
//            }
//        }
//    }

}
