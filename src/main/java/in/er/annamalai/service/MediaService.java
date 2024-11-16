package in.er.annamalai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MediaService {

    @Value("${media.upload-dir}")
    private String uploadDir;

    public void saveFile(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        file.transferTo(filePath);
    }

    public byte[] serveMedia(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        return Files.readAllBytes(filePath);
    }

    public List<String> getAllMediaFiles() {
        File folder = new File(uploadDir);
        File[] listOfFiles = folder.listFiles();
        List<String> mediaFiles = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    mediaFiles.add(file.getName());
                }
            }
        }
        return mediaFiles;
    }
}
