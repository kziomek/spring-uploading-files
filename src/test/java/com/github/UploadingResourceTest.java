package com.github;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
import java.net.URI;

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

        // then
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());

    }


}