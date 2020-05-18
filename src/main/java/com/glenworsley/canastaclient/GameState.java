package com.glenworsley.canastaclient;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class GameState {

    private boolean myTurn;
    private String gameCode;
    private String playerName;
    private String gameState;
    private List<String> handOfCards;

    public void addCardToHand(String card) {
        handOfCards.add(card);
    }
}
