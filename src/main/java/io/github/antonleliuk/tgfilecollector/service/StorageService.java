package io.github.antonleliuk.tgfilecollector.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;

import io.github.antonleliuk.tgfilecollector.conf.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class StorageService {

    private final TelegramProperties telegramProperties;

    @SneakyThrows
    public String save(String sourceFilePath, String fileName) {
        Path source = Path.of(sourceFilePath.replaceAll(telegramProperties.getStorage().getTelegramRootDir(), telegramProperties.getStorage().getRootDir()));
        Path target = Paths.get(telegramProperties.getStorage().getRootDir(), "media", fileName);
        log.debug("Creating directory if not exists: {}", target.getParent());
        Files.createDirectories(target.getParent());
        log.debug("Moving file from: {}, to: {}", source, target);
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }
}
