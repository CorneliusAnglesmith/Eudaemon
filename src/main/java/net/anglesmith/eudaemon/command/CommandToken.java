package net.anglesmith.eudaemon.command;

import java.util.Arrays;
import java.util.Optional;

public enum CommandToken {
    COMMAND_ROLL("roll"),
    COMMAND_HELP("help"),
    COMMAND_LIST("list"),
    COMMAND_MUSIC("music");

    private final String commandName;

    CommandToken(String command) {
        this.commandName = command;
    }

    public static Optional<CommandToken> fromCommandString(String command) {
        return Arrays.stream(CommandToken.values())
            .filter(commandToken -> commandToken.commandName.equalsIgnoreCase(command))
            .findFirst();
    }

    public String getCommandName() {
        return this.commandName;
    }
}
