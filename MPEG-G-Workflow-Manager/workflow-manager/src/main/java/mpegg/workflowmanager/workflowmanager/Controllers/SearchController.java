package mpegg.workflowmanager.workflowmanager.Controllers;

import mpegg.workflowmanager.workflowmanager.Repositories.DatasetGroupRepository;
import mpegg.workflowmanager.workflowmanager.Repositories.DatasetRepository;
import mpegg.workflowmanager.workflowmanager.Repositories.MPEGFileRepository;
import mpegg.workflowmanager.workflowmanager.Utils.AuthorizationUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    @Autowired
    private MPEGFileRepository mpegFileRepository;

    @Autowired
    private DatasetGroupRepository datasetGroupRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Value("${search.url}")
    private final String urlSearch = null;

    @PostMapping("/findDatasetGroupMetadata")
    private ResponseEntity<JSONArray> findDatasetGroupMetadata(@AuthenticationPrincipal Jwt jwt,
                                                               @RequestParam(value = "center", required = false) String center,
                                                               @RequestParam(value = "description", required = false) String description,
                                                               @RequestParam(value = "title", required = false) String title,
                                                               @RequestParam(value = "type", required = false) String type)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("center", center);
        body.add("description", description);
        body.add("title", title);
        body.add("type", type);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(urlSearch + "/api/v1/findDatasetGroupMetadata", HttpMethod.POST, requestEntity, JSONArray.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONArray>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/findDatasetMetadata")
    private ResponseEntity<JSONArray> findDatasetMetadata(@AuthenticationPrincipal Jwt jwt,
                                                               @RequestParam(value = "title", required = false) String title,
                                                               @RequestParam(value = "taxon_id", required = false) String taxon_id)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", title);
        body.add("taxon_id", taxon_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(urlSearch + "/api/v1/findDatasetMetadata", HttpMethod.POST, requestEntity, JSONArray.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONArray>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/ownFiles")
    private ResponseEntity<JSONArray> ownFiles(@AuthenticationPrincipal Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONArray> response = null;
        try {
            response = restTemplate.exchange(urlSearch + "/api/v1/ownFiles/", HttpMethod.GET, entity, JSONArray.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONArray>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping("/ownDatasetGroup")
    private ResponseEntity<JSONArray> ownDatasetGroup(@AuthenticationPrincipal Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONArray> response = null;
        try {
            response = restTemplate.exchange(urlSearch + "/api/v1/ownDatasetGroup/", HttpMethod.GET, entity, JSONArray.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONArray>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping("/ownDataset")
    private ResponseEntity<JSONArray> ownDataset(@AuthenticationPrincipal Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONArray> response = null;
        try {
            response = restTemplate.exchange(urlSearch + "/api/v1/ownDataset/", HttpMethod.GET, entity, JSONArray.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<JSONArray>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


}
