package com.glenworsley.canastaclient;

import lombok.Data;

@Data
public class ServerMessage {

    private String event;

    //current game state
    private String gameState;
    private String[] players;


}
