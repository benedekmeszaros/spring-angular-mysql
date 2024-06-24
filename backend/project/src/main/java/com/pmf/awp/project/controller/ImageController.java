package com.pmf.awp.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.service.ImageService;
import com.pmf.awp.project.service.ImageService.Image;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{src}")
    public ResponseEntity<byte[]> getImage(@PathVariable("src") String path) {
        Image image = imageService.load(path);
        MediaType mediaType = null;
        try {
            mediaType = MediaType.valueOf("image/" + image.getExtension());
        } catch (Exception e) {
            throw new GenericHttpException("Invaild MIME type", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(image.getBytes());
    }
}
