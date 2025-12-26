package io.github.antonleliuk.tgfilecollector.bot;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.antonleliuk.tgfilecollector.conf.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramFileCollectorLongPollingUpdateConsumer implements LongPollingUpdateConsumer {

    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    private final TelegramClient telegramClient;
    private final TelegramProperties telegramProperties;

    @Override
    public void consume(List<Update> updates) {
        log.info("Received updates: {}", updates);
        updates.forEach(update -> executor.execute(new TelegramFileCollectorTask(update, telegramClient, telegramProperties)));
    }
}
