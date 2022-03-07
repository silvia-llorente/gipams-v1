package mpegg.gcs.genomiccontentservice.Utils;

import mpegg.gcs.genomiccontentservice.Models.Dataset;
import mpegg.gcs.genomiccontentservice.Models.DatasetGroup;
import mpegg.gcs.genomiccontentservice.Repositories.DatasetRepository;
import mpegg.gcs.genomiccontentservice.Repositories.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DatasetUtil {

    final FileUtil f = new FileUtil();
    final JWTUtil j = new JWTUtil();
    final MetadataUtil metadataUtil = new MetadataUtil();

    public Dataset addDataset(Jwt jwt, MultipartFile dt_md, MultipartFile dt_pr, DatasetGroup dg, DatasetRepository datasetRepository, Integer dt_id) throws Exception {
        Dataset d = new Dataset(dg,j.getUID(jwt));
        List<Dataset> datasets = dg.getDatasets();
        d.setDt_id(dt_id);
        String datasetPath = dg.getPath()+ File.separator+"dt_"+dt_id;
        if (dt_md != null) {
            if (!f.validateXML(new StreamSource(new ByteArrayInputStream(dt_md.getBytes())),"Dataset")) throw new Exception("Invalid xml");
            d = metadataUtil.parseDataset(new String(dt_md.getBytes()), d);
        }
        try {
            f.createDirectory(datasetPath);
            d.setPath(datasetPath);
        } catch (Exception e) {
            throw new Exception("Error creating dataset");
        }

        try {
            if (dt_md != null) {
                d.setMetadata(true);
                f.createFile(datasetPath + File.separator + "dt_md.xml", new String(dt_md.getBytes()));
            }

            if (dt_pr != null) {
                d.setProtection(true);
                f.createFile(datasetPath+File.separator+"dt_pr.xml",new String(dt_pr.getBytes()));
            }
            datasetRepository.save(d);
        } catch (Exception e) {
            try {
                f.deleteDirectory(datasetPath);
            } catch (IOException ignored) {}
            throw new Exception("Error creating dataset");
        }
        return d;
    }

    public void deleteDataset(Dataset dt, DatasetRepository datasetRepository) throws Exception {
        try {
            f.deleteDirectory(dt.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error deleting dataset");
        }
        datasetRepository.deleteById(dt.getId());
    }

    public void editDataset(Dataset dt, MultipartFile dt_md, MultipartFile dt_pr, DatasetRepository datasetRepository) throws Exception {
        if (dt_md != null) {
            if (!f.validateXML(new StreamSource(new ByteArrayInputStream(dt_md.getBytes())),"Dataset")) throw new Exception("Invalid xml");
            dt.setMetadata(true);
            f.updateFile(dt.getPath()+File.separator+"dt_md.xml",new String(dt_md.getBytes()));
            dt = metadataUtil.parseDataset(new String(dt_md.getBytes()), dt);
        }
        if (dt_pr != null) {
            dt.setProtection(true);
            f.updateFile(dt.getPath()+File.separator+"dt_pr.xml",new String(dt_pr.getBytes()));
        }
        datasetRepository.save(dt);
    }

    public String getMetadata(Dataset dt) throws IOException {
        return f.getFile(dt.getPath()+File.separator+"dt_md.xml");
    }

    public String getProtection(Dataset dt) throws IOException {
        if (dt.getProtection()) return f.getFile(dt.getPath()+File.separator+"dt_pr.xml");
        return null;
    }


}
