package in.er.annamalai.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.er.annamalai.service.MediaService;

@Controller
public class MediaController {

    private final MediaService mediaService;

    // The directory where files are uploaded
    @Value("${media.upload-dir}")
    private String uploadDir;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // Display upload form and media list
    @GetMapping("/")
    public String showMediaPage(Model model) {
        List<String> mediaFiles = mediaService.getAllMediaFiles();
        model.addAttribute("mediaFiles", mediaFiles);
        return "media-list-upload"; // Thymeleaf template for the page
    }

    // Endpoint to handle file upload
    @PostMapping("/upload")
    public String uploadMedia(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload.");
                return "media-list-upload"; // Return to the same page with error
            }

            mediaService.saveFile(file); // Service method to save the file
            return "redirect:/"; // Redirect to refresh the list after upload
        } catch (IOException e) {
            model.addAttribute("error", "Error uploading the file.");
            return "media-list-upload"; // Return to the same page with error
        }
    }

    // Endpoint to serve media files (images, videos, audio)
    @GetMapping("/serve/{filename}")
    public ResponseEntity<InputStreamResource> serveFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        File file = filePath.toFile();

        // Set the appropriate content type
        String contentType = URLConnection.guessContentTypeFromName(file.getName());

        // Return the file as a stream
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .contentLength(file.length())
                            .body(resource);
    }

    // Endpoint to show list of all uploaded media files
    @GetMapping("/list")
    public String listAllMediaFiles(Model model) {
        List<String> mediaFiles = getAllMediaFiles();
        model.addAttribute("mediaFiles", mediaFiles); // Add list of media files to model
        return "media-list"; // Return the media list page
    }

    // Helper method to get all media files from the directory
    private List<String> getAllMediaFiles() {
        List<String> mediaFiles = new ArrayList<>();
        File folder = new File(uploadDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    mediaFiles.add(file.getName()); // Add file name to the list
                }
            }
        }
        return mediaFiles;
    }
}
