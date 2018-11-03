package net.anglesmith.eudaemon;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class Eudaemon extends ListenerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(Eudaemon.class);

    private static final String CONFIG_FILE_LOCATION = "./eudaemon.properties";

    public static void main(String[] args) {
        Configurations commonsConfigurations = new Configurations();

        try {
            Configuration eudaemonConfiguration = commonsConfigurations.properties(CONFIG_FILE_LOCATION);

            final String botToken = eudaemonConfiguration.getString("botToken");

            JDA jda = new JDABuilder(botToken).addEventListener(new Eudaemon()).build();
            jda.awaitReady();

            LOGGER.info("Eudaemon JDA startup completed.");
        } catch (ConfigurationException configEx) {
            LOGGER.fatal("Unable to load configuration file: " + CONFIG_FILE_LOCATION, configEx);
        } catch (LoginException loginEx) {
            LOGGER.fatal("Eudaemon login failed.", loginEx);
        } catch (InterruptedException interruptEx) {
            LOGGER.fatal("JDA startup on main thread interrupted!", interruptEx);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();

        User sender = event.getAuthor();

        String senderName = sender.getName();
        String message = event.getMessage().getContentDisplay();

        boolean isSenderBot = sender.isBot();

        if (event.isFromType(ChannelType.TEXT) && !isSenderBot) {
            TextChannel channel = event.getTextChannel();

            if (message.equals("Die please.")) {
                LOGGER.info("Eudaemon chat kill switch activated by " + senderName + ".");

                channel.sendMessage("Oof.").tts(true).queue();

                jda.shutdown();

                System.exit(0);
            } else {
                channel.sendMessage(String.format("I think I can hear you, %s; you said \"%s\"", senderName, message)).queue();
            }
        }
    }
}
