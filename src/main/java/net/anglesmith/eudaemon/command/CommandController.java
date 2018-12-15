package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommandController {
    // Kind of a crappy way of doing this, but it works and I don't have to think about it.
    private static final Map<String, MessageCommand> COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_MAP.put(CommandToken.COMMAND_ROLL.toString(), new MessageCommandRoll());
    }

    private static final MessageCommand FALLBACK_COMMAND = new MessageCommandInvalid();

    public static Message executeMessageCommand(MessageReceivedEvent messageEvent, String messageContent) throws EudaemonCommandException {
        final List<String> messageTokens = Arrays.asList(messageContent.trim().split("\\s"));

        final String commandEndpoint = messageTokens.size() != 0 ?
                CommandToken.retrieveCommandEndpoint(messageTokens.get(0)) : null;

        MessageCommand resolvedCommand = commandEndpoint != null && COMMAND_MAP.containsKey(commandEndpoint) ?
            COMMAND_MAP.get(commandEndpoint) : FALLBACK_COMMAND;

        if (!resolvedCommand.accept(messageEvent, messageTokens)) {
            throw new EudaemonCommandException("Command arguments unacceptable.");
        }

        Message commandResponse;

        try {
            commandResponse = resolvedCommand.execute();
        } catch (IllegalArgumentException e) {
            throw new EudaemonCommandException("Arguments gathered for selected command are invalid.", e);
        }

        return commandResponse;
    }
}
