package ru.hogvartz.school.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogvartz.school.Model.Avatar;
import ru.hogvartz.school.Model.Student;
import ru.hogvartz.school.Repository.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class AvatarService {
    private final StudentService studentService;
    private final AvatarRepository avatarRepository;
    @Value("${avatar.dir.path}")
    private String avatarDir;

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.getStudent(studentId);

        Path filePath = Path.of(avatarDir, studentId + getExtention(Objects.requireNonNull(file.getOriginalFilename())));

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = Files.newOutputStream(filePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024))
        {
            bufferedInputStream.transferTo(bufferedOutputStream);
        }

        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(saveAvatar(filePath));

        avatarRepository.save(avatar);
    }

    public byte[] downloadAvatar(Long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);
        return avatar.getData();
    }

    public Avatar getAvatar(Long studentId) throws IOException {
        return avatarRepository.findByStudentId(studentId).orElse(null);
    }

    private byte[] saveAvatar(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
        {
            return byteArrayOutputStream.toByteArray();
        }
    }

    private String getExtention(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }
}
