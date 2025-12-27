package io.github.antonleliuk.tgfilecollector.conf;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import okhttp3.OkHttpClient;

@Configuration
public class TelegramConfiguration {

    @Bean
    public TelegramUrl telegramUrl(TelegramProperties telegramProperties) {
        return telegramProperties.getTelegramUrl();
    }

    @Bean
    public TelegramClient telegramClient(TelegramProperties props, TelegramUrl telegramUrl) {
        return new OkHttpTelegramClient(
                new OkHttpClient.Builder()
                        .connectTimeout(props.getHttpClient().getConnectTimeout(), TimeUnit.MILLISECONDS)
                        .readTimeout(props.getHttpClient().getReadTimeout(), TimeUnit.MILLISECONDS)
                        .writeTimeout(props.getHttpClient().getWriteTimeout(), TimeUnit.MILLISECONDS)
                        .build(),
                props.getSecurity().getToken(),
                telegramUrl
        );
    }
}
