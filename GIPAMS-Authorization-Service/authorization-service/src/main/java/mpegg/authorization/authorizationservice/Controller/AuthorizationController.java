package mpegg.authorization.authorizationservice.Controller;

import mpegg.authorization.authorizationservice.Utils.FileUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@RestController
public class AuthorizationController {

    @PostMapping("/authorize_rule")
    ResponseEntity<String> authorize_rule(@RequestParam("request") String request, @RequestParam("rule") String rule) {
        FileUtil fu = new FileUtil();
        String policyLocation = null;
        String name = null;
        try {
            name = fu.randomFileName();
            policyLocation = (new File("src")).getCanonicalPath() + File.separator + "main" + File.separator + "resources" + File.separator + "support" + File.separator + "policy";
            policyLocation += File.separator + name;
            fu.createDirectory(policyLocation);
            fu.createFile(policyLocation + File.separator + "policy.xml", rule);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Server error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String result = null;
        try {
            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> modules = new HashSet<PolicyFinderModule>();
            Set<String> policySet = new HashSet<String>();
            policySet.add(policyLocation);
            modules.add(new FileBasedPolicyFinderModule(policySet));
            policyFinder.setModules(modules);
            PDPConfig pdpc = new PDPConfig(null, policyFinder, null);
            PDP pdp = new PDP(pdpc);
            result = pdp.evaluate(request);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            try {
                fu.deleteDirectory(policyLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<String>("Server error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            fu.deleteDirectory(policyLocation);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Server error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(result,HttpStatus.OK);
    }
}