package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandController {
    // Kind of a crappy way of doing this, but it works and I don't have to think about it.
    private final Map<String, MessageCommand> commandMap = new HashMap<>();

    private final MessageCommand fallbackCommand = new MessageCommandInvalid();

    public CommandController() {
        this.initializeCommandMap();
    }

    private void initializeCommandMap() {
        // TODO - Place command initialization here.
    }

    public boolean executeMessageCommand(MessageReceivedEvent messageEvent, String messageContent) throws EudaemonCommandException {
        final List<String> messageTokens = Arrays.asList(messageContent.trim().split("\\s"));
        boolean success = false;


        MessageCommand command = this.retrieveMessageCommand(messageTokens);

        try {
            success = command.accept(messageEvent, messageTokens);

            command.execute();
        } catch (IllegalArgumentException e) {
            throw new EudaemonCommandException("Arguments gathered for selected command are invalid.", e);
        }

        return success;
    }

    private MessageCommand retrieveMessageCommand(List<String> messageTokens) {
        MessageCommand parsedCommand;

        if (messageTokens == null || messageTokens.size() == 0 || !this.commandMap.containsKey(messageTokens.get(0))) {
            parsedCommand = this.fallbackCommand;
        } else {
            parsedCommand = this.commandMap.get(messageTokens.get(0));
        }

        return parsedCommand;
    }
}
