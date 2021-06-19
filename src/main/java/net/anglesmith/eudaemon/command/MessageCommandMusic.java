package net.anglesmith.eudaemon.command;

import net.anglesmith.eudaemon.exception.EudaemonCommandException;
import net.anglesmith.eudaemon.message.Constants;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MessageCommandMusic implements MessageCommand {
    @Override
    public boolean validate(MessageReceivedEvent messageEvent, List<String> messageTokens) {
        return true;
    }

    @Override
    public Message execute(MessageReceivedEvent messageEvent, List<String> messageTokens) throws EudaemonCommandException {
        final var user = messageEvent.getAuthor();
        final var guild = messageEvent.getGuild();
        final var audioManager = guild.getAudioManager();

        return guild.getVoiceStates().stream()
            .filter(guildVoiceState ->
                (guildVoiceState.getMember().getUser().getIdLong() == user.getIdLong() && guildVoiceState.getChannel() != null))
            .map(GuildVoiceState::getChannel)
            .findAny()
            .map(channel -> {
                audioManager.openAudioConnection(channel);

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                audioManager.closeAudioConnection();

                return new MessageBuilder().append(channel.getAsMention()).build();})
            .orElse(new MessageBuilder().append("ayy lmao").build());
    }

    @Override
    public Message documentation() {
        final MessageBuilder docMessageBuilder = new MessageBuilder();
        final String invokeExpression = Constants.COMMAND_INVOCATION_TOKEN + " " + CommandToken.COMMAND_MUSIC.getCommandName();

        docMessageBuilder.append(invokeExpression);

        return docMessageBuilder.build();
    }

    @Override
    public String invocationToken() {
        return CommandToken.COMMAND_MUSIC.getCommandName();
    }
}
