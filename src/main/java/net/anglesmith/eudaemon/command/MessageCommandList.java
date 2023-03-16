package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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
    public MessageCreateData execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final MessageCreateBuilder builder = new MessageCreateBuilder();

        if (messageTokens.size() > 1) {
            builder.addContent("The List command takes no arguments.");
        } else {
            builder.addContent("The following commands are supported:\n");
            this.commandMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .forEach(command ->
                    builder.addContent(":point_right: ")
                        .addContent(String.format("**%s**", command.invocationToken()))
                        .addContent("\n"));
            builder.addContent("Use ")
                .addContent(String.format("`%s %s`",
                    Constants.COMMAND_INVOCATION_TOKEN, "help"))
                .addContent(" for more information about all commands or ")
                .addContent(String.format("`%s %s [command name]`",
                    Constants.COMMAND_INVOCATION_TOKEN, "help"))
                .addContent(" for a specific command.");
        }

        return builder.build();
    }

    @Override
    public MessageCreateData documentation() {
        final MessageCreateBuilder builder = new MessageCreateBuilder();

        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + this.invocationToken();

        builder.addContent(MarkdownUtil.codeblock(
            "Command list utility.\n\n"
                + "SYNOPSIS\n\t" + invokeExpression + "\n"
                + "DESCRIPTION\n\tRetrieve a simple listing of all commands."));

        return builder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_LIST.getCommandName();
    }

    @Override
    public SlashCommandData asSlashCommand() {
        return Commands.slash(this.invocationToken(), "Retrieve a simple listing of all commands.");
    }
}
