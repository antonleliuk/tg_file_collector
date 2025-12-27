package io.github.antonleliuk.tgfilecollector.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import io.github.antonleliuk.tgfilecollector.conf.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SecurityService {

    private final TelegramProperties telegramProperties;

    public boolean isAllowed(Message message) {
        return CollectionUtils.isEmpty(telegramProperties.getSecurity().getAllowedUsers())
                || telegramProperties.getSecurity().getAllowedUsers().contains(message.getFrom().getUserName());
    }
}
