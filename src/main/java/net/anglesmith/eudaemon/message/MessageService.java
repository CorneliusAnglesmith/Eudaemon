package net.anglesmith.eudaemon.message;

import net.anglesmith.eudaemon.command.CommandController;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

/**
 * Redirects specific JDA messages to corresponding bot behavior.
 */
public class MessageService {
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);

    private static final String COMMAND_TOKEN = "$$";

    private final CommandController controller = new CommandController();

    /**
     * Processes a message sent from a text channel and directs it to the appropriate behavior.
     *
     * @param messageEvent a JDA {@link MessageReceivedEvent} sent to a text channel.
     */
    public void handleTextMessage(MessageReceivedEvent messageEvent) {
        String messageContent = messageEvent.getMessage().getContentStripped();

        if (!messageEvent.getAuthor().isBot() && messageContent.startsWith(COMMAND_TOKEN)) {
            messageContent = messageContent.substring(2).trim();

            try {
                controller.executeMessageCommand(messageEvent, messageContent);
            } catch (EudaemonCommandException e) {
                LOGGER.error("A passed command failed to be processed.", e);
            }
        }
    }
}
