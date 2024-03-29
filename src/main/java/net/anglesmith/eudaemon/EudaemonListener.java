package net.anglesmith.eudaemon;

import net.anglesmith.eudaemon.message.MessageService;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EudaemonListener extends ListenerAdapter {
    private final MessageService messageService;

    public EudaemonListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            this.messageService.handleTextMessage(event);
        }
    }
}
