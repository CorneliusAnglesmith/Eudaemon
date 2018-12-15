package net.anglesmith.eudaemon.command;

import java.util.Arrays;
import java.util.Optional;

public enum CommandToken {
    COMMAND_ROLL("roll");

    private final String commandName;

    CommandToken(String command) {
        this.commandName = command;
    }

    @Override
    public String toString() {
        return this.commandName;
    }

    public String endpointName() {
        return "/" + this.commandName;
    }

    public static String retrieveCommandEndpoint(String commandToken) {
        Optional<CommandToken> optionalStream = Arrays.stream(CommandToken.values())
                .filter(command -> commandToken.equals(command.toString()))
                .findFirst();

        String commandEndpoint = null;

        if (optionalStream.isPresent()) {
            commandEndpoint = optionalStream.get().endpointName();
        }

        return commandEndpoint;
    }
}
