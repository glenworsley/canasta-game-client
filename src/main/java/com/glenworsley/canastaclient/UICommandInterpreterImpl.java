package com.glenworsley.canastaclient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class UICommandInterpreterImpl implements UICommandInterpreter {

    private final GetGameCodeCommand getGameCodeCommand;
    private final JoinGameCommand joinGameCommand;
    private final ExitCommand exitCommand;
    private final GameState gameState;
    private final DrawStockCardCommand drawStockCardCommand;

    @Override
    public UICommand getCommand(String rawInput) {
        if ("1".equals(rawInput)) {
            if (gameState.isMyTurn())
                return drawStockCardCommand;
            else
                return getGameCodeCommand;
        } else {
            if ("2".equals(rawInput)) {
                return joinGameCommand;
            } else {
                if ("3".equals(rawInput)) {
                    return exitCommand;
                } else {
                    log.warn("unknown command received: {}", rawInput);
                }
            }
        }
        return null;
    }
}
