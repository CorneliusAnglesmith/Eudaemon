package net.anglesmith.eudaemon.message;

import net.anglesmith.eudaemon.command.CommandInterpreter;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Redirects specific JDA messages to corresponding bot behavior.
 */
@Service
public class MessageService {
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);

    private final CommandInterpreter commandInterpreter;

    public MessageService(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    /**
     * Processes a message sent from a text channel and directs it to the appropriate behavior.
     *
     * @param messageEvent a JDA {@link MessageReceivedEvent} sent to a text channel.
     */
    public void handleTextMessage(MessageReceivedEvent messageEvent) {
        String messageContent = messageEvent.getMessage().getContentStripped();

        if (!messageEvent.getAuthor().isBot() && messageContent.startsWith(Constants.COMMAND_INVOCATION_TOKEN)) {
            messageContent = messageContent.substring(2).trim();

            LOGGER.info("Eudaemon invoked with command " + messageContent);

            MessageCreateData response = null;

            try {
                response = this.commandInterpreter.executeMessageCommand(messageEvent, messageContent);
            } catch (EudaemonCommandException e) {
                LOGGER.error("A passed command failed to be processed.", e);
            } catch (Exception e) { // Don't judge me.
                LOGGER.fatal("Something really bad just happened", e);

                response = new MessageCreateBuilder()
                    .addContent("Whoa whoa buster!  Something you sent was really messed up.  Ask Ryan to check the logs.")
                    .build();
            }

            if (response != null) {
                this.handleMessageResponse(messageEvent, response);
            }
        }
    }

    private void handleMessageResponse(MessageReceivedEvent messageEvent, MessageCreateData response) {
        messageEvent.getChannel().asTextChannel().sendMessage(response).queue();
    }
}
