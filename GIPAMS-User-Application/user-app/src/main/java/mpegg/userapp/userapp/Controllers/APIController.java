package mpegg.userapp.userapp.Controllers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.bouncycastle.math.raw.Mod;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class APIController {
    private final HttpServletRequest request;

    @Autowired
    private Environment env;

    @Value("${workflow.url}")
    private final String urlWorkflow = null;

    @Autowired
    public APIController(HttpServletRequest request) {
        this.request = request;
    }

    @PostMapping("/addFile")
    public ModelAndView addFile(@RequestParam("file_name") String file_name) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_name", file_name);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(urlWorkflow + "/api/v1/addFile", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            ModelAndView m = new ModelAndView("error");
            m.addObject("error", "Error creating file");
            return m;
        }
        ModelAndView m = new ModelAndView("addDatasetGroup");
        m.addObject("file_id", response.getBody());
        return m;
    }

    @PostMapping("/addDatasetGroup")
    public ModelAndView addDatasetGroup(@RequestPart("dg_md") MultipartFile dg_md, @RequestPart("dg_pr") MultipartFile dg_pr, @RequestPart("dt_md") MultipartFile[] dt_md, @RequestPart("file_id") String file_id) {
        ModelAndView m = new ModelAndView("addDatasetGroup");
        m.addObject("file_id",file_id);
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_id", file_id);
        body.add("dg_md", dg_md.getResource());
        body.add("dg_pr", dg_pr.getResource());
        for (MultipartFile file : dt_md) {
            body.add("dt_md",file.getResource());
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(urlWorkflow + "/api/v1/addDatasetGroup", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error adding new Dataset Group");
            return m;
        }
        m.addObject("file_id",file_id);
        m.addObject("success","Dataset Group added successfully");
        return m;
    }

    @PostMapping("/addDataset")
    public ModelAndView addDataset(@RequestPart(value = "dt_md",required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr", required = false) MultipartFile dt_pr, @RequestPart("dg_id") String dg_id) {
        ModelAndView m = new ModelAndView("addDataset");
        m.addObject("dg_id",dg_id);
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        body.add("dt_md", dt_md.getResource());
        body.add("dt_pr", dt_pr.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/addDataset", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error adding new Dataset");
            return m;
        }
        m.addObject("success","Dataset added successfully");
        return m;
    }

    @PostMapping("/editDatasetGroup")
    public ModelAndView editDatasetGroup(@RequestPart(value = "dg_md",required = false) MultipartFile dg_md, @RequestPart(value = "dg_pr", required = false) MultipartFile dg_pr, @RequestPart("dg_id") String dg_id, @RequestPart(value = "file_id",required = false) String file_id) {
        ModelAndView m = new ModelAndView("editDatasetGroup");
        m.addObject("file_id",file_id);
        m.addObject("dg_id",dg_id);
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        body.add("dg_md", dg_md.getResource());
        body.add("dg_pr", dg_pr.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/editDatasetGroup", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error editing Dataset Group");
            return m;
        }
        m.addObject("success","Dataset Group edited successfully");
        return m;
    }

    @PostMapping("/editDataset")
    public ModelAndView editDataset(@RequestPart(value = "dt_md",required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr", required = false) MultipartFile dt_pr, @RequestPart("dt_id") String dt_id, @RequestPart(value="dg_id",required = false) String dg_id) {
        ModelAndView m = new ModelAndView("editDataset");
        m.addObject("dg_id",dg_id);
        m.addObject("dt_id",dt_id);
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dt_id", dt_id);
        body.add("dt_md", dt_md.getResource());
        body.add("dt_pr", dt_pr.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/editDataset", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error editing Dataset Group");
            return m;
        }
        m.addObject("success","Dataset Group edited successfully");
        return m;
    }

    @PostMapping(value = "/dg/{dg_id}/{resource}",produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String getDatasetGroup(@PathVariable("dg_id") String dg_id, @PathVariable("resource") String resource) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+context.getTokenString());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        response = restTemplate.exchange(urlWorkflow + "/api/v1/dg/" + dg_id + "/" + resource, HttpMethod.GET, entity, JSONObject.class);
        return (String) response.getBody().get("data");
    }

    @PostMapping(value = "/dt/{dt_id}/{resource}",produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String getDataset(@PathVariable("dt_id") String dt_id, @PathVariable("resource") String resource) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+context.getTokenString());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        response = restTemplate.exchange(urlWorkflow + "/api/v1/dt/" + dt_id + "/" + resource, HttpMethod.GET, entity, JSONObject.class);
        return (String) response.getBody().get("data");
    }
    @PostMapping("searchDatasetGroup")
    @ResponseBody
    public ModelAndView searchDatasetGroup(@RequestParam(value = "center", required = false) String center,
                                     @RequestParam(value = "description", required = false) String description,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "type", required = false) String type) {
        ModelAndView model = new ModelAndView("searchDatasetGroupResult");
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("center", center);
        body.add("description", description);
        body.add("title", title);
        body.add("type", type);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        JSONArray response = restTemplate.exchange(urlWorkflow + "/api/v1/findDatasetGroupMetadata", HttpMethod.POST, requestEntity, JSONArray.class).getBody();
        model.addObject("dg",response);
        return model;
    }

    @PostMapping("/deleteFile")
    public ModelAndView deleteFile(@RequestParam("file_id") String file_id) {
        ModelAndView m = new ModelAndView("deleteResult");
        HttpHeaders headers = new HttpHeaders();
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_id", file_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/deleteFile", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error deleting file");
            return m;
        }
        m.addObject("success","File deleted successfully");
        return m;
    }

    @PostMapping("searchDataset")
    @ResponseBody
    public ModelAndView searchDataset(@RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "taxon_id", required = false) String taxon_id) {
        ModelAndView model = new ModelAndView("searchDatasetResult");
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", title);
        body.add("taxon_id", taxon_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        JSONArray response = restTemplate.exchange(urlWorkflow + "/api/v1/findDatasetMetadata", HttpMethod.POST, requestEntity, JSONArray.class).getBody();
        model.addObject("dt",response);
        return model;
    }
    @PostMapping("/deleteDatasetGroup")
    public ModelAndView deleteDatasetGroup(@RequestParam("dg_id") String dg_id) {
        ModelAndView m = new ModelAndView("deleteResult");
        HttpHeaders headers = new HttpHeaders();
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id", dg_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/deleteDatasetGroup", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error deleting Dataset Group");
            return m;
        }
        m.addObject("success","Dataset Group deleted successfully");
        return m;
    }

    @PostMapping("/deleteDataset")
    public ModelAndView deleteDataset(@RequestParam("dt_id") String dt_id) {
        ModelAndView m = new ModelAndView("deleteResult");
        HttpHeaders headers = new HttpHeaders();
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        headers.set("Authorization","Bearer "+context.getTokenString());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dt_id", dt_id);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWorkflow + "/api/v1/deleteDataset", HttpMethod.DELETE, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            m.addObject("error","Error deleting Dataset");
            return m;
        }
        m.addObject("success","Dataset deleted successfully");
        return m;
    }
}
