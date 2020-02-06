package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Eudaemon commands implement this interface, typically using JDA as a receiver.
 *
 * See Design Patterns: Elements of Reusable Object-Oriented Software. Addison Wesley. pp. 233ff.
 * https://en.wikipedia.org/wiki/Command_pattern
 */
public interface MessageCommand {
    /**
     * Determine whether the routed message and tokens are appropriate for this command.
     *
     * @param messageEvent A {@link MessageReceivedEvent} invoking this command.
     * @param messageTokens A {@link List} of {@link String} tokens acting as arguments for this command.
     * @return <code>true</code> if the command is acceptable.
     */
    boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens);

    /**
     * Execute the preset Eudaemon command.
     *
     * @param messageEvent A {@link MessageReceivedEvent} invoking this command.
     * @param messageTokens A {@link List} of {@link String} tokens acting as arguments for this command.
     * @return A {@link Message} containing an appropriate response.
     * @throws EudaemonCommandException if command arguments are not set before calling this command or if there were
     *                                  problems during execution.
     */
    Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException;

    /**
     * Present help documentation for this Eudaemon command.
     *
     * @return A {@link Message} containing helpful information about this command.
     */
    Message documentation();

    /**
     * Present this command's requested invocation command token.
     *
     * @return a {@link String} containing the token this command should be registered under.
     */
    String invocationToken();
}
