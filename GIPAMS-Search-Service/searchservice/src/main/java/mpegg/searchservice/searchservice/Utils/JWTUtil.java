package mpegg.searchservice.searchservice.Utils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.security.oauth2.jwt.Jwt;

public class JWTUtil {
    public String getUID(Jwt j) {
        return (String) j.getClaims().get("sub");
    }

    public JSONArray getRoles(Jwt j) {
        return (JSONArray)((JSONObject) j.getClaims().get("realm_access")).get("roles");
    }
}