package me.firephoenix.velocitylogging;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.firephoenix.velocitylogging.listener.ChatCommandListener;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Plugin(
        id = "velocitylogging",
        name = "VelocityLogging",
        version = "1.0"
)
public class VelocityLogging {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static VelocityLogging INSTANCE;
    private final ProxyServer server;
    @Getter
    private final Logger logger;
    public Path dataFolderPath;

    @Inject
    public VelocityLogging(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        this.server = server;
        this.logger = logger;
        this.dataFolderPath = folder;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        setInstance(this);

        server.getEventManager().register(this, new ChatCommandListener());

        createLogFolderAndFileIfItNotExists();
    }

    public void setInstance(VelocityLogging INSTANCE) {
        VelocityLogging.INSTANCE = INSTANCE;
    }

    public void addToLogFile(String messageOrCommand, String playerName, String server) {
        executor.execute(() -> {
            LocalDateTime date = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            String formattedDate = date.format(formatter);

            Path logFilePath = Path.of(dataFolderPath + "/" + formattedDate + ".log");

            if (!logFilePath.toFile().exists()) createLogFolderAndFileIfItNotExists();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true))) {
                String logLine = "[" + playerName + "] " +
                        "[" + server + "] " +
                        "[UTC:" + date.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]:" +
                        messageOrCommand;

                writer.write(logLine);
                writer.newLine();
            } catch (Exception e) {
               e.printStackTrace();
            }
        });
    }

    public void createLogFolderAndFileIfItNotExists() {
        File folder = dataFolderPath.toFile();

        LocalDate date = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String formattedDate = date.format(formatter);

        File file = new File(folder, formattedDate + ".log");

        if (!file.getParentFile().exists()) {
            boolean created = file.getParentFile().mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create directories for log file.");
            }
        }

        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create log file.");
            }
        }
    }

}
