package ru.hogvartz.school.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogvartz.school.Model.Avatar;
import ru.hogvartz.school.Service.AvatarService;

import java.awt.*;
import java.io.*;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "upload/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@RequestParam Long id, @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok("Avatar uploaded successfully");
    }

    @GetMapping("getAvatarFromBD/{id}")
    public ResponseEntity<byte[]> getAvatarFromBD(@PathVariable Long id) throws IOException {
        Avatar avatar = avatarService.getAvatar(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping("getAvatarFromFile/{id}")
    public void getAvatarFromFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.getAvatar(id);

        Path filePath = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(filePath);
             OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os);) {
            response.setContentType(avatar.getMediaType());
            response.setContentLength(avatar.getData().length);
            bis.transferTo(bos);
        }


    }
}
