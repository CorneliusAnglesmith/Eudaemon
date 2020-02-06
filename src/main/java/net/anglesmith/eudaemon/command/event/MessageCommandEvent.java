package net.anglesmith.eudaemon.command.event;

import net.anglesmith.eudaemon.command.MessageCommand;
import net.anglesmith.eudaemon.command.event.EventDao;
import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageCommandEvent implements MessageCommand {
    private final EventDao eventDao;

    public MessageCommandEvent(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        this.eventDao.testConnection();

        return new MessageBuilder().append("Test complete!!!").build();
    }

    @Override
    public Message documentation() {
        return new MessageBuilder().append("```TODO```").build();
    }

    @Override
    public String invocationToken() {
        return "event";
    }
}
