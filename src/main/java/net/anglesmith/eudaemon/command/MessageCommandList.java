package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class MessageCommandList implements MessageCommand {
    private final Map<String, MessageCommand> commandMap;

    public MessageCommandList(Map<String, MessageCommand> commandMap) {
        this.commandMap = commandMap;
    }

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
            this.commandMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .forEach(command ->
                    builder.append(":point_right: ")
                        .append(String.format("**%s**", command.invocationToken()))
                        .append('\n'));
            builder.append("Use ")
                .append(String.format("`%s %s`",
                    Constants.COMMAND_INVOCATION_TOKEN, "help"))
                .append(" for more information about all commands or ")
                .append(String.format("`%s %s [command name]`",
                    Constants.COMMAND_INVOCATION_TOKEN, "help"))
                .append(" for a specific command.");
        }

        return builder.build();
    }

    @Override
    public Message documentation() {
        final MessageBuilder builder = new MessageBuilder();

        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + this.invocationToken();

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
