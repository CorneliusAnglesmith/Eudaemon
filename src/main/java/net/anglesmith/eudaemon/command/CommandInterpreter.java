package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class CommandInterpreter {
    private final CommandLibrary commandLibrary;

    public CommandInterpreter(CommandLibrary commandLibrary) {
        this.commandLibrary = commandLibrary;
    }

    public Message executeMessageCommand(MessageReceivedEvent messageEvent, String messageContent) throws EudaemonCommandException {
        final List<String> messageTokens = Arrays.asList(messageContent.trim().split("\\s"));
        final MessageCommand resolvedCommand = this.commandLibrary.resolveExecutableCommand(messageTokens);
        final Message commandResponse;

        if (!resolvedCommand.validate(messageEvent, messageTokens)) {
            commandResponse = resolvedCommand.documentation();
        } else {
            try {
                commandResponse = resolvedCommand.execute(messageEvent, messageTokens);
            } catch (IllegalArgumentException e) {
                throw new EudaemonCommandException("Arguments gathered for selected command are invalid.", e);
            }
        }

        return commandResponse;
    }
}
