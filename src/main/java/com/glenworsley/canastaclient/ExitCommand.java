package com.glenworsley.canastaclient;

import org.springframework.stereotype.Component;

@Component
public class ExitCommand implements UICommand {

    @Override
    public String execute() {
        return "Bye!";
    }
}
