package mpegg.gcs.genomiccontentservice.Utils;

import mpegg.gcs.genomiccontentservice.Models.MPEGFile;
import mpegg.gcs.genomiccontentservice.Repositories.MPEGFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class MPEGFileUtil {

    final FileUtil f = new FileUtil();
    final JWTUtil j = new JWTUtil();
    final String path = "resources/storage";

    public MPEGFile addMpegFile(String name, Jwt jwt, MPEGFileRepository mpegFileRepository) throws Exception {
        MPEGFile mpegfile = new MPEGFile(j.getUID(jwt),name);
        mpegFileRepository.save(mpegfile);
        String newPath = path+ File.separator+mpegfile.getId();
        mpegfile.setPath(newPath);
        try {
            f.createDirectory(newPath);
            mpegFileRepository.save(mpegfile);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                f.deleteDirectory(newPath);
            } catch (Exception ignored){}
            mpegFileRepository.delete(mpegfile);
            throw new Exception("Error creating the file");
        }
        return mpegfile;
    }

    public void deleteMpegFile(MPEGFile file, MPEGFileRepository mpegFileRepository) throws Exception {
        try {
            f.deleteDirectory(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error deleting file");
        }
        mpegFileRepository.deleteById(file.getId());
    }


}
