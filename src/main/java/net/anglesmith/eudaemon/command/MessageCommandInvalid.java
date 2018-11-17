package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * This command is used as a fallback when the controller can't understand the passed command token.
 */
public class MessageCommandInvalid implements MessageCommand {
    private List<String> tokens;
    private TextChannel messageTextChannel;

    /**
     * {@inheritDoc}
     *
     * {@link MessageCommandInvalid} always returns <code>true</code> and is used as a fallback.
     */
    @Override
    public boolean accept(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        if (messageEvent == null) {
            throw new IllegalArgumentException("Message event cannot be null.");
        }

        this.tokens = messageTokens;
        this.messageTextChannel = messageEvent.getTextChannel();

        return true;
    }

    @Override
    public void execute() throws EudaemonCommandException {
        if (this.messageTextChannel == null) {
            throw new EudaemonCommandException("No message event was received");
        }

        final StringBuilder response = new StringBuilder();

        response.append("I had a problem understanding what you sent.");

        if (this.tokens != null) {
            if (this.tokens.size() > 0) {
                response.append(String.format("  I don't know what \"%s\" means.", this.tokens.get(0)));
            }

            if (this.tokens.size() > 1) {
                final String responseArguments = String.join(" ", this.tokens.subList(1, this.tokens.size()));
                response.append(
                        String.format("  You also sent \"%s\" as arguments; were you looking for a different command?",
                                responseArguments));
            }
        }

        response.append("  Please try again.");

        this.messageTextChannel.sendMessage(response.toString()).queue();
    }
}
