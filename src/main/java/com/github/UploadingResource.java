package com.github;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Krzysztof Ziomek
 * @since 07/06/2016.
 */
@RestController
@RequestMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
public class UploadingResource {

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestPart(value = "filename") String fileName,
                                         @RequestPart(value = "file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File(Application.ROOT + "/" + fileName)));
                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();
            } catch (Exception e) {
                String message = "You failed to upload " + fileName + " => " + e.getMessage();
                return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            String message = "You failed to upload " + fileName + " because the file was empty";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}