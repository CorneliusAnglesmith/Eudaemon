package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class CommandInterpreter {
    private final CommandLibrary commandLibrary;

    public CommandInterpreter(CommandLibrary commandLibrary) {
        this.commandLibrary = commandLibrary;
    }

    public MessageCreateData executeMessageCommand(MessageReceivedEvent messageEvent, String messageContent) throws EudaemonCommandException {
        final List<String> messageTokens = Arrays.asList(messageContent.trim().split("\\s"));
        final MessageCommand resolvedCommand = this.commandLibrary.resolveExecutableCommand(messageTokens);
        final MessageCreateData commandResponse;

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
