package com.pmf.awp.project.service;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pmf.awp.project.exception.GenericHttpException;

import lombok.Getter;

@Service
public class ImageService {
    @Getter
    public class Image {
        private final String name;
        private final String extension;
        private final byte[] bytes;

        public Image(String name, String extension, byte[] bytes) {
            this.name = name;
            this.extension = extension;
            this.bytes = bytes;
        }
    }

    @Value("${persistence.data-path}")
    private String dataPath;

    private static final String NATIVE_PATH = "http://localhost:7777/images";

    public String save(MultipartFile image, String fileName) {
        File file = new File(getRelativePath(fileName));
        try {
            image.transferTo(file);
            return NATIVE_PATH + "/" + fileName;
        } catch (Exception e) {
            throw new GenericHttpException("Unable to upload image", HttpStatus.BAD_REQUEST);
        }
    }

    public Image load(String filePath) {
        byte[] bytes = null;
        File file = new File(getRelativePath(filePath));
        if (file.exists()) {
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (Exception e) {
                throw new GenericHttpException("Cannot read image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            throw new GenericHttpException("Image not found", HttpStatus.NOT_FOUND);
        return new Image(file.getName(), getFileExtension(filePath), bytes);
    }

    public void delete(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        File file = new File(getRelativePath(fileName));
        try {
            Files.deleteIfExists(file.toPath());
        } catch (Exception e) {
            throw new GenericHttpException("Image not found", HttpStatus.NOT_FOUND);
        }
    }

    private String getRelativePath(String filePath) {
        return dataPath + "/" + filePath;
    }

    private String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return filePath.substring(lastIndexOfDot + 1);
    }
}
