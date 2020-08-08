package net.anglesmith.eudaemon;

import net.anglesmith.eudaemon.message.MessageService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@SpringBootApplication(scanBasePackages = "net.anglesmith.eudaemon")
public class Eudaemon implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(Eudaemon.class);

    private static final String CONFIG_FILE_LOCATION = "./eudaemon.properties";

    private final MessageService messageService;

    private final ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Eudaemon.class, args);
    }

    public Eudaemon(MessageService messageService, ApplicationContext context) {
        this.messageService = messageService;
        this.applicationContext = context;
    }

    @Override
    public void run(String... args) {
        Configurations commonsConfigurations = new Configurations();

        final LocalDate startupTime = Instant.ofEpochMilli(this.applicationContext.getStartupDate()).atZone(
            ZoneId.systemDefault()).toLocalDate();

        LOGGER.info(" ======== EUDAEMON ENVIRONMENT BEGIN ======== ");
        LOGGER.info("Application Name: " + this.applicationContext.getApplicationName());
        LOGGER.info("Display Name: " + this.applicationContext.getDisplayName());
        LOGGER.info("Application ID: " + this.applicationContext.getId());
        LOGGER.info("Startup date: " + startupTime.toString());
        LOGGER.info(" ========  EUDAEMON ENVIRONMENT END  ======== ");

        try {
            Configuration eudaemonConfiguration = commonsConfigurations.properties(CONFIG_FILE_LOCATION);

            final String botToken = eudaemonConfiguration.getString("botToken");

            // Initialize JDA, giving the bot the ability to read messages, reactions, and member lists.
            final JDA jda = JDABuilder.create(botToken,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                .addEventListeners(new EudaemonListener(this.messageService))
                .build();

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
}
