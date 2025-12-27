package io.github.antonleliuk.tgfilecollector.bot;

import jakarta.annotation.Nullable;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.antonleliuk.tgfilecollector.service.SecurityService;
import io.github.antonleliuk.tgfilecollector.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TelegramFileCollectorTask implements Runnable {

    private final Update update;
    private final TelegramClient client;

    private final SecurityService securityService;

    private final StorageService storageService;

    @SneakyThrows
    @Override
    public void run() {
        log.info("Processing update: {}", update.getUpdateId());
        var message = update.getMessage();
        if (update.hasMessage()) {
            if (securityService.isAllowed(update.getMessage())) {
                if (message.hasText() && "/start".equals(message.getText())) {
                    log.info("Sending start message for update: {}", update.getUpdateId());
                    String firstName = message.getFrom().getFirstName();
                    String lastName = message.getFrom().getLastName();
                    String fullName = firstName + (lastName != null ? " " + lastName : "");
                    sendMessage(message.getChatId(), message.getMessageThreadId(), "Welcome, " + fullName + "! Send me any file, and I will save it for you.");
                } else if (message.hasDocument()) {
                    log.info("Downloading document: {}, for update: {}", message.getDocument(), update.getUpdateId());
                    sendMessage(message.getChatId(), message.getMessageThreadId(), "Start saving file.");
                    var document = message.getDocument();
                    String fileId = document.getFileId();

                    client.executeAsync(new GetFile(fileId))
                            .thenAccept(file -> {
                                try {
                                    String fileName = document.getFileName();

                                    log.info("Saving file for update: {}", update.getUpdateId());
                                    // TODO check for virus??
                                    var saved = storageService.save(file.getFilePath(), fileName);
                                    log.info("File saved to: {} for update: {}", saved, update.getUpdateId());
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
