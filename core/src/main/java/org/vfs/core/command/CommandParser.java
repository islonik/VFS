package org.vfs.core.command;

import java.util.StringTokenizer;

/**
 * @author Lipatov Nikita
 */
public class CommandParser {
    private CommandValues paramsObject;

    public void parse(String inputString) {
        if (inputString.startsWith("-")) {
            throw new RuntimeException("Command error : " + inputString);
        }
        paramsObject = new CommandValues();
        paramsObject.setSource(inputString);

        StringTokenizer tokenizer = new StringTokenizer(inputString.trim(), " :");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.startsWith("-")) {
                token = token.replaceAll("-", "");
                if (!token.isEmpty()) {
                    paramsObject.getKeys().add(token);
                }
            } else {
                if (paramsObject.getCommand() == null) { // first param is command
                    paramsObject.setCommand(token);
                } else {
                    paramsObject.getParams().add(token);
                }
            }
        }
    }

    public CommandValues getCommandValues() {
        return paramsObject;
    }
}
