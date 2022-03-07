package mpegg.userapp.userapp.Controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
public class AppController {
    private final HttpServletRequest request;
    @Value("${workflow.url}")
    private final String urlWorkflow = null;

    @Autowired
    public AppController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping(path = "/")
    public RedirectView index() {
        return new RedirectView("/home");
    }

    @GetMapping(path = "/home")
    public ModelAndView home() {
        ModelAndView model = new ModelAndView("home");
        KeycloakSecurityContext context = getKeycloakSecurityContext();
        model.addObject("username", context.getIdToken().getPreferredUsername());
        //model.addAttribute("roles", ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getToken().getRealmAccess().getRoles());
        return model;
    }

    @PostMapping(path = "/addDatasetGroup")
    public ModelAndView addDatasetGroup(@RequestParam("file_id") String file_id) {
        ModelAndView model = new ModelAndView("addDatasetGroup");
        KeycloakSecurityContext context = getKeycloakSecurityContext();
        model.addObject("file_id", file_id);
        return model;
    }

    @PostMapping(path = "/addDataset")
    public ModelAndView addDataset(@RequestParam("dg_id") String dg_id) {
        ModelAndView model = new ModelAndView("addDataset");
        model.addObject("dg_id", dg_id);
        return model;
    }

    @PostMapping(path = "/editDatasetGroup")
    public ModelAndView editDatasetGroup(@RequestParam("dg_id") String dg_id, @RequestParam(value="file_id",required = false) String file_id) {
        ModelAndView model = new ModelAndView("editDatasetGroup");
        model.addObject("dg_id", dg_id);
        model.addObject("file_id", file_id);
        return model;
    }

    @PostMapping(path = "/editDataset")
    public ModelAndView editDataset(@RequestParam("dt_id") String dt_id, @RequestParam(value="dg_id",required = false) String dg_id, @RequestParam(value="file_id",required = false) String file_id) {
        ModelAndView model = new ModelAndView("editDataset");
        model.addObject("file_id", file_id);
        model.addObject("dt_id", dt_id);
        model.addObject("dg_id", dg_id);
        return model;
    }

    @GetMapping(path = "/addFile")
    public ModelAndView addFile() {
        return new ModelAndView("addFile");
    }

    @GetMapping(path = "/ownFiles")
    public ModelAndView ownFiles() {
        ModelAndView model = new ModelAndView("ownFiles");
        KeycloakSecurityContext context = getKeycloakSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+context.getTokenString());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONArray> response = null;
        response = restTemplate.exchange(urlWorkflow+"/api/v1/ownFiles", HttpMethod.POST, entity, JSONArray.class);
        JSONArray a = response.getBody();
        model.addObject("files", a);
        return model;
    }

    @PostMapping(path = "/getFileData")
    public ModelAndView getFileData(@RequestParam("file_id") String file_id) {
        ModelAndView model = new ModelAndView("getFileData");
        KeycloakSecurityContext context = getKeycloakSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+context.getTokenString());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        response = restTemplate.exchange(urlWorkflow+"/api/v1/mpegfile/"+file_id, HttpMethod.GET, entity, JSONObject.class);
        model.addObject("file", response.getBody());
        return model;
    }

    @PostMapping(path = "/getDatasetGroupData")
    public ModelAndView getDatasetGroupData(@RequestParam("dg_id") String dg_id, @RequestParam(value = "file_id",required = false) String file_id) {
        ModelAndView model = new ModelAndView("getDatasetGroupData");
        KeycloakSecurityContext context = getKeycloakSecurityContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+context.getTokenString());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        response = restTemplate.exchange(urlWorkflow+"/api/v1/dg/"+dg_id+"/datasets", HttpMethod.GET, entity, JSONObject.class);
        model.addObject("dg", response.getBody());
        model.addObject("dg_id", dg_id);
        model.addObject("file_id", file_id);
        return model;
    }

    @GetMapping(path = "/searchDatasetGroup")
    public ModelAndView searchDatasetGroup() {
        return new ModelAndView("searchDatasetGroup");
    }

    @GetMapping(path = "/searchDataset")
    public ModelAndView searchDataset() {
        return new ModelAndView("searchDataset");
    }

    @GetMapping(path = "/logout")
    public RedirectView logout() throws ServletException {
        request.logout();
        return new RedirectView("/");
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }
}
