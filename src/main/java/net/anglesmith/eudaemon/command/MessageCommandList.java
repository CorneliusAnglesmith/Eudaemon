package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MessageCommandList implements MessageCommand {
    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final MessageBuilder builder = new MessageBuilder();

        if (messageTokens.size() > 1) {
            builder.append("The List command takes no arguments.");
        } else {
            builder.append("The following commands are supported:\n");
            Stream.of(CommandToken.values())
                .map(CommandToken::getCommandName)
                .sorted()
                .forEach(token ->
                    builder.append(":point_right: ")
                        .append(String.format("**%s**", token))
                        .append('\n'));
            builder.append("Use ")
                .append(String.format("`%s %s`",
                    Constants.COMMAND_INVOCATION_TOKEN, CommandToken.COMMAND_HELP.getCommandName()))
                .append(" for more information about all commands or ")
                .append(String.format("`%s %s [command name]`",
                    Constants.COMMAND_INVOCATION_TOKEN, CommandToken.COMMAND_HELP.getCommandName()))
                .append(" for a specific command.");
        }

        return builder.build();
    }

    @Override
    public Message documentation() {
        final MessageBuilder builder = new MessageBuilder();

        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + CommandToken.COMMAND_LIST.getCommandName();

        builder.appendCodeBlock(
            "Command list utility.\n\n"
                + "SYNOPSIS\n\t" + invokeExpression + "\n"
                + "DESCRIPTION\n\tRetrieve a simple listing of all commands.",
            "");

        return builder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_LIST.getCommandName();
    }
}
