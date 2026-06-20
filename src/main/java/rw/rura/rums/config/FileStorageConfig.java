package rw.rura.rums.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "rums.files")
@Getter
@Setter
public class FileStorageConfig {

    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir, "clms-documents"));
        Files.createDirectories(Paths.get(uploadDir, "reports"));
    }
}
