package com.github.io;

import org.springframework.core.io.ByteArrayResource;

/**
 * @author Krzysztof Ziomek
 * @since 07/06/2016.
 */
public class ByteArrayAsFileResource extends ByteArrayResource {

    public ByteArrayAsFileResource(byte[] byteArray, String description) {
        super(byteArray, description);
    }

    @Override
    public String getFilename() {
        return getDescription().replaceAll(".*\\[|\\].*", "");
    }
}
