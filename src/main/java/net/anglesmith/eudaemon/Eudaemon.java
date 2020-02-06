package net.anglesmith.eudaemon;

import net.anglesmith.eudaemon.message.MessageService;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Eudaemon extends ListenerAdapter {
    private final MessageService messageService;

    public Eudaemon(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            this.messageService.handleTextMessage(event);
        }
    }
}
