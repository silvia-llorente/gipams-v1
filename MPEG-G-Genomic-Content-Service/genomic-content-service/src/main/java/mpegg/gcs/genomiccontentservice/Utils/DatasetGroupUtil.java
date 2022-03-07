package mpegg.gcs.genomiccontentservice.Utils;

import mpegg.gcs.genomiccontentservice.Models.Dataset;
import mpegg.gcs.genomiccontentservice.Models.DatasetGroup;
import mpegg.gcs.genomiccontentservice.Models.MPEGFile;
import mpegg.gcs.genomiccontentservice.Repositories.DatasetGroupRepository;
import mpegg.gcs.genomiccontentservice.Repositories.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class DatasetGroupUtil {

    final FileUtil f = new FileUtil();
    final JWTUtil j = new JWTUtil();
    final MetadataUtil metadataUtil = new MetadataUtil();

    public DatasetGroup addDatasetGroup(Jwt jwt, MultipartFile dg_md, MultipartFile dg_pr, MPEGFile mpegfile, DatasetGroupRepository datasetGroupRepository, int dg_id) throws Exception {
        DatasetGroup dg = new DatasetGroup(mpegfile,j.getUID(jwt));
        dg.setDg_id(dg_id);
        String datasetGroupPath = mpegfile.getPath() + File.separator + "dg_"+dg_id;
        if (!f.validateXML(new StreamSource(new ByteArrayInputStream(dg_md.getBytes())),"DatasetGroup")) throw new Exception("Invalid xml");
        dg = metadataUtil.parseDatasetGroup(new String(dg_md.getBytes()), dg);
        try {
            dg.setPath(datasetGroupPath);
            f.createDirectory(datasetGroupPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error creating datasetgroup");
        }

        try {
            f.createFile(datasetGroupPath + File.separator + "dg_md.xml", new String(dg_md.getBytes()));
            f.createFile(datasetGroupPath + File.separator + "dg_pr.xml", new String(dg_pr.getBytes()));
            datasetGroupRepository.save(dg);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                f.deleteDirectory(datasetGroupPath);
            }
            catch (Exception ignored) {}
            throw new Exception("Error creating datasetgroup");
        }
        return dg;
    }

    public void deleteDatasetGroup(DatasetGroup dg, DatasetGroupRepository datasetGroupRepository) throws Exception {
        try {
            f.deleteDirectory(dg.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error deleting datasetgroup");
        }
        datasetGroupRepository.deleteById(dg.getId());
    }

    public void editDatasetGroup(DatasetGroup dg, MultipartFile dg_md, MultipartFile dg_pr, DatasetGroupRepository datasetGroupRepository) throws Exception {
        if (dg_md != null) {
            if (!f.validateXML(new StreamSource(new ByteArrayInputStream(dg_md.getBytes())),"DatasetGroup")) throw new Exception("Invalid xml");
            f.updateFile(dg.getPath()+File.separator+"dg_md.xml",new String(dg_md.getBytes()));
            dg = metadataUtil.parseDatasetGroup(new String(dg_md.getBytes()), dg);
            datasetGroupRepository.save(dg);
        }
        if (dg_pr != null) {
            f.updateFile(dg.getPath()+File.separator+"dg_pr.xml",new String(dg_pr.getBytes()));
        }
    }

    public String getMetadata(DatasetGroup dg) throws IOException {
        return f.getFile(dg.getPath()+File.separator+"dg_md.xml");
    }

    public String getProtection(DatasetGroup dg) throws IOException {
        return f.getFile(dg.getPath()+File.separator+"dg_pr.xml");
    }
}
