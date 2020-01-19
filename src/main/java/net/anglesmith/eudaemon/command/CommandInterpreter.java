package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class CommandInterpreter {
    private static final MessageCommand FALLBACK_COMMAND = new MessageCommandInvalid();

    public static Message executeMessageCommand(MessageReceivedEvent messageEvent, String messageContent) throws EudaemonCommandException {
        final List<String> messageTokens = Arrays.asList(messageContent.trim().split("\\s"));
        final MessageCommand resolvedCommand = resolveExecutableCommand(messageTokens);
        final Message commandResponse;

        if (!resolvedCommand.accept(messageEvent, messageTokens)) {
            commandResponse = resolvedCommand.documentation();
        } else {
            try {
                commandResponse = resolvedCommand.execute();
            } catch (IllegalArgumentException e) {
                throw new EudaemonCommandException("Arguments gathered for selected command are invalid.", e);
            }
        }

        return commandResponse;
    }

    private static MessageCommand resolveExecutableCommand(List<String> messageTokens) {
        return messageTokens.stream()
            .findFirst()
            .flatMap(CommandInterpreter::retrieveMessageCommandFromInvocation)
            .orElse(FALLBACK_COMMAND);
    }

    public static Optional<MessageCommand> retrieveMessageCommandFromInvocation(String command) {
        return CommandToken.fromCommandString(command).map(commandToken -> {
            switch(commandToken) {
                case COMMAND_ROLL:
                    return new MessageCommandRoll();
                case COMMAND_HELP:
                    return new MessageCommandHelp();
                case COMMAND_LIST:
                    return new MessageCommandList();
                default:
                    throw new UnsupportedOperationException(
                            "The following command is not yet fully implemented: " + command);
            }
        });
    }
}
