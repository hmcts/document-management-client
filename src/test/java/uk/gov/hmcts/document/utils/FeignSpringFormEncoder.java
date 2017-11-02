package uk.gov.hmcts.document.utils;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FeignSpringFormEncoder implements Encoder {

    private final List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
    private final HttpHeaders multipartHeaders = new HttpHeaders();
    private final HttpHeaders jsonHeaders = new HttpHeaders();

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public FeignSpringFormEncoder() {
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (isFormRequest(bodyType)) {
            encodeMultipartFormRequest((Map<String, ?>) object, template);
        } else {
            encodeRequest(object, jsonHeaders, template);
        }
    }

    private void encodeMultipartFormRequest(Map<String, ?> formMap, RequestTemplate template) throws EncodeException {
        if (formMap == null) {
            throw new EncodeException("Cannot encode request with null form.");
        }
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (Entry<String, ?> entry : formMap.entrySet()) {
            Object value = entry.getValue();
            if (isMultipartFile(value)) {
                map.add(entry.getKey(), encodeMultipartFile((MultipartFile) value));
            } else if (isMultipartFileArray(value)) {
                encodeMultipartFiles(map, entry.getKey(), Arrays.asList((MultipartFile[]) value));
            } else {
                map.add(entry.getKey(), encodeJsonObject(value));
            }
        }
        encodeRequest(map, multipartHeaders, template);
    }

    private boolean isMultipartFile(Object object) {
        return object instanceof MultipartFile;
    }

    private boolean isMultipartFileArray(Object object) {
        return object != null
            && object.getClass().isArray()
            && MultipartFile.class.isAssignableFrom(object.getClass().getComponentType());
    }

    private HttpEntity<?> encodeMultipartFile(MultipartFile file) {
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(),
                file.getSize(), file.getInputStream());
            return new HttpEntity<>(multipartFileResource, filePartHeaders);
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
    }

    private void encodeMultipartFiles(LinkedMultiValueMap<String, Object> map,
                                      String name, List<? extends MultipartFile> files) {
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            for (MultipartFile file : files) {
                Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(),
                    file.getSize(), file.getInputStream());
                map.add(name, new HttpEntity<>(multipartFileResource, filePartHeaders));
            }
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
    }

    private HttpEntity<?> encodeJsonObject(Object object) {
        HttpHeaders jsonPartHeaders = new HttpHeaders();
        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(object, jsonPartHeaders);
    }

    private void encodeRequest(Object value, HttpHeaders requestHeaders,
                               RequestTemplate template) throws EncodeException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpOutputMessage dummyRequest = new HttpOutputMessageImpl(outputStream, requestHeaders);
        try {
            Class<?> requestType = value.getClass();
            MediaType requestContentType = requestHeaders.getContentType();
            for (HttpMessageConverter<?> messageConverter : converters) {
                if (messageConverter.canWrite(requestType, requestContentType)) {
                    ((HttpMessageConverter<Object>) messageConverter).write(
                        value, requestContentType, dummyRequest);
                    break;
                }
            }
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
        HttpHeaders headers = dummyRequest.getHeaders();
        if (headers != null) {
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                template.header(entry.getKey(), entry.getValue());
            }
        }

        template.body(outputStream.toByteArray(), UTF_8);
    }

    private class HttpOutputMessageImpl implements HttpOutputMessage {

        private final OutputStream body;
        private final HttpHeaders headers;

        public HttpOutputMessageImpl(OutputStream body, HttpHeaders headers) {
            this.body = body;
            this.headers = headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

    }

    static boolean isFormRequest(Type type) {
        return MAP_STRING_WILDCARD.equals(type);
    }

    static class MultipartFileResource extends InputStreamResource {

        private final String filename;
        private final long size;

        public MultipartFileResource(String filename, long size, InputStream inputStream) {
            super(inputStream);
            this.size = size;
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public InputStream getInputStream() throws IOException, IllegalStateException {
            return super.getInputStream();
        }

        @Override
        public long contentLength() throws IOException {
            return size;
        }

    }

}
