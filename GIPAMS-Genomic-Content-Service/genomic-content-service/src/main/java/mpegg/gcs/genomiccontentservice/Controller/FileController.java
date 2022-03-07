package mpegg.gcs.genomiccontentservice.Controller;

import mpegg.gcs.genomiccontentservice.Models.Dataset;
import mpegg.gcs.genomiccontentservice.Models.DatasetGroup;
import mpegg.gcs.genomiccontentservice.Models.MPEGFile;
import mpegg.gcs.genomiccontentservice.Repositories.DatasetGroupRepository;
import mpegg.gcs.genomiccontentservice.Repositories.DatasetRepository;
import mpegg.gcs.genomiccontentservice.Repositories.MPEGFileRepository;
import mpegg.gcs.genomiccontentservice.Repositories.SampleRepository;
import mpegg.gcs.genomiccontentservice.Utils.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class FileController {
    final String path = "resources/storage";
    final JWTUtil j = new JWTUtil();
    final FileUtil f = new FileUtil();
    final MPEGFileUtil mUtil = new MPEGFileUtil();
    final DatasetGroupUtil dgUtil = new DatasetGroupUtil();
    final DatasetUtil dtUtil = new DatasetUtil();
    @Value("${gcs.url}")
    private final String urlGCS = null;

    @Autowired
    private MPEGFileRepository mpegFileRepository;

    @Autowired
    private DatasetGroupRepository datasetGroupRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private SampleRepository sampleRepository;

    private final AuthorizationUtil authorizationUtil = new AuthorizationUtil();

    //Tested
    @PostMapping("/addFile")
    public ResponseEntity<String> addFile(@AuthenticationPrincipal Jwt jwt, @RequestParam("file_name") String file_name) {
        MPEGFile m = null;
        try {
            m = mUtil.addMpegFile(file_name,jwt,mpegFileRepository);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Error creating file",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(m.getId().toString(),HttpStatus.OK);
    }

    //Tested
    @PostMapping("/addDatasetGroup")
    public ResponseEntity<String> addDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart("dg_md") MultipartFile dg_md, @RequestPart(value = "dt_md", required = false) MultipartFile[] dt_md, @RequestParam("file_id") String file_id, @RequestPart("dg_pr") MultipartFile dg_pr) {
        Long file_idL = Long.parseLong(file_id);
        Optional<MPEGFile> fileOptional = mpegFileRepository.findById(file_idL);
        MPEGFile file = null;
        if (fileOptional.isPresent()) {
            file = fileOptional.get();
        }
        else return new ResponseEntity<String>("File doesn't exist",HttpStatus.NOT_ACCEPTABLE);
        DatasetGroup dg = null;
        ArrayList<Integer> a = (ArrayList<Integer>) datasetGroupRepository.getMaxDgId(file.getId());
        int dg_id = 0;
        if (a.size() != 0 && a.get(0) != null) dg_id = a.get(0)+1;
        try {
            dg = dgUtil.addDatasetGroup(jwt,dg_md,dg_pr,file,datasetGroupRepository,dg_id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (dt_md != null) {
            int dt_id = 0;
            for (MultipartFile dt : dt_md) {
                ResponseEntity<String> r = addDataset(jwt, dt, null, dg.getId().toString(), dt_id);
                ++dt_id;
            }
        }
        return new ResponseEntity<String>(dg.getId().toString(),HttpStatus.OK);
    }

    private ResponseEntity<String> addDataset(Jwt jwt, MultipartFile dt_md, MultipartFile dt_pr, String dg_id, Integer dt_id) {
        Long dg_idL = Long.parseLong(dg_id);
        Optional<DatasetGroup> datasetGroupOptional = datasetGroupRepository.findById(dg_idL);
        if (datasetGroupOptional.isPresent()) {
            DatasetGroup dg = datasetGroupOptional.get();
            if (dt_id == null) {
                dt_id = 0;
                ArrayList<Integer> a = (ArrayList<Integer>) datasetRepository.getMaxDtId(dg.getId());
                if (a.size() != 0 && a.get(0) != null) dt_id = a.get(0) + 1;
            }
            Dataset dt = null;
            try {
                dt = dtUtil.addDataset(jwt, dt_md, dt_pr, dg, datasetRepository, dt_id);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<String>(dt.getId().toString(),HttpStatus.OK);
        }
        return new ResponseEntity<String>("DatasetGroup doesn't exist",HttpStatus.NOT_ACCEPTABLE);
    }

    //Tested
    @PostMapping("/addDataset")
    public ResponseEntity<String> addDatasetAux(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dt_md", required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr", required = false) MultipartFile dt_pr, @RequestPart("dg_id") String dg_id) {
        try {
            return addDataset(jwt,dt_md,dt_pr,dg_id,null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Tested
    @PostMapping("/editDatasetGroup")
    public ResponseEntity<String> editDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dg_md", required = false) MultipartFile dg_md, @RequestPart(value = "dg_pr",required = false) MultipartFile dg_pr, @RequestPart(value = "dg_id") String dg_id) {
        Long dg_idL = Long.parseLong(dg_id);
        Optional<DatasetGroup> datasetGroupOptional = datasetGroupRepository.findById(dg_idL);
        if (datasetGroupOptional.isPresent()) {
            DatasetGroup dg = datasetGroupOptional.get();
            try {
                dgUtil.editDatasetGroup(dg,dg_md,dg_pr,datasetGroupRepository);
                return new ResponseEntity<String>("ok",HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<String>("DatasetGroup doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    //Tested
    @PostMapping("/editDataset")
    public ResponseEntity<String> editDataset(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dt_md", required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr",required = false) MultipartFile dt_pr, @RequestPart("dt_id") String dt_id) {
        Long dt_idL = Long.parseLong(dt_id);
        Optional<Dataset> datasetOptional = datasetRepository.findById(dt_idL);
        if (datasetOptional.isPresent()) {
            Dataset dt = datasetOptional.get();
            try {
                dtUtil.editDataset(dt,dt_md,dt_pr,datasetRepository);
                return new ResponseEntity<String>("ok",HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>(e.toString(),HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return new ResponseEntity<String>("Dataset doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@AuthenticationPrincipal Jwt jwt, @RequestParam("file_id") String file_id) {
        Long file_idL = Long.parseLong(file_id);
        Optional<MPEGFile> mpegFileOptional = mpegFileRepository.findById(file_idL);
        if (mpegFileOptional.isPresent()) {
            MPEGFile mpegfile = mpegFileOptional.get();
            if (mpegfile.getDatasetGroups() != null) {
                for (DatasetGroup dg : mpegfile.getDatasetGroups()) {
                    deleteDatasetGroup(jwt,dg.getId().toString());
                }
            }
            try {
                mUtil.deleteMpegFile(mpegfile,mpegFileRepository);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<String>("ok",HttpStatus.OK);
        }
        return new ResponseEntity<String>("File doesn't exist",HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/deleteDatasetGroup")
    public ResponseEntity<String> deleteDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestParam("dg_id") String dg_id) {
        Long dg_idL = Long.parseLong(dg_id);
        Optional<DatasetGroup> datasetGroupOptional = datasetGroupRepository.findById(dg_idL);
        if (datasetGroupOptional.isPresent()) {
            DatasetGroup dg = datasetGroupOptional.get();
            if (dg.getDatasets() != null) {
                for (Dataset dt : dg.getDatasets()) {
                    deleteDataset(jwt,dt.getId().toString());
                }
                try {
                    dgUtil.deleteDatasetGroup(dg,datasetGroupRepository);
                } catch (Exception e) {
                    return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<String>("ok",HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("DatasetGroup doesn't exist",HttpStatus.NOT_ACCEPTABLE);
    }

    @DeleteMapping("/deleteDataset")
    public ResponseEntity<String> deleteDataset(@AuthenticationPrincipal Jwt jwt, @RequestParam("dt_id") String dt_id) {
        Long dt_idL = Long.parseLong(dt_id);
        Optional<Dataset> datasetOptional = datasetRepository.findById(dt_idL);
        if (datasetOptional.isPresent()) {
            Dataset dt = datasetOptional.get();
            try {
                dtUtil.deleteDataset(dt,datasetRepository);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<String>("ok",HttpStatus.OK);
        }
        return new ResponseEntity<String>("DatasetGroup doesn't exist",HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/mpegfile/{file_id}")
    public ResponseEntity<JSONObject> getFile(@AuthenticationPrincipal Jwt jwt, @PathVariable("file_id") String file_id) {
        Long file_idL = Long.parseLong(file_id);
        Optional<MPEGFile> fileOptional = mpegFileRepository.findById(file_idL);
        MPEGFile file = null;
        if (fileOptional.isPresent()) {
            file = fileOptional.get();
            JSONObject jo = new JSONObject();
            jo.put("name",file.getName());
            jo.put("id",file.getId());
            JSONArray ja = new JSONArray();
            if (file.getDatasetGroups() != null) {
                for (DatasetGroup dg : file.getDatasetGroups()) {
                    JSONObject dg_jo = new JSONObject();
                    boolean authorized = authorizationUtil.authorized(urlGCS, "dg", String.valueOf(dg.getId()), jwt, "GetIdDatasetGroup", datasetGroupRepository, datasetRepository, mpegFileRepository);
                    if (authorized) {
                        dg_jo.put("id",dg.getId());
                        dg_jo.put("dg_id",dg.getDg_id());
                        ja.add(dg_jo);
                    }
                }
            }
            jo.put("dg",ja);
            return new ResponseEntity<JSONObject>(jo,HttpStatus.OK);
        }
        return new ResponseEntity<JSONObject>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/dg/{dg_id}/{resource}")
    public ResponseEntity<JSONObject> getDatasetGroup(@AuthenticationPrincipal Jwt jwt, @PathVariable("dg_id") String dg_id, @PathVariable("resource") String resource) {
        Long dg_idL = Long.parseLong(dg_id);
        Optional<DatasetGroup> datasetGroupOptional = datasetGroupRepository.findById(dg_idL);
        JSONObject jo = new JSONObject();
        if (datasetGroupOptional.isEmpty()) {
            return new ResponseEntity<JSONObject>(HttpStatus.NOT_ACCEPTABLE);
        }
        DatasetGroup dg = datasetGroupOptional.get();
        switch (resource) {
            case "datasets":
                JSONArray ja = new JSONArray();
                if (dg.getDatasets() != null) {
                    for (Dataset dt : dg.getDatasets()) {
                        JSONObject dt_jo = new JSONObject();
                        boolean authorized = authorizationUtil.authorized(urlGCS, "dt", String.valueOf(dt.getId()), jwt, "GetIdDataset", datasetGroupRepository, datasetRepository, mpegFileRepository);
                        if (authorized) {
                            dt_jo.put("id",dt.getId());
                            dt_jo.put("dt_id",dt.getDt_id());
                            ja.add(dt_jo);
                        }
                    }
                }
                jo.put("dt", ja);
                break;
            case "metadata":
                try {
                    jo.put("data",dgUtil.getMetadata(dg));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "protection":
                try {
                    jo.put("data",dgUtil.getProtection(dg));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            default:
                return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<JSONObject>(jo,HttpStatus.OK);
    }

    @GetMapping("/dt/{dt_id}/{resource}")
    public ResponseEntity<JSONObject> getDataset(@AuthenticationPrincipal Jwt jwt, @PathVariable("dt_id") String dt_id, @PathVariable("resource") String resource) {
        Long dt_idL = Long.parseLong(dt_id);
        Optional<Dataset> datasetOptional = datasetRepository.findById(dt_idL);
        JSONObject jo = new JSONObject();
        if (datasetOptional.isEmpty()) {
            return new ResponseEntity<JSONObject>(HttpStatus.NOT_ACCEPTABLE);
        }
        Dataset dt = datasetOptional.get();
        switch (resource) {
            case "metadata":
                try {
                    jo.put("data",dtUtil.getMetadata(dt));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "protection":
                try {
                    jo.put("data",dtUtil.getProtection(dt));
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            default:
                return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<JSONObject>(jo,HttpStatus.OK);
    }

    @GetMapping("/ownFiles")
    public List<MPEGFile> getFiles(@AuthenticationPrincipal Jwt jwt) {
        return mpegFileRepository.findByOwner(j.getUID(jwt));
    }
}