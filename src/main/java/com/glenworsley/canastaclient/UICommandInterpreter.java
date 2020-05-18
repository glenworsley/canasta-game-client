package com.glenworsley.canastaclient;

public interface UICommandInterpreter {

    UICommand getCommand(String rawInput);
}
