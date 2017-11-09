package uk.gov.hmcts.document.utils;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MultiValueMapEncoder extends FeignSpringFormEncoder {

    public static final String FILES_PARAMETER = "files";

    public MultiValueMapEncoder() {
        super();
    }

    @Override
    protected boolean isFormRequest(final Type type) {
        return true;
    }

    private boolean isMultipartFile(Object object) {
        return object instanceof MultipartFile;
    }

    public static MultiValueMap<String, Object> prepareRequest(List<MultipartFile> files) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        files.stream()
            .map(MultiValueMapEncoder::buildPartFromFile)
            .forEach(file -> parameters.add(FILES_PARAMETER, file));
        return parameters;
    }

    private static HttpEntity<Resource> buildPartFromFile(MultipartFile file) {
        return new HttpEntity<>(buildByteArrayResource(file), buildPartHeaders(file));
    }

    private static HttpHeaders buildPartHeaders(MultipartFile file) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(buildMediaType(file.getContentType()));
        return headers;
    }

    private static MediaType buildMediaType(String contentType) {
        final String[] splitz = contentType.split("/");
        if (splitz.length > 1) {
            return new MediaType(splitz[0], splitz[1]);
        }
        return new MediaType(splitz[0]);
    }

    private static ByteArrayResource buildByteArrayResource(MultipartFile file) {
        /*
         * We need to override the getFileName method to return something
         * otherwise spring calls this method, gets null and throws a NPE.
         * If you leave this out then you end up hitting the wrong EM end point
         * because the list of files is mapped properly.
         */
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
