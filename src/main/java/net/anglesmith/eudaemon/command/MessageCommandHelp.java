package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MessageCommandHelp implements MessageCommand {
    private final Map<String, MessageCommand> commandMap;

    public MessageCommandHelp(Map<String, MessageCommand> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final String joinedTokens = String.join(" ", messageTokens.subList(1, messageTokens.size()));

        final var requestedCommand = joinedTokens.trim();

        final MessageBuilder documentationBuilder = new MessageBuilder();

        if (StringUtils.isBlank(requestedCommand)) {
            this.commandMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(MessageCommand::documentation)
                .map(Message::getContentRaw)
                .forEach(content -> documentationBuilder.append("\n").append(content));
        } else {
            documentationBuilder.append(
                Optional.ofNullable(this.commandMap.get(requestedCommand))
                    .map(MessageCommand::documentation)
                    .map(Message::getContentRaw)
                    .orElse("Could not find documentation for '" + requestedCommand + "'."));
        }

        return documentationBuilder.build();
    }

    @Override
    public Message documentation() {
        final MessageBuilder docMessageBuilder = new MessageBuilder();
        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + this.invocationToken();

        docMessageBuilder.appendCodeBlock(
            "Documentation command.\n\n"
            + "SYNOPSIS\n\t" + invokeExpression + " [Eudaemon command]\n\t" + invokeExpression + "\n"
            + "DESCRIPTION\n\tRetrieve documentation for a specific command, or for all commands.",
            "");

        return docMessageBuilder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_HELP.getCommandName();
    }
}
