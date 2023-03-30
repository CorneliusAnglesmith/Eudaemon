package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
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
    public MessageCreateData execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final String joinedTokens = String.join(" ", messageTokens.subList(1, messageTokens.size()));

        final var requestedCommand = joinedTokens.trim();

        final MessageCreateBuilder documentationBuilder = new MessageCreateBuilder();

        if (StringUtils.isBlank(requestedCommand)) {
            this.commandMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(MessageCommand::documentation)
                .map(MessageData::getContent)
                .forEach(content -> documentationBuilder.addContent("\n").addContent(content));
        } else {
            documentationBuilder.addContent(
                Optional.ofNullable(this.commandMap.get(requestedCommand))
                    .map(MessageCommand::documentation)
                    .map(MessageData::getContent)
                    .orElse("Could not find documentation for '" + requestedCommand + "'."));
        }

        return documentationBuilder.build();
    }

    @Override
    public MessageCreateData documentation() {
        final MessageCreateBuilder docMessageBuilder = new MessageCreateBuilder();
        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + this.invocationToken();

        docMessageBuilder.addContent(
            "```Documentation command.\n\n"
            + "SYNOPSIS\n\t" + invokeExpression + " [Eudaemon command]\n\t" + invokeExpression + "\n"
            + "DESCRIPTION\n\tRetrieve documentation for a specific command, or for all commands.```");

        return docMessageBuilder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_HELP.getCommandName();
    }

    @Override
    public SlashCommandData asSlashCommand() {
        return Commands.slash(this.invocationToken(), "Retrieve documentation for a specific command, or for all commands.")
            .addOption(OptionType.STRING, "command", "The name of the command to retrieve documentation for.  Optional.");
    }
}
