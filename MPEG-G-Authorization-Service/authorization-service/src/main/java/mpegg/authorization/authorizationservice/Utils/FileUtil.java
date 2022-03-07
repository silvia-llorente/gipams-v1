package mpegg.authorization.authorizationservice.Utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class FileUtil {
    public String randomFileName() {
        String uuid = UUID.randomUUID().toString();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return uuid + sdf.format(cal.getTime());
    }

    public void createDirectory(String path) throws FileAlreadyExistsException {
        boolean created = new File(path).mkdirs();
        if (!created) {
            throw new FileAlreadyExistsException("Directory already exists");
        }
    }

    public void deleteDirectory(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }

    public void createFile(String path, String content) throws IOException {
        File f = new File(path);
        if (f.createNewFile()) {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        }
        else {
            throw new FileAlreadyExistsException("File already exists");
        }
    }
}
