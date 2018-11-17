package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
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
    boolean accept(MessageReceivedEvent messageEvent, List<String> messageTokens);

    /**
     * Execute the preset Eudaemon command.
     *
     * @throws EudaemonCommandException if command arguments are not set before calling this command or if there were
     *                                  problems during execution.
     */
    void execute() throws EudaemonCommandException;
}
