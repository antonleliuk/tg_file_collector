package io.github.antonleliuk.tgfilecollector.bot;

import java.io.IOException;
import jakarta.annotation.Nullable;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.antonleliuk.tgfilecollector.conf.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TelegramFileCollectorTask implements Runnable {

    private final Update update;
    private final TelegramClient client;
    private final TelegramProperties  telegramProperties;

    @SneakyThrows
    @Override
    public void run() {
        log.info("Processing update: {}", update.getUpdateId());
        var message = update.getMessage();
        if (update.hasMessage()) {
            if (telegramProperties.getAllowedUsers().contains(message.getFrom().getUserName())) {
                if (message.hasDocument()) {
                    log.info("Downloading document: {}, for update: {}", message.getDocument(), update.getUpdateId());
                    var document = message.getDocument();
                    String fileId = document.getFileId();

                    client.executeAsync(new GetFile(fileId))
                            .thenCompose(file -> {
                                log.info("Downloading file: {}, for update: {}", file.getFileId(), update.getUpdateId());
                                return client.downloadFileAsync(file);
                            })
                            .thenAccept(file -> {
                                try {
                                    String fileName = document.getFileName();

                                    log.info("Saving file: {}, for update: {}", file, update.getUpdateId());
//                                    Path target = Paths.get(fileName);
//                                    Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
//                                    log.info("File saved to: {}", target.toAbsolutePath());
                                    sendMessage(message.getChatId(), message.getMessageThreadId(), "File saved successfully.");
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .exceptionally(ex -> {
                                log.error("Error processing file", ex);
                                sendMessage(
                                        message.getChatId(),
                                        message.getMessageThreadId(),
                                        "An error occurred while saving the file. Please try again later.");
                                return null;
                            });
                } else {
                    log.info("No document found for download: {}", update.getUpdateId());
                    sendMessage(
                            message.getChatId(),
                            message.getMessageThreadId(),
                            "No document found in your message. Please send a file to process.");
                }
            } else {
                log.info("User {} is not allowed to use bot", message.getFrom().getUserName());
                sendMessage(message.getChatId(), message.getMessageThreadId(), "You are not allowed to use this bot.");
            }
        }
    }

    @SneakyThrows
    private void sendMessage(Long chatId, @Nullable Integer messageThreadId, String text) {
        client.executeAsync(
                SendMessage.builder()
                        .chatId(chatId)
                        .messageThreadId(messageThreadId)
                        .text(text).build()
        );
    }
}
