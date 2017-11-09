package uk.gov.hmcts.document.domain;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.form.spring.SpringMultipartEncodedDataProcessor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiValueMapEncoder extends SpringFormEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiValueMapEncoder.class);

    private final Encoder delegate;

    public MultiValueMapEncoder() {
        this(new Encoder.Default());
    }

    public MultiValueMapEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (!bodyType.equals(MultiValueMap.class)) {
            delegate.encode(object, bodyType, template);
            return;
        }

        MultiValueMap<String, Object> map = (MultiValueMap<String, Object>) object;
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, List<Object>> entry : map.entrySet()) {

            if (entry.getKey().equals("files")) {
                MultipartFile multipartFile = (MultipartFile) entry.getValue().get(0);
                final File file = new File(multipartFile.getOriginalFilename());
                try {
                    FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
                } catch (IOException exception) {
                    LOGGER.error("ERROR: ", exception);
                }

                data.put(multipartFile.getName(), file);
            } else {
                data.put(entry.getKey(), entry.getValue().get(0));

            }
        }
        new SpringMultipartEncodedDataProcessor().process(data, template);
    }
}
