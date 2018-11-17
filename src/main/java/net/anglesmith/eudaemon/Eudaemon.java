package net.anglesmith.eudaemon;

import net.anglesmith.eudaemon.message.MessageService;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
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

    private final MessageService messageService = new MessageService();

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
        if (event.isFromType(ChannelType.TEXT)) {
            this.messageService.handleTextMessage(event);
        }
    }
}
