package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MessageCommandHelp implements MessageCommand {
    private String requestedCommand;

    @Override
    public boolean accept(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        final String joinedTokens = String.join(" ", messageTokens.subList(1, messageTokens.size()));

        this.requestedCommand = joinedTokens.trim();

        return true;
    }

    @Override
    public Message execute() throws EudaemonCommandException {
        final MessageBuilder documentationBuilder = new MessageBuilder();

        if (StringUtils.isBlank(this.requestedCommand)) {
            Stream.of(CommandToken.values())
                .map(CommandToken::getCommandName)
                .map(CommandInterpreter::retrieveMessageCommandFromInvocation)
                .flatMap(Optional::stream)
                .map(MessageCommand::documentation)
                .map(Message::getContentRaw)
                .forEach(content -> documentationBuilder.append("\n").append(content));
        } else {
            documentationBuilder.append(
                CommandInterpreter.retrieveMessageCommandFromInvocation(this.requestedCommand)
                    .map(MessageCommand::documentation)
                    .map(Message::getContentRaw)
                    .orElse("Could not find documentation for '" + this.requestedCommand + "'."));
        }

        return documentationBuilder.build();
    }

    @Override
    public Message documentation() {
        final MessageBuilder docMessageBuilder = new MessageBuilder();
        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + CommandToken.COMMAND_HELP.getCommandName();

        docMessageBuilder.appendCodeBlock(
            "Documentation command.\n\n"
            + "SYNOPSIS\n\t" + invokeExpression + " [Eudaemon command]\n\t" + invokeExpression + "\n"
            + "DESCRIPTION\n\tRetrieve documentation for a specific command, or for all commands.",
            "");

        return docMessageBuilder.build();
    }
}
