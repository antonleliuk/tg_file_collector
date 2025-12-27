package io.github.antonleliuk.tgfilecollector.conf;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramUrl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegrambots")
public class TelegramProperties {

    private boolean enabled;

    @NestedConfigurationProperty
    private Security security = new Security();

    @NestedConfigurationProperty
    private HttpClient httpClient = new HttpClient();

    @NestedConfigurationProperty
    private TelegramUrl telegramUrl;

    @NestedConfigurationProperty
    private StorageProperties storage;

    @Getter
    @Setter
    public static class Security {
        private String token;

        private Set<String> allowedUsers;
    }

    @Getter
    @Setter
    public static class HttpClient {
        private int connectTimeout = 10000;

        private int readTimeout = 60000;

        private int writeTimeout = 10000;
    }

    @Getter
    @Setter
    public static class StorageProperties {

        /**
         * Destination directory for stored files
         */
        private String rootDir;

        /**
         * Destination directory for stored files from telegram
         */
        private String telegramRootDir;
    }
}
