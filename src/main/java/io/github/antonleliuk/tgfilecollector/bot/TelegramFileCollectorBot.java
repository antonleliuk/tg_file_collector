package io.github.antonleliuk.tgfilecollector.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

import io.github.antonleliuk.tgfilecollector.conf.TelegramProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TelegramFileCollectorBot implements SpringLongPollingBot {

    private final TelegramProperties telegramProperties;
    private final TelegramFileCollectorLongPollingUpdateConsumer updateConsumer;

    @Override
    public String getBotToken() {
        return telegramProperties.getSecurity().getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
