package com.github;

import com.github.io.ByteArrayAsFileResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * @author Krzysztof Ziomek
 * @since 07/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest({"server.port=8080"})
public class UploadingResourceTest {

    @Autowired
    private RestTemplate formRestTemplate;

    private URI FILES_URI = UriComponentsBuilder.fromHttpUrl(LocalClientConfig.API_BASE_URL).path("/files").build().toUri();


    @Test
    public void uploadShouldUploadTemporaryFile() throws IOException {

        // given
        File tmpFile = FileUtils.createTemporaryFile("This is example body of temp file.");
        Resource resource = new FileSystemResource(tmpFile);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = MultipartFormDataBuilder.buildMultipartRequestEntity(new ImmutablePair<>(resource, MediaType.TEXT_PLAIN));

        // when
        ResponseEntity<String> result = formRestTemplate.exchange(FILES_URI, HttpMethod.POST, requestEntity, String.class);
        String message = result.getBody();

        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("File uploaded.", message);

        Path uploadedFilePath = Paths.get(Application.ROOT, tmpFile.getName());
        assertTrue(Files.exists(uploadedFilePath));

        // tear down
        Files.delete(uploadedFilePath);

    }

    @Test
    public void uploadShouldUploadClassPathResource() throws Exception {

        // given
        Resource resource = new ClassPathResource("com/github/file_for_upload_test.txt");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = MultipartFormDataBuilder.buildMultipartRequestEntity(new ImmutablePair<>(resource, MediaType.TEXT_PLAIN));

        // when
        ResponseEntity<String> result = formRestTemplate.exchange(FILES_URI, HttpMethod.POST, requestEntity, String.class);
        String message = result.getBody();

        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("File uploaded.", message);

        Path uploadedFilePath = Paths.get(Application.ROOT, resource.getFilename());
        assertTrue(Files.exists(uploadedFilePath));

        // tear down
        Files.delete(uploadedFilePath);

    }

    @Test
    public void uploadShouldUploadByteArrayAsFile() throws Exception {
        String filename = "file.txt";
        String fileBody = "This is plain String";

        ByteArrayAsFileResource resource = new ByteArrayAsFileResource(fileBody.getBytes(), filename);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = MultipartFormDataBuilder.buildMultipartRequestEntity(new ImmutablePair<>(resource, MediaType.TEXT_PLAIN));

        // execute
        ResponseEntity<String> result = formRestTemplate.exchange(FILES_URI, HttpMethod.POST, requestEntity, String.class);
        String message = result.getBody();

        // assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("File uploaded.", message);

        Path uploadedFilePath = Paths.get(Application.ROOT, resource.getFilename());
        assertTrue(Files.exists(uploadedFilePath));
        assertThat(fileBody.getBytes(), equalTo(Files.readAllBytes(uploadedFilePath)));

        // tear down
        Files.delete(uploadedFilePath);
    }

    @Test
    public void uploadShouldUpload1GBFile() throws Exception {

        // given
        String filename = "huge";
        RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rw");
        randomAccessFile.setLength(1024 * 1024 * 1024);

        Resource resource = new FileSystemResource(new File(filename));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = MultipartFormDataBuilder.buildMultipartRequestEntity(new ImmutablePair<>(resource, MediaType.APPLICATION_OCTET_STREAM));

        // when
        ResponseEntity<String> result = formRestTemplate.exchange(FILES_URI, HttpMethod.POST, requestEntity, String.class);
        String message = result.getBody();

        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("File uploaded.", message);

        Path uploadedFilePath = Paths.get(Application.ROOT, resource.getFilename());
        assertTrue(Files.exists(uploadedFilePath));
        assertEquals(new File(filename).length(), uploadedFilePath.toFile().length());

        // tear down
        Files.delete(Paths.get(filename));
        Files.delete(uploadedFilePath);

    }


}