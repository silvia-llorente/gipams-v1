package mpegg.gcs.genomiccontentservice.Utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class FileUtil {
    private static String dgXSD = "src/main/resources/schemas/ISOIEC_FDIS_23092-3_Annex_A1_dgmd_schema.xsd";
    private static Schema dgSchema = null;
    private static String dtXSD = "src/main/resources/schemas/ISOIEC_FDIS_23092-3_Annex_A2_dtmd_schema.xsd";
    private static Schema dtSchema = null;

    public FileUtil() {
        try {
            if (dgSchema == null) dgSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(dgXSD));
            if (dtSchema == null)  dtSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(dtXSD));
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

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

    public void updateFile(String path, String content) throws IOException {
        File f = new File(path);
        f.createNewFile();
        FileWriter writer = new FileWriter(path);
        writer.write(content);
        writer.close();
    }

    public String getFile(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    public boolean validateXML(StreamSource xml, String type) throws SAXException {
        Schema xsd = null;
        switch (type) {
            case "DatasetGroup":
                xsd = dgSchema;
                break;
            case "Dataset":
                xsd = dtSchema;
                break;
            default:
                return false;
        }
        Validator validator = xsd.newValidator();
        try {
            validator.validate(xml);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
