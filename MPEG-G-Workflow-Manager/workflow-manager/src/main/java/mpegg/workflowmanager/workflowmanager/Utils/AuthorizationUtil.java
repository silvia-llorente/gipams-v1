package mpegg.workflowmanager.workflowmanager.Utils;

import mpegg.workflowmanager.workflowmanager.Models.Dataset;
import mpegg.workflowmanager.workflowmanager.Models.DatasetGroup;
import mpegg.workflowmanager.workflowmanager.Models.MPEGFile;
import mpegg.workflowmanager.workflowmanager.Repositories.*;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Optional;

public class AuthorizationUtil {
    private final String authorizationURL = "http://authorization-service:8083";    
    private final String requestSample = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<Request xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"\n"+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
    "xsi:schemaLocation=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd\"\n"+
    "ReturnPolicyIdList=\"false\" CombinedDecision=\"false\">\n"+
    "<Attributes Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">\n"+
        "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:3.0:example:attribute:role\" IncludeInResult=\"true\">\n"+
            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">%s</AttributeValue>\n"+
        "</Attribute>\n"+
    "</Attributes>\n"+
    "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">\n"+
        "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"\n"+
    "IncludeInResult=\"true\">\n"+

            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">%s</AttributeValue>\n"+
        "</Attribute>\n"+
    "</Attributes>\n"+
    "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:date\">\n"+
        "<Attribute AttributeId=\"accessDate\" IncludeInResult=\"true\">\n"+
            "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#date\">%s</AttributeValue>\n"+
        "</Attribute>\n"+
    "</Attributes>\n"+
"</Request>";
    private JWTUtil jwtUtil = new JWTUtil();

    public boolean authorized(String baseURL, String resource, String id, Jwt jwt, String action, DatasetGroupRepository datasetGroupRepository, DatasetRepository datasetRepository, MPEGFileRepository mpegFileRepository) {
        if (resource.equals("dt")) {
            Long dtIdL = Long.parseLong(id);
            Optional<Dataset> dtOpt = datasetRepository.findById(dtIdL);
            if (dtOpt.isEmpty()) return false;
            Dataset dt = dtOpt.get();
            if (dt.getOwner().equals(jwtUtil.getUID(jwt))) return true;
            if (!dt.getProtection()) {
                resource = "dg";
                id = String.valueOf(dt.getDatasetGroup().getId());
            }
        }
        else if (resource.equals("dg")) {
            Long dgIdL = Long.parseLong(id);
            Optional<DatasetGroup> dgOpt = datasetGroupRepository.findById(dgIdL);
            if (dgOpt.isEmpty()) return false;
            DatasetGroup dg = dgOpt.get();
            if (dg.getOwner().equals(jwtUtil.getUID(jwt))) return true;
        }

        else if (resource.equals("file")) {
            Long fileIdL = Long.parseLong(id);
            Optional<MPEGFile> fileOpt = mpegFileRepository.findById(fileIdL);
            if (fileOpt.isEmpty()) return false;
            MPEGFile file = fileOpt.get();
            return file.getOwner().equals(jwtUtil.getUID(jwt));
        }

        else return false;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> response = null;
        response = restTemplate.exchange(baseURL+"/api/v1/"+resource+"/"+id+"/protection", HttpMethod.GET, entity, JSONObject.class);
        Boolean authorized = null;
        authorized = getAuthorization(action,jwt, String.valueOf(response.getBody().get("data")));
        return authorized;
    }

    private boolean getAuthorization(String action, Jwt jwt, String rules) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("request", getRequest(jwt,action));
        body.add("rule", rules);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        Boolean authorized = null;
        try {
            response = restTemplate.exchange(authorizationURL+"/authorize_rule", HttpMethod.POST, requestEntity, String.class);
            authorized = parseResponse(response.getBody());
        } catch (Exception e) {
            System.out.println(authorizationURL);
            return false;
        }
        return authorized;
    }

    private String getRequest(Jwt jwt, String action) {
        JSONObject a = (JSONObject) jwt.getClaims().get("realm_access");
        String role = ((ArrayList<String>) a.get("roles")).get(0);
        String date = "2020-01-01";
        return String.format(requestSample,role,action,date);
    }

    private boolean parseResponse(String response) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(response));
        Document d = builder.parse(is);
        String decision = d.getDocumentElement().getElementsByTagName("Decision").item(0).getTextContent();
        if (decision.equals("Permit")) return true;
        return false;
    }
}
