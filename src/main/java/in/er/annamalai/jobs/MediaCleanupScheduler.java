package in.er.annamalai.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MediaCleanupScheduler {

    @Value("${media.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_AGE_MS = 30 * 60 * 1000; // 30 minutes

    @Scheduled(fixedRate = 5 * 60 * 1000) // runs every 5 minutes
    public void cleanUpOldFiles() {
        List<File> filesToDelete = getFilesOlderThanMaxAge();
        for (File file : filesToDelete) {
            try {
                Files.delete(file.toPath());
                System.out.println("Deleted file: " + file.getName());
            } catch (IOException e) {
                System.err.println("Error deleting file: " + file.getName());
            }
        }
    }

    private List<File> getFilesOlderThanMaxAge() {
        List<File> filesToDelete = new ArrayList<>();
        File dir = new File(uploadDir);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                long currentTime = System.currentTimeMillis();
                for (File file : files) {
                    if (file.isFile() && currentTime - file.lastModified() > MAX_FILE_AGE_MS) {
                        filesToDelete.add(file);
                    }
                }
            }
        }

        return filesToDelete;
    }
}
