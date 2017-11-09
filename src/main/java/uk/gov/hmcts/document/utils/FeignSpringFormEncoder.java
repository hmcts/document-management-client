package uk.gov.hmcts.document.utils;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.core.io.ByteArrayResource;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (isFormRequest(bodyType)) {
            encodeMultipartFormRequest((Map<String, ?>) object, template);
        } else {
            encodeRequest(object, jsonHeaders, template);
        }
    }

    /**
     * Encodes the request as a multipart form. It can detect a single {@link MultipartFile}, an
     * array of {@link MultipartFile}s, or POJOs (that are converted to JSON).
     *
     * @param formMap
     * @param template
     * @throws EncodeException
     */
    private void encodeMultipartFormRequest(Map<String, ?> formMap, RequestTemplate template) throws EncodeException {
        if (formMap == null) {
            throw new EncodeException("Cannot encode request with null form.");
        }
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (Entry<String, ?> entry : formMap.entrySet()) {
            Object value = ((List) entry.getValue()).get(0);
            if (isMultipartFile(value)) {
                map.add(entry.getKey(), encodeMultipartFile((MultipartFile) value));
            } else if (isMultipartFileArray(value)) {
                encodeMultipartFiles(map, entry.getKey(), Arrays.asList((MultipartFile[]) value));
            } else {
                map.add(entry.getKey(), encodeTextObject(entry.getKey(), value));
            }
        }
        encodeRequest(map, multipartHeaders, template);
    }

    private boolean isMultipartFile(Object object) {
        return object instanceof MultipartFile;
    }

    private boolean isMultipartFileArray(Object o) {
        return o != null && o.getClass().isArray() && MultipartFile.class.isAssignableFrom(o.getClass().getComponentType());
    }

    private HttpEntity<?> encodeMultipartFile(MultipartFile file) {
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.valueOf(file.getContentType()));
//        try {
//            Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(), file.getInputStream());
            return new HttpEntity<>(buildByteArrayResource(file), filePartHeaders);
//        } catch (IOException ex) {
//            throw new EncodeException("Cannot encode request.", ex);
//        }
    }

    private void encodeMultipartFiles(LinkedMultiValueMap<String, Object> map, String name, List<? extends MultipartFile> files) {
        HttpHeaders filePartHeaders = new HttpHeaders();
//        try {
            for (MultipartFile file : files) {
//                Resource multipartFileResource = new MultipartFileResource(file.getOriginalFilename(), file.getSize(), file.getInputStream());
                filePartHeaders.setContentType(MediaType.valueOf(file.getContentType()));
                map.add(name, new HttpEntity<>(buildByteArrayResource(file), filePartHeaders));
            }
//        } catch (IOException ex) {
//            throw new EncodeException("Cannot encode request.", ex);
//        }
    }

    private HttpEntity<?> encodeTextObject(String key, Object object) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData(key, null);
        return new HttpEntity<>(object, headers);
    }

    private void encodeRequest(Object value, HttpHeaders requestHeaders, RequestTemplate template) throws EncodeException {
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
        /*
        we should use a template output stream... this will cause issues if files are too big,
        since the whole request will be in memory.
         */
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

    protected boolean isFormRequest(Type type) {
        return MAP_STRING_WILDCARD.equals(type);
    }

    private static ByteArrayResource buildByteArrayResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    /**
     * Dummy resource class. Wraps file content and its original name.
     */
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
            return super.getInputStream(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public long contentLength() throws IOException {
            return size;
        }

    }

}
