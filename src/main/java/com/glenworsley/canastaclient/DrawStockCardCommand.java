package com.glenworsley.canastaclient;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class DrawStockCardCommand implements UICommand {

    private final GameState gameState;
    private final ClientMessageHandler clientMessageHandler;

    @SneakyThrows
    @Override
    public String execute() {

        String drawStockCardMessage = buildDrawStockCardMessage();
        String response = clientMessageHandler.sendMessageToServer(drawStockCardMessage);
        log.info("response from server: {}", response);
        String drawnCard = getDrawnCard(response);
        gameState.addCardToHand(drawnCard);
        return "Card is: " + drawnCard;
    }

    private String getDrawnCard(String response) {
        JSONObject responseJson = new JSONObject(response);
        return responseJson.getString("drawnCard");
    }

    private String buildDrawStockCardMessage() {
        JSONObject drawStockCardMessage = new JSONObject();
        drawStockCardMessage.put("event", "draw_stock_card");
        drawStockCardMessage.put("gameCode", gameState.getGameCode());
        return drawStockCardMessage.toString();
    }

}


