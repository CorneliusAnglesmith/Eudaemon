package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.exception.EudaemonRuntimeException;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * This command is used as a fallback when the controller can't understand the passed command token.
 */
public class MessageCommandInvalid implements MessageCommand {
    /**
     * {@inheritDoc}
     *
     * {@link MessageCommandInvalid} always returns <code>true</code> and is used as a fallback.
     */
    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        if (messageEvent == null) {
            throw new IllegalArgumentException("Message event cannot be null.");
        }

        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        if (messageEvent == null) {
            throw new EudaemonCommandException("No message event was received");
        }

        final MessageBuilder responseMessageBuilder = new MessageBuilder();

        responseMessageBuilder.append(messageEvent.getAuthor().getAsMention());

        responseMessageBuilder.append(", I had a problem understanding what you sent.");

        if (messageTokens != null) {
            if (messageTokens.size() > 0) {
                responseMessageBuilder.append(String.format("  I don't know what \"%s\" means.", messageTokens.get(0)));
            }

            if (messageTokens.size() > 1) {
                final String responseArguments = String.join(" ", messageTokens.subList(1, messageTokens.size()));
                responseMessageBuilder.append(
                    String.format("  You also sent \"%s\" as arguments; were you looking for a different command?",
                        responseArguments));
            }
        }

        responseMessageBuilder.append("  Please try again.");

        return responseMessageBuilder.build();
    }

    @Override
    public Message documentation() {
        throw new EudaemonRuntimeException("Cannot resolve documentation for the placeholder 'Invalid' command.");
    }

    @Override
    public String invocationToken() {
        throw new UnsupportedOperationException(
            "This fallback command should not be registered under an invocation token.");
    }
}
