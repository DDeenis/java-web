package step.learning.services.formparse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MixedFormParseService implements FormParseService {
    private static final int MEMORY_THRESHOLD = 5 * 1024 * 1024;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MAX_FORM_SIZE = 20 * 1024 * 1024;

    private final ServletFileUpload fileUpload;

    @Inject
    public MixedFormParseService() {
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(MEMORY_THRESHOLD);
        fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        fileItemFactory.setDefaultCharset(StandardCharsets.UTF_8.name());

        fileUpload = new ServletFileUpload(fileItemFactory);
        fileUpload.setFileSizeMax(MAX_FILE_SIZE);
        fileUpload.setSizeMax(MAX_FORM_SIZE);
    }

    @Override
    public FormParseResult parse(HttpServletRequest request) {
        final Map<String, String> fields = new HashMap<>();
        final Map<String, FileItem> files = new HashMap<>();

        boolean isMultipart = request.getHeader("Content-Type").startsWith("multipart/form-data");
        boolean isUrlEncoded = request.getHeader("Content-Type").startsWith("application/x-www-form-urlencoded");
        String charsetName = (String)request.getAttribute("charsetName");
        charsetName = charsetName == null ? StandardCharsets.UTF_8.name() : charsetName;

        if(isMultipart) {
            try {
                List<FileItem> items = fileUpload.parseRequest(request);
                for(FileItem item : items) {
                    if(item.isFormField()) {
                        fields.put(item.getFieldName(), item.getString(charsetName));
                    }
                    else {
                        files.put(item.getFieldName(), item);
                    }
                }
            } catch (FileUploadException | java.io.UnsupportedEncodingException  e) {
                throw new RuntimeException(e);
            }
        }
        else if(isUrlEncoded) {
            Enumeration<String> paramsNames = request.getParameterNames();
            while (paramsNames.hasMoreElements()) {
                String name = paramsNames.nextElement();
                fields.put(name, request.getParameter(name));
            }
        }

        return new FormParseResult() {
            @Override
            public Map<String, String> getFields() {
                return fields;
            }

            @Override
            public Map<String, FileItem> getFiles() {
                return files;
            }
        };
    }
}
