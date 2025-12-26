package io.github.antonleliuk.tgfilecollector.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramConfiguration {

    @Bean
    public TelegramUrl telegramUrl(TelegramProperties telegramProperties) {
        return telegramProperties.getTelegramUrl();
    }

    @Bean
    public TelegramClient telegramClient(TelegramProperties props, TelegramUrl telegramUrl) {
        return new OkHttpTelegramClient(props.getToken(), telegramUrl);
    }
}
