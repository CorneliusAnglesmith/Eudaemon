package net.anglesmith.eudaemon.command;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommandLibrary {
    private static final MessageCommand FALLBACK_COMMAND = new MessageCommandInvalid();

    private final Map<String, MessageCommand> registeredCommands;

    public CommandLibrary(List<MessageCommand> registeredCommands) {
        this.registeredCommands =
            registeredCommands.stream().collect(Collectors.toMap(MessageCommand::invocationToken, command -> command));

        // Load meta-commands
        final var helpCommand = new MessageCommandHelp(this.registeredCommands);
        final var listCommand = new MessageCommandList(this.registeredCommands);
        this.registeredCommands.put(helpCommand.invocationToken(), helpCommand);
        this.registeredCommands.put(listCommand.invocationToken(), listCommand);
    }

    public Optional<MessageCommand> retrieveMessageCommandFromInvocation(String command) {
        return Optional.ofNullable(this.registeredCommands.get(command));
    }

    public MessageCommand resolveExecutableCommand(List<String> messageTokens) {
        return messageTokens.stream()
            .findFirst()
            .flatMap(this::retrieveMessageCommandFromInvocation)
            .orElse(FALLBACK_COMMAND);
    }
}
