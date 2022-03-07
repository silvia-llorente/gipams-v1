package mpegg.workflowmanager.workflowmanager.Controllers;

import mpegg.workflowmanager.workflowmanager.Models.DatasetGroup;
import mpegg.workflowmanager.workflowmanager.Repositories.*;
import mpegg.workflowmanager.workflowmanager.Utils.AuthorizationUtil;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class GCSController {

    @Autowired
    private MPEGFileRepository mpegFileRepository;

    @Autowired
    private DatasetGroupRepository datasetGroupRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Value("${gcs.url}")
    private final String urlGCS = null;
    private final AuthorizationUtil authorizationUtil = new AuthorizationUtil();

    @PostMapping("/addFile")
    public ResponseEntity<String> addFile(@AuthenticationPrincipal Jwt jwt, @RequestParam("file_name") String file_name) {
        if (!file_name.matches("(?:[?\\dA-Za-zÀ-ÿ0-9])+$")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (file_name.length() > 15) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_name", file_name);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(urlGCS + "/api/v1/addFile", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addDatasetGroup")
    public ResponseEntity<String> addDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart("dg_md") MultipartFile dg_md, @RequestPart("dg_pr") MultipartFile dg_pr, @RequestPart(value = "dt_md", required = false) MultipartFile[] dt_md, @RequestPart("file_id") String file_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "file", file_id, jwt, null, datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        JSONObject a = (JSONObject) jwt.getClaims().get("realm_access");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_id", file_id);
        body.add("dg_md", dg_md.getResource());
        body.add("dg_pr", dg_pr.getResource());
        if (dt_md != null) {
            for (MultipartFile file : dt_md) {
                body.add("dt_md", file.getResource());
            }
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(urlGCS + "/api/v1/addDatasetGroup", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(response.getBody(), HttpStatus.OK);
    }

    @PostMapping("/addDataset")
    public ResponseEntity<String> addDataset(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dt_md", required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr", required = false) MultipartFile dt_pr, @RequestPart("dg_id") String dg_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "dg", dg_id, jwt, "AddDataset", datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        if (dt_md == null && dt_pr == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (dt_md != null) body.add("dt_md", dt_md.getResource());
        if (dt_pr != null) body.add("dt_pr", dt_pr.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(urlGCS + "/api/v1/addDataset", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(response.getBody(), HttpStatus.OK);
    }

    @PostMapping("/editDatasetGroup")
    public ResponseEntity<String> editDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dg_md", required = false) MultipartFile dg_md, @RequestPart(value = "dg_pr", required = false) MultipartFile dg_pr, @RequestPart(value = "dg_id") String dg_id) {
        if (dg_md == null && dg_pr == null) return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        boolean authorized = true;
        if (dg_md != null) {
            authorized = authorized && authorizationUtil.authorized(urlGCS, "dg", dg_id, jwt, "EditMetadataDatasetGroup", datasetGroupRepository, datasetRepository, mpegFileRepository);
            body.add("dg_md", dg_md.getResource());
        }
        if (dg_pr != null) {
            authorized = authorized && authorizationUtil.authorized(urlGCS, "dg", dg_id, jwt, "EditProtectionDatasetGroup", datasetGroupRepository, datasetRepository, mpegFileRepository);
            body.add("dg_pr", dg_pr.getResource());
        }
        if (!authorized) return new ResponseEntity<String>("Not authorized",HttpStatus.FORBIDDEN);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/editDatasetGroup", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @PostMapping("/editDataset")
    public ResponseEntity<String> editDataset(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dt_md", required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr", required = false) MultipartFile dt_pr, @RequestPart(value = "dt_id") String dt_id) {
        if (dt_md == null && dt_pr == null) return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dt_id", dt_id);
        boolean authorized = true;
        if (dt_md != null) {
            authorized = authorized && authorizationUtil.authorized(urlGCS, "dt", dt_id, jwt, "EditMetadataDataset", datasetGroupRepository, datasetRepository, mpegFileRepository);
            body.add("dt_md", dt_md.getResource());
        }
        if (dt_pr != null) {
            authorized = authorized && authorizationUtil.authorized(urlGCS, "dt", dt_id, jwt, "EditProtectionDataset", datasetGroupRepository, datasetRepository, mpegFileRepository);
            body.add("dt_pr", dt_pr.getResource());
        }
        if (!authorized) return new ResponseEntity<String>("Not authorized",HttpStatus.FORBIDDEN);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/editDataset", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@AuthenticationPrincipal Jwt jwt, @RequestParam("file_id") String file_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "file", file_id, jwt, null, datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_id", file_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/deleteFile", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("ok", HttpStatus.OK);

    }

    @DeleteMapping("/deleteDatasetGroup")
    public ResponseEntity<String> deleteDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestParam("dg_id") String dg_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "dg", dg_id, jwt, "deleteDatasetGroup", datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/deleteDatasetGroup", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @DeleteMapping("/deleteDataset")
    public ResponseEntity<String> deleteDataset(@AuthenticationPrincipal Jwt jwt, @RequestParam("dt_id") String dt_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "dt", dt_id, jwt, "deleteDataset", datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dt_id", dt_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/deleteDataset", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }

    @GetMapping("/mpegfile/{file_id}")
    public ResponseEntity<JSONObject> getFile(@AuthenticationPrincipal Jwt jwt, @PathVariable("file_id") String file_id) {
        boolean authorized = authorizationUtil.authorized(urlGCS, "file", file_id, jwt, null, datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<JSONObject>(HttpStatus.FORBIDDEN);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        try {
            response = restTemplate.exchange(urlGCS + "/api/v1/mpegfile/" + file_id, HttpMethod.GET, entity, JSONObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/dg/{dg_id}/{resource}")
    public ResponseEntity<JSONObject> getDatasetGroup(@AuthenticationPrincipal Jwt jwt, @PathVariable("dg_id") String dg_id, @PathVariable("resource") String resource) {
        String action = null;
        switch (resource) {
            case "metadata":
                action = "GetMetadataDatasetGroup";
                break;
            case "protection":
                action = "GetProtectionDatasetGroup";
                break;
            case "datasets":
                Long dgIdL = Long.parseLong(dg_id);
                Optional<DatasetGroup> dgOpt = datasetGroupRepository.findById(dgIdL);
                if (dgOpt.isEmpty()) return new ResponseEntity<JSONObject>(HttpStatus.FORBIDDEN);
                break;
            default:
                return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
        }
        if (action != null) {
            boolean authorized = authorizationUtil.authorized(urlGCS, "dg", dg_id, jwt, action, datasetGroupRepository, datasetRepository, mpegFileRepository);
            if (!authorized) return new ResponseEntity<JSONObject>(HttpStatus.FORBIDDEN);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        try {
            response = restTemplate.exchange(urlGCS + "/api/v1/dg/" + dg_id + "/" + resource, HttpMethod.GET, entity, JSONObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/dt/{dt_id}/{resource}")
    public ResponseEntity<JSONObject> getDataset(@AuthenticationPrincipal Jwt jwt, @PathVariable("dt_id") String dt_id, @PathVariable("resource") String resource) {
        String action = null;
        switch (resource) {
            case "metadata":
                action = "GetMetadataDataset";
                break;
            case "protection":
                action = "GetProtectionDataset";
                break;
            default:
                return new ResponseEntity<JSONObject>(HttpStatus.BAD_REQUEST);
        }
        boolean authorized = authorizationUtil.authorized(urlGCS, "dt", dt_id, jwt, action, datasetGroupRepository, datasetRepository, mpegFileRepository);
        if (!authorized) return new ResponseEntity<JSONObject>(HttpStatus.FORBIDDEN);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        try {
            response = restTemplate.exchange(urlGCS + "/api/v1/dt/" + dt_id + "/" + resource, HttpMethod.GET, entity, JSONObject.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONObject>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}