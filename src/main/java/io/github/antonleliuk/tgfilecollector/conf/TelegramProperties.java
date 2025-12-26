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

    private String token;

    private Set<String> allowedUsers;

    @NestedConfigurationProperty
    private TelegramUrl telegramUrl;
}
